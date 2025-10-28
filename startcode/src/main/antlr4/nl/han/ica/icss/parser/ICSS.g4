grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';


//--- ADDED LEXER ---
LEFT_PAREN: '(';
RIGHT_PAREN: ')';

//--- PARSER: ---
stylesheet: (variable_assignment | stylerule)*;
stylerule: selector OPEN_BRACE (variable_assignment | declaration | if_clause)* CLOSE_BRACE;

// declaration
declaration: property COLON expression SEMICOLON;
property: LOWER_IDENT;


// variables
variable_assignment: variable_reference (ASSIGNMENT_OPERATOR expression)? SEMICOLON;
variable_reference: CAPITAL_IDENT;

// selectors
selector: tag_selector | class_selector | id_selector;
class_selector: CLASS_IDENT;
id_selector: ID_IDENT;
tag_selector: LOWER_IDENT;

// Expressions
expression: expression MUL expression #multiply_operation| expression PLUS expression #add_operation
            | expression MIN expression #subtract_operation | literal #literal_operation
             | variable_reference #variable_reference_operation |  LEFT_PAREN expression RIGHT_PAREN #parentheses_operation;

// literals
literal: bool_literal | color_literal | percentage_literal | scalar_literal | pixel_literal;
bool_literal: TRUE | FALSE;
color_literal: COLOR;
percentage_literal: PERCENTAGE;
scalar_literal: SCALAR;
pixel_literal: PIXELSIZE;

// If statement
if_clause: IF BOX_BRACKET_OPEN variable_reference BOX_BRACKET_CLOSE OPEN_BRACE (declaration | if_clause)* CLOSE_BRACE (else_clause)?;

else_clause: ELSE OPEN_BRACE (declaration | if_clause)* CLOSE_BRACE;