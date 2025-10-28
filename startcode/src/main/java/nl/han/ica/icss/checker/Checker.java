package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;
import java.util.LinkedList;

public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        variableTypes.push(new HashMap<>());
        checkStylesheet(ast.root);
        variableTypes.pop();
    }

    private void checkStylesheet(Stylesheet sheet) {
        for (ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkStylerule(Stylerule rule) {
        checkNextScope(rule);
    }

    private void checkIfClause(IfClause ifClause) {
        checkNextScope(ifClause);

        ExpressionType expressionType = checkExpressionType(ifClause.conditionalExpression);
        if (expressionType != ExpressionType.BOOL) {
            ifClause.setError("If condition must be of type BOOL. Found: " + expressionType);
        }
    }

    private void checkNextScope(ASTNode node) {
        variableTypes.push(new HashMap<>());

        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                checkIfClause((IfClause) child);
            }
        }

        variableTypes.pop();
    }


    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        String name = variableAssignment.name.name;
        ExpressionType type = checkExpressionType(variableAssignment.expression);
        if (name != null) {
            variableTypes.peek().put(name, type);
        }
    }


    private ExpressionType getVariableType(String name) {
        for (HashMap<String, ExpressionType> scope : variableTypes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    private ExpressionType checkExpressionType(ASTNode node) {
        if (node instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        } else if (node instanceof BoolLiteral){
            return ExpressionType.BOOL;
        } else if (node instanceof PercentageLiteral){
            return ExpressionType.PERCENTAGE;
        } else if (node instanceof PixelLiteral){
            return ExpressionType.PIXEL;
        } else if (node instanceof ScalarLiteral){
            return ExpressionType.SCALAR;
        } else if (node instanceof VariableReference) {
            String varName = ((VariableReference) node).name;
            ExpressionType type = getVariableType(varName);
            if (type == null) {
                node.setError("Variable '" + varName + "' is not defined in this scope");
            }
            return type;
        } else if (node instanceof Operation) {
            return checkOperationType(node);
        }
        return null;
    }

    private boolean checkColor(ExpressionType type) {
        return ExpressionType.COLOR.equals(type);
    }

    private void checkDeclaration(Declaration declaration) {
        ExpressionType expressionType = checkExpressionType(declaration.expression);
        boolean isColor = checkColor(expressionType);

        switch (declaration.property.name) {
            case "width":
            case "height":
                if (isColor) {
                    declaration.setError("Property '" + declaration.property.name + "': color not allowed");
                }
                break;
            case "color":
            case "background-color":
                if (expressionType != ExpressionType.COLOR) {
                    declaration.setError("Property '" + declaration.property.name + "': only colors allowed");
                }
                break;
            default:
                declaration.setError("Unknown property: " + declaration.property.name);
                break;
        }
    }


    private ExpressionType checkOperationType(ASTNode node) {
        if (!(node instanceof Operation)) return null;

        if (node.getChildren().size() != 2) {
            node.setError("Operation must have exactly two literals.");
            return null;
        }

        ASTNode leftNode = node.getChildren().get(0);
        ASTNode rightNode = node.getChildren().get(1);

        ExpressionType leftType = checkExpressionType(leftNode);
        ExpressionType rightType = checkExpressionType(rightNode);

        if (leftType == null || rightType == null) {
            return null;
        }

        if (node instanceof AddOperation || node instanceof SubtractOperation) {
            if (leftType != rightType) {
                node.setError("Literals of + or - must be of the same type. Found: "
                        + leftType + " and " + rightType);
                return null;
            }
            return leftType;
        }

        if (node instanceof MultiplyOperation) {
            boolean leftScalar = leftType == ExpressionType.SCALAR;
            boolean rightScalar = rightType == ExpressionType.SCALAR;

            if (leftScalar && !rightScalar){
                return rightType;
            }
            if (!leftScalar && rightScalar){
                return leftType;
            }
            if (leftScalar && rightScalar){
                return ExpressionType.SCALAR;
            }

            node.setError("One literal of * must be a scalar. Found: " + leftType + " * " + rightType);
            return null;
        }

        node.setError("Unknown operation type: " + node.getClass().getSimpleName());
        return null;
    }
}
