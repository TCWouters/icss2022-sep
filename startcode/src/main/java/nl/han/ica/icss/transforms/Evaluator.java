package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.HashMap;
import java.util.LinkedList;

public class Evaluator implements Transform {

    private LinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        //variableValues = new LinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        //variableValues = new LinkedList<>();
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        applyStylerule((Stylerule) node.getChildren().get(0));

    }

    private void applyStylerule(Stylerule rule) {
        for(ASTNode child : rule.getChildren()) {
            if(child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            }
        }
    }

    private void applyDeclaration(Declaration node) {
        node.expression = evaluateExpression(node.expression);
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
            case "BooleanLiteral":
                return (BoolLiteral) expression;
            case "colorLiteral":
                return (ColorLiteral) expression;
            default:
                return null;
        }
    }

    private Literal evalMultiplyOperation(MultiplyOperation expression) {
        if(evaluateExpression(expression.lhs) instanceof ScalarLiteral | evaluateExpression(expression.rhs) instanceof ScalarLiteral) {
            ScalarLiteral left = (ScalarLiteral) evaluateExpression(expression.lhs);
            PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
            return new PixelLiteral(left.value * right.value);
        }
        return null;
    }

    private Literal evalSubtractOperation(SubtractOperation expression) {
        return null;
    }

    private Literal evalAddOperation(AddOperation expression) {
        PixelLiteral left = (PixelLiteral) evaluateExpression(expression.lhs);
        PixelLiteral right = (PixelLiteral) evaluateExpression(expression.rhs);
        return new PixelLiteral(left.value + right.value);
    }


}
