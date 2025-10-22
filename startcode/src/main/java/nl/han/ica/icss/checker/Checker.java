package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
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
        for (ASTNode child : rule.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        String name = null;
        ExpressionType type = null;

        for (ASTNode child : variableAssignment.getChildren()) {
            if (child instanceof VariableReference) {
                name = ((VariableReference) child).name;
            } else {
                type = inferExpressionType(child);
            }
        }

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

    private ExpressionType inferExpressionType(ASTNode node) {
        if (node instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        }
        if (node instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        }
        if (node instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        }
        if (node instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        }
        if (node instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        }
        if (node instanceof VariableReference) {
            return getVariableType(((VariableReference) node).name);
        }
        if (node instanceof AddOperation) {
            ExpressionType first = null;
            for (ASTNode child : node.getChildren()) {
                ExpressionType t = inferExpressionType(child);
                if (t == null) return null;
                if (first == null) first = t;
                else if (first != t) return null;
            }
            return first;
        }
        return null;
    }

    private boolean checkColor(ExpressionType type) {
        return ExpressionType.COLOR.equals(type);
    }

    private void checkDeclaration(Declaration declaration) {
        ExpressionType expressionType = inferExpressionType(declaration.expression);
        boolean isColor = checkColor(expressionType);

        switch (declaration.property.name) {
            case "width":
                if (isColor) {
                    declaration.setError("Property 'width': color not allowed");
                }
                break;
            case "color":
                if (expressionType != ExpressionType.COLOR) {
                    declaration.setError("Property 'color': only colors allowed");
                }
                break;
            case "background-color":
                if (expressionType != ExpressionType.COLOR) {
                    declaration.setError("Property 'background-color': only colors allowed");
                }
                break;
            case "height":
                if (isColor) {
                    declaration.setError("Property 'height': color not allowed");
                }
                break;
            default:
                declaration.setError("Unknown property: " + declaration.property.name);
                break;
        }
    }
}
