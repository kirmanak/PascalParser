package ru.ifmo.compilers;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs the syntax analysis
 */
@RequiredArgsConstructor
class Parser {
    /**
     * The list of lexemes from {@link Lexer}
     */
    @NonNull
    private final List<Lexeme> lexemes;

    /**
     * The list of errors found during analysis
     */
    @Getter
    private final List<String> errorMessages = new ArrayList<>();

    /**
     * The root node of the AST
     */
    @Getter
    private OutputTreeNode<Lexeme> root = new OutputTreeNode<>("\nAST");

    /**
     * Index of last accessed lexeme in lexemes list
     */
    private int index = -1;

    /**
     * Starts the analysis
     *
     * @return true if program is correct, false otherwise
     * @throws IllegalStateException if program has been analysed already
     */
    boolean parseProgram() {
        if (root.hasChildren() || index > -1)
            throw new IllegalStateException("AST was already parsed!");

        return parseVariablesDeclaration(root, true)
                && parseComputations(root, true);
    }

    /**
     * Parses "Computations" = "Begin *list of operators* End."
     *
     * @param parent            the parent node to add found AST nodes
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse computations, false otherwise
     */
    private boolean parseComputations(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (!checkNextLexeme(LexemeClass.Keyword, "Begin", isLastAlternative))
            return false;

        if (!parseOperatorsList(parent, isLastAlternative))
            return false;

        return checkNextLexeme(LexemeClass.Keyword, "End.", isLastAlternative);
    }

    /**
     * Parses list of operators as defined in {@link Parser#parseOperator}
     *
     * @param parent            parent node to assign found operators
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse at least one operator, false otherwise
     */
    private boolean parseOperatorsList(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (parseOperator(parent, isLastAlternative)) {
            parseOperatorsList(parent, false);
            return true;
        }

        return false;
    }

    /**
     * Parses operator: either assignment, complex operator or compound operator
     *
     * @param parent            parent node to assign found operator
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse an operator, false otherwise
     */
    private boolean parseOperator(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        return parseAssignment(parent, false)
                || parseComplexOperator(parent, false)
                || parseCompoundOperator(parent, isLastAlternative);
    }

    /**
     * Parses a compound operator = "Begin *list of operators* End"
     *
     * @param parent            parent node to assign found operator
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseCompoundOperator(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (!checkNextLexeme(LexemeClass.Keyword, "Begin", isLastAlternative))
            return false;

        if (!parseOperatorsList(parent, isLastAlternative))
            return false;

        return checkNextLexeme(LexemeClass.Keyword, "End", isLastAlternative);

    }

    /**
     * Parses complex operator: either a loop or compound operator
     *
     * @param parent            parent node to assign found operator
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseComplexOperator(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        return parseLoopOperator(parent, false)
                || parseCompoundOperator(parent, isLastAlternative);
    }

    /**
     * Parses loop operator: "WHILE *expression* DO *operator* "
     *
     * @param parent            parent node to assign found operator
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseLoopOperator(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        OutputTreeNode<Lexeme> whileNode;
        if (checkNextLexeme(LexemeClass.Keyword, "WHILE", isLastAlternative))
            whileNode = addLexeme(parent);
        else
            return false;

        if (!parseExpression(whileNode, true))
            return false;

        OutputTreeNode<Lexeme> doNode;
        if (checkNextLexeme(LexemeClass.Keyword, "DO", true))
            doNode = addLexeme(whileNode);
        else
            return false;

        return parseOperator(doNode, true);
    }

    /**
     * Parses an assignment = "*Ident* := *expression* "
     *
     * @param parent            parent node to assign found assignment
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseAssignment(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        Lexeme toBeAssigned;
        if (checkNextLexeme(LexemeClass.Ident, null, isLastAlternative))
            toBeAssigned = lexemes.get(index);
        else
            return false;

        OutputTreeNode<Lexeme> assignmentOp;
        if (checkNextLexeme(LexemeClass.AssignmentOperator, null, true))
            assignmentOp = addLexeme(parent);
        else
            return false;

        assignmentOp.addChild(toBeAssigned);

        if (!parseExpression(assignmentOp, true))
            return false;

        return checkNextLexeme(LexemeClass.Separator, ";", true);
    }

    /**
     * Parses an expression = "*unary operator* *subexpression*"
     * where unary operator is optional
     *
     * @param parent            parent node to assign found expression
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseExpression(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        parseUnaryOperation(parent, false);
        return parseSubExpression(parent, isLastAlternative);
    }

    /**
     * Parses sub expression = "( *Expression* )" or " *Operand* "
     * or " *Operand* *Binary operator* *Expression * "
     *
     * @param parent            parent node to assign found subexpression
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseSubExpression(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Separator, "(", false)) {
            if (!parseExpression(parent, true))
                return false;

            return checkNextLexeme(LexemeClass.Separator, ")", true);
        }

        OutputTreeNode<Lexeme> operation = new OutputTreeNode<>("Dummy");
        if (parseOperand(operation, isLastAlternative)) {
            var store = new NodeStore();

            if (parseBinaryOperator(parent, false, store)) {
                store.getNode().addChildren(operation.getChildren());

                return parseSubExpression(store.getNode(), true);
            } else {
                parent.addChildren(operation.getChildren());

                return true;
            }
        }

        return false;
    }

    /**
     * Parses binary operator: either {@link LexemeClass#ArithmeticOperator}
     * or {@link LexemeClass#ComparisonOperator}
     *
     * @param parent            parent node to assign found operator
     * @param isLastAlternative if true, adds errors to error list
     * @param store             where the created node will be stored
     * @return true if managed to parse, false otherwise
     */
    private boolean parseBinaryOperator(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative,
                                        @NonNull NodeStore store) {
        if (checkNextLexeme(LexemeClass.ArithmeticOperator, null, false)) {
            store.setNode(addLexeme(parent));
            return true;
        }

        if (checkNextLexeme(LexemeClass.ComparisonOperator, null, isLastAlternative)) {
            store.setNode(addLexeme(parent));
            return true;
        }

        return false;
    }

    /**
     * Parses unary operator: {@link LexemeClass#ArithmeticOperator} where sign is "-"
     *
     * @param parent            parent node to assign found operator
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseUnaryOperation(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.ArithmeticOperator, "-", isLastAlternative)) {
            addLexeme(parent);
            return true;
        }

        return false;
    }

    /**
     * Parses an operand: either {@link LexemeClass#Ident} or {@link LexemeClass#Const}
     *
     * @param parent            parent node to assign found operand
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseOperand(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Ident, null, false)) {
            addLexeme(parent);
            return true;
        }

        if (checkNextLexeme(LexemeClass.Const, null, isLastAlternative))
            addLexeme(parent);
        else
            return false;

        return true;
    }

    /**
     * Parses the declaration of variables: "Var *list of variables*"
     *
     * @param parent            parent node to assign found variables list
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse, false otherwise
     */
    private boolean parseVariablesDeclaration(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Keyword, "Var", isLastAlternative))
            return parseVariablesList(addLexeme(parent), isLastAlternative);
        else
            return false;
    }

    /**
     * Parses list of variables: "{@link LexemeClass#Ident}" or "{@link LexemeClass#Ident} ; *list of variables*"
     * or "{@link LexemeClass#Ident}, *list of variables*"
     *
     * @param parent            parent node to assign found variables list
     * @param isLastAlternative if true, adds errors to error list
     * @return true if managed to parse at least one variable, false otherwise
     */
    private boolean parseVariablesList(@NonNull OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Ident, null, isLastAlternative))
            addLexeme(parent);
        else
            return false;

        if (checkNextLexeme(LexemeClass.Separator, ";", false)) {
            parseVariablesList(parent, false);
            return true;
        }

        if (checkNextLexeme(LexemeClass.Separator, ",", isLastAlternative))
            return parseVariablesList(parent, isLastAlternative);

        return false;
    }

    /**
     * Checks whether the next lexeme in the list is of {@param lexemeClass}
     * and its sign is {@param sign}. If {@param sign} is null, checks only the class
     *
     * @param lexemeClass       the class of expected lexeme
     * @param sign              the sign of expected lexeme
     * @param isLastAlternative if true, adds error to errors list
     * @return if the next lexeme equals to expected one
     */
    private boolean checkNextLexeme(@NonNull LexemeClass lexemeClass, String sign, boolean isLastAlternative) {
        if (index + 1 < lexemes.size()) {
            Lexeme lexeme = lexemes.get(index + 1);

            if (lexeme.getLexemeClass() == lexemeClass && (sign == null || lexeme.getSign().equals(sign))) {
                index++;
                return true;
            }

            if (isLastAlternative) {
                errorMessages.add(String.format(
                        "On line %d expected '%s', but found '%s'",
                        lexeme.getLine(), sign == null ? "*Any* " + lexemeClass : sign, lexeme.getSign()
                ));
            }

            return false;
        }

        if (isLastAlternative)
            errorMessages.add(String.format("Expected %s, but the end of input reached", sign));

        return false;
    }

    /**
     * Adds current lexeme in the lexemes list as child to {@param node}
     *
     * @param node the node to be assigned a new child
     * @return the created node
     */
    private OutputTreeNode<Lexeme> addLexeme(@NonNull OutputTreeNode<Lexeme> node) {
        return node.addChild(lexemes.get(index));
    }
}
