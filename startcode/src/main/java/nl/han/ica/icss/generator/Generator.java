package nl.han.ica.icss.generator;

import nl.han.ica.icss.ast.*;

public class Generator {

    public String generate(AST ast) {
        return generateStylesheet(ast.root);
    }

    private String generateStylesheet(Stylesheet sheet) {
        StringBuilder sb = new StringBuilder();
        for (ASTNode node : sheet.getChildren()) {
            if (node instanceof Stylerule) {
                sb.append(generateStylerule((Stylerule) node));
            }
        }
        return sb.toString();
    }

    private String generateStylerule(Stylerule node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.selectors.get(0).toString()).append(" {\n");
        for (ASTNode child : node.body) {
            if (child instanceof Declaration) {
                sb.append(generateDeclaration((Declaration) child));
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String generateDeclaration(Declaration declaration) {
        String value = "";

        if (declaration.expression instanceof nl.han.ica.icss.ast.literals.ColorLiteral) {
            value = ((nl.han.ica.icss.ast.literals.ColorLiteral) declaration.expression).value;
        } else if (declaration.expression instanceof nl.han.ica.icss.ast.literals.PixelLiteral) {
            value = ((nl.han.ica.icss.ast.literals.PixelLiteral) declaration.expression).value + "px";
        } else if (declaration.expression instanceof nl.han.ica.icss.ast.literals.PercentageLiteral) {
            value = ((nl.han.ica.icss.ast.literals.PercentageLiteral) declaration.expression).value + "%";
        } else {
            value = declaration.expression.toString();
        }

        return "  " + declaration.property.name + ": " + value + ";\n";
    }

}
