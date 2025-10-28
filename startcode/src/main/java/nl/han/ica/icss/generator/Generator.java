package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

    public String generate(AST ast) {
        return generateStylesheet(ast.root);
    }


    private String generateStylesheet(Stylesheet sheet) {
        String result = "";
        for (Object child : sheet.getChildren()) {
            if (child instanceof Stylerule) {
                result += generateStylerule((Stylerule) child);
            }
        }
        return result;
    }

    private String generateStylerule(Stylerule node) {
        String result = "";
        
        for (int i = 0; i < node.selectors.size(); i++) {
            result += node.selectors.get(i).toString();
        }

        result += " {\n";

        for (Object bodyNode : node.body) {
            if (bodyNode instanceof Declaration) {
                result += generateDeclaration((Declaration) bodyNode);
            }
        }

        result += "}\n";
        return result;
    }


    private String generateDeclaration(Declaration declaration) {
        String value = "";

        if (declaration.expression instanceof ColorLiteral) {
            value = ((ColorLiteral) declaration.expression).value;
        } else if (declaration.expression instanceof PixelLiteral) {
            value = ((PixelLiteral) declaration.expression).value + "px";
        } else if (declaration.expression instanceof PercentageLiteral) {
            value = ((PercentageLiteral) declaration.expression).value + "%";
        } else {
            value = declaration.expression.toString();
        }

        return "  " + declaration.property.name + ": " + value + ";\n";
    }

}
