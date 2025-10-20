package nl.han.ica.icss.parser;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}
    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx){
        Stylesheet stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx){
        Stylesheet stylesheet = (Stylesheet)currentContainer.pop();
        ast.setRoot(stylesheet);
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx){
        Stylerule stylerule = (Stylerule)currentContainer.pop();
        currentContainer.peek().addChild(stylerule);
    }

    @Override
    public void enterVariable_assignment(ICSSParser.Variable_assignmentContext ctx) {
VariableAssignment variableAssignment = new VariableAssignment();
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariable_assignment(ICSSParser.Variable_assignmentContext ctx) {
        VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    @Override
    public void enterVariable_reference(ICSSParser.Variable_referenceContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariable_reference(ICSSParser.Variable_referenceContext ctx) {
        VariableReference variableReference = (VariableReference)currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
    }

    @Override
    public void enterId_selector(ICSSParser.Id_selectorContext ctx){
        IdSelector selector = new IdSelector(ctx.getText());
        currentContainer.push(selector);
    }

    @Override
    public void exitId_selector(ICSSParser.Id_selectorContext ctx){
        IdSelector selector = (IdSelector)currentContainer.pop();
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void enterClass_selector(ICSSParser.Class_selectorContext ctx) {
        ClassSelector selector = new ClassSelector(ctx.getText());
        currentContainer.push(selector);
    }

    @Override
    public void exitClass_selector(ICSSParser.Class_selectorContext ctx){
        ClassSelector selector = (ClassSelector)currentContainer.pop();
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void enterTag_selector(ICSSParser.Tag_selectorContext ctx) {
        TagSelector selector = new TagSelector(ctx.getText());
        currentContainer.push(selector);
    }

    @Override
    public void exitTag_selector(ICSSParser.Tag_selectorContext ctx){
        TagSelector selector = (TagSelector)currentContainer.pop();
        currentContainer.peek().addChild(selector);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = (Declaration)currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }

    @Override
    public void enterProperty(ICSSParser.PropertyContext ctx) {
        PropertyName property = new PropertyName(ctx.getText());
        currentContainer.push(property);
    }

    @Override
    public void exitProperty(ICSSParser.PropertyContext ctx) {
        PropertyName property = (PropertyName)currentContainer.pop();
        currentContainer.peek().addChild(property);
    }

    @Override
    public void enterColor_literal(ICSSParser.Color_literalContext ctx) {
        ColorLiteral colorLiteral = new ColorLiteral(ctx.getText());
        currentContainer.push(colorLiteral);
    }

    @Override
    public void exitColor_literal(ICSSParser.Color_literalContext ctx) {
        ColorLiteral colorLiteral = (ColorLiteral)currentContainer.pop();
        currentContainer.peek().addChild(colorLiteral);
    }

    @Override
    public void enterPixel_literal(ICSSParser.Pixel_literalContext ctx) {
        PixelLiteral pixelLiteral = new PixelLiteral(ctx.getText());
        currentContainer.push(pixelLiteral);
    }

    @Override
    public void exitPixel_literal(ICSSParser.Pixel_literalContext ctx) {
        PixelLiteral pixelLiteral = (PixelLiteral)currentContainer.pop();
        currentContainer.peek().addChild(pixelLiteral);
    }

    @Override
    public void enterPercentage_literal(ICSSParser.Percentage_literalContext ctx) {
        PercentageLiteral percentageLiteral = new PercentageLiteral(ctx.getText());
        currentContainer.push(percentageLiteral);
    }

    @Override
    public void exitPercentage_literal(ICSSParser.Percentage_literalContext ctx) {
        PercentageLiteral percentageLiteral = (PercentageLiteral)currentContainer.pop();
        currentContainer.peek().addChild(percentageLiteral);
    }

    @Override
    public void enterBool_literal(ICSSParser.Bool_literalContext ctx) {
        BoolLiteral boolLiteral = new BoolLiteral(ctx.getText());
        currentContainer.push(boolLiteral);
    }

    @Override
    public void exitBool_literal(ICSSParser.Bool_literalContext ctx) {
        BoolLiteral boolLiteral = (BoolLiteral)currentContainer.pop();
        currentContainer.peek().addChild(boolLiteral);
    }

    @Override
    public void enterScalar_literal(ICSSParser.Scalar_literalContext ctx) {
        ScalarLiteral scalarLiteral = new ScalarLiteral(ctx.getText());
        currentContainer.push(scalarLiteral);
    }

    @Override
    public void exitScalar_literal(ICSSParser.Scalar_literalContext ctx) {
        ScalarLiteral scalarLiteral = (ScalarLiteral)currentContainer.pop();
        currentContainer.peek().addChild(scalarLiteral);
    }

    @Override
    public void enterAdd_operation(ICSSParser.Add_operationContext ctx) {
        AddOperation addOperation = new AddOperation();
        currentContainer.push(addOperation);
    }

    @Override
    public void exitAdd_operation(ICSSParser.Add_operationContext ctx) {
        AddOperation addOperation = (AddOperation)currentContainer.pop();
        currentContainer.peek().addChild(addOperation);
    }

    @Override
    public void enterMultiply_operation(ICSSParser.Multiply_operationContext ctx) {
        MultiplyOperation multiplyOperation = new MultiplyOperation();
        currentContainer.push(multiplyOperation);
    }

    @Override
    public void exitMultiply_operation(ICSSParser.Multiply_operationContext ctx) {
        MultiplyOperation multiplyOperation = (MultiplyOperation)currentContainer.pop();
        currentContainer.peek().addChild(multiplyOperation);
    }

    @Override
    public void enterSubtract_operation(ICSSParser.Subtract_operationContext ctx) {
        SubtractOperation subtractOperation = new SubtractOperation();
        currentContainer.push(subtractOperation);
    }

    @Override
    public void exitSubtract_operation(ICSSParser.Subtract_operationContext ctx) {
        SubtractOperation subtractOperation = (SubtractOperation)currentContainer.pop();
        currentContainer.peek().addChild(subtractOperation);
    }

    @Override
    public void enterIf_clause(ICSSParser.If_clauseContext ctx) {
        IfClause ifClause = new IfClause();
        currentContainer.push(ifClause);
    }

    @Override
    public void exitIf_clause(ICSSParser.If_clauseContext ctx) {
        IfClause ifClause = (IfClause)currentContainer.pop();
        currentContainer.peek().addChild(ifClause);
    }

    @Override
    public void enterElse_clause(ICSSParser.Else_clauseContext ctx) {
        ElseClause elseClause = new ElseClause();
        currentContainer.push(elseClause);
    }

    @Override
    public void exitElse_clause(ICSSParser.Else_clauseContext ctx) {
        ElseClause elseClause = (ElseClause)currentContainer.pop();
        currentContainer.peek().addChild(elseClause);
    }
}