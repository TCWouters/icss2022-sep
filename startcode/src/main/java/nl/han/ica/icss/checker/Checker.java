package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class Checker {

    private LinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        variableTypes = new LinkedList<>();
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet sheet) {
        for(ASTNode child : sheet.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) sheet.getChildren().get(0));
            } else if(child instanceof Stylerule) {
                Checkstylerule((Stylerule) sheet.getChildren().get(0));
            }
        }
    }

    private void Checkstylerule(Stylerule rule) {
        for(ASTNode child : rule.getChildren()) {
            if(child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        for(ASTNode child : variableAssignment.getChildren()) {
            if(child instanceof VariableReference) {
                checkVariableReference((VariableReference) child);
            }
        }
    }


    private String checkVariableReference(VariableReference reference) {
        return reference.name;
    }


    private void checkDeclaration(Declaration declaration) {
        switch (declaration.property.name) {
            case "width":
                if (declaration.expression instanceof ColorLiteral) {
                    declaration.setError("Property 'width': color not allowed");
                }
                break;
            case "color":
                if (!(declaration.expression instanceof ColorLiteral)) {
                    declaration.setError("Property 'color': only colors allowed");
                }
                break;
            case "background-color":
                if (!(declaration.expression instanceof ColorLiteral)) {
                    declaration.setError("Property 'background-color': only colors allowed");
                }
                break;
            case "height":
                if (declaration.expression instanceof ColorLiteral) {
                    declaration.setError("Property 'height': color not allowed");
                }
                break;
            default:
                declaration.setError("Unknown property: " + declaration.property.name);
                break;
        }
    }


}
