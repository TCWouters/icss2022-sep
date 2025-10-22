package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.AST;
import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.Stylerule;
import nl.han.ica.icss.ast.Stylesheet;

public class Generator {

	public String generate(AST ast) {
        return generateStylesheet(ast.root);
	}

    private String generateStylesheet(Stylesheet sheet) {
        return generateStylerule((Stylerule) sheet.getChildren().get(0));
    }

    private String generateStylerule(Stylerule node) {
        return node.selectors.get(0).toString() + "{\n" + generateDeclarions((Declaration) node.body.get(0)) + "}\n";
    }

    private String generateDeclarions(Declaration declaration) {
        return "  " + declaration.property.name + ": " + declaration.expression.toString() + ";\n";
    }


}
