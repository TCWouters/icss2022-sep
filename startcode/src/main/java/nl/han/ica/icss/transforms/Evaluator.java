package nl.han.ica.icss.transforms;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new LinkedList<>();
        variableValues.add(new HashMap<>());
    }

    @Override
    public void apply(AST ast) {
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                VariableAssignment assignment = (VariableAssignment) child;
                Literal value = evaluateExpression(assignment.expression);
                variableValues.peek().put(assignment.name.name, value);
            } else if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyStylerule(Stylerule rule) {
        ArrayList<ASTNode> newBody = new ArrayList<>();
        for (ASTNode child : rule.body) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
                newBody.add(child);
            } else if (child instanceof IfClause) {
                evalIfClause((IfClause) child, newBody);
            } else {
                newBody.add(child);
            }
        }
        rule.body = newBody;
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpression(node.expression);
    }

    private Literal getVariableValue(String name) {
        for (HashMap<String, Literal> scope : variableValues) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    private void evalIfClause(IfClause ifClause, ArrayList<ASTNode> newBody) {
        Literal condition = evaluateExpression(ifClause.conditionalExpression);

        if (condition instanceof BoolLiteral) {
            BoolLiteral boolCondition = (BoolLiteral) condition;

            if (boolCondition.value) {
                for (ASTNode child : ifClause.body) {
                    if (child instanceof Declaration) {
                        applyDeclaration((Declaration) child);
                        newBody.add(child);
                    } else if (child instanceof IfClause) {
                        evalIfClause((IfClause) child, newBody);
                    } else {
                        newBody.add(child);
                    }
                }
            } else if (ifClause.elseClause != null) {
                    for (ASTNode child : ifClause.elseClause.body) {
                        if (child instanceof Declaration) {
                            applyDeclaration((Declaration) child);
                            newBody.add(child);
                        } else if (child instanceof IfClause) {
                            evalIfClause((IfClause) child, newBody);
                        } else {
                            newBody.add(child);
                        }
                    }
            }
        }
    }

    private Literal evaluateExpression(Expression expression) {

        switch (expression.getClass().getSimpleName()) {
            case "PixelLiteral":
                return (PixelLiteral) expression;
            case "AddOperation":
                return evalAddOperation((AddOperation) expression);
            case "SubtractOperation":
                return evalSubtractOperation((SubtractOperation) expression);
            case "MultiplyOperation":
                return evalMultiplyOperation((MultiplyOperation) expression);
            case "PercentageLiteral":
                return (PercentageLiteral) expression;
            case "ScalarLiteral":
                return (ScalarLiteral) expression;
            case "BoolLiteral":
                return (BoolLiteral) expression;
            case "ColorLiteral":
                return (ColorLiteral) expression;
            case "VariableReference":
                return getVariableValue(((VariableReference) expression).name);
            default:
                return null;
        }
    }

    private Literal evalSubtractOperation(SubtractOperation expression) {
        Literal leftLit = evaluateExpression(expression.lhs);
        Literal rightLit = evaluateExpression(expression.rhs);

        if (leftLit instanceof PixelLiteral && rightLit instanceof PixelLiteral){
            return subtractPixels(leftLit, rightLit);
        } else if (leftLit instanceof PercentageLiteral && rightLit instanceof PercentageLiteral){
            return subtractPercentages(leftLit, rightLit);
        }

        return null;
    }

    private Literal evalMultiplyOperation(MultiplyOperation expression) {

        Literal leftLit = evaluateExpression(expression.lhs);
        Literal rightLit = evaluateExpression(expression.rhs);


        if (leftLit instanceof ScalarLiteral && rightLit instanceof PixelLiteral) {
            return multiplyPixels(rightLit, leftLit);
        } else if (leftLit instanceof PixelLiteral && rightLit instanceof ScalarLiteral) {
            return multiplyPixels(leftLit, rightLit);
        } else if (leftLit instanceof PercentageLiteral && rightLit instanceof ScalarLiteral) {
            return multiplyPercentages(leftLit, rightLit);
        } else if (leftLit instanceof ScalarLiteral && rightLit instanceof PercentageLiteral) {
            return multiplyPercentages(rightLit, leftLit);
        }
        return null;
    }

    private Literal evalAddOperation(AddOperation expression) {
        Literal leftLit = evaluateExpression(expression.lhs);
        Literal rightLit = evaluateExpression(expression.rhs);

        if (leftLit instanceof PixelLiteral && rightLit instanceof PixelLiteral){
            return addPixels(leftLit, rightLit);
        } else if (leftLit instanceof PercentageLiteral && rightLit instanceof PercentageLiteral){
            return addPercentages(leftLit, rightLit);
        }

        return null;
    }

    private Literal multiplyPixels(Literal leftLit, Literal rightLit) {
        PixelLiteral pixel = (PixelLiteral) leftLit;
        ScalarLiteral scalar = (ScalarLiteral) rightLit;
        return new PixelLiteral(pixel.value * scalar.value);
    }

    private Literal multiplyPercentages(Literal leftLit, Literal rightLit) {
        PercentageLiteral percentage = (PercentageLiteral) leftLit;
        ScalarLiteral scalar = (ScalarLiteral) rightLit;
        return new PercentageLiteral(percentage.value * scalar.value);
    }

    private Literal addPercentages(Literal leftLit, Literal rightLit) {
        PercentageLiteral leftPercentage = (PercentageLiteral) leftLit;
        PercentageLiteral rightPercentage = (PercentageLiteral) rightLit;
        return new PercentageLiteral(leftPercentage.value + rightPercentage.value);
    }

    private Literal addPixels(Literal leftLit, Literal rightLit) {
        PixelLiteral leftPixel = (PixelLiteral) leftLit;
        PixelLiteral rightPixel = (PixelLiteral) rightLit;
        return new PixelLiteral(leftPixel.value + rightPixel.value);
    }

    private Literal subtractPixels(Literal leftLit, Literal rightLit) {
        PixelLiteral leftPixel = (PixelLiteral) leftLit;
        PixelLiteral rightPixel = (PixelLiteral) rightLit;
        return new PixelLiteral(leftPixel.value - rightPixel.value);
    }

    private Literal subtractPercentages(Literal leftLit, Literal rightLit) {
        PercentageLiteral leftPercentage = (PercentageLiteral) leftLit;
        PercentageLiteral rightPercentage = (PercentageLiteral) rightLit;
        return new PercentageLiteral(leftPercentage.value - rightPercentage.value);
    }
}
