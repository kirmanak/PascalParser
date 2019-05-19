package ru.ifmo.compilers;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs the syntax analysis
 */
class Parser {
    /**
     * The list of lexemes from {@link Lexer}
     */
    @NonNull
    private final List<Lexeme> lexemes;

    @Getter
    private final List<String> errorMessages = new ArrayList<>();
    @Getter
    private OutputTreeNode<Lexeme> root = new OutputTreeNode<>("AST");
    private int index = -1;

    /**
     * Constructs a new instance of parser
     *
     * @param lexemes the list of lexemes to be parsed
     */
    Parser(@NonNull List<Lexeme> lexemes) {
        this.lexemes = lexemes;
    }

    boolean parseProgram() {
        if (root.hasChildren())
            throw new IllegalStateException("AST was already parsed!");

        return parseVariablesDeclaration(root, true) && parseComputations(root, true);
    }

    private boolean parseComputations(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (!checkNextLexeme(LexemeClass.Keyword, "Begin", isLastAlternative))
            return false;

        if (!parseOperatorsList(parent, isLastAlternative))
            return false;

        return checkNextLexeme(LexemeClass.Keyword, "End.", isLastAlternative);

    }

    private boolean parseOperatorsList(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (parseOperator(parent, isLastAlternative)) {
            parseOperatorsList(parent, isLastAlternative);
            return true;
        }

        return false;
    }

    private boolean parseOperator(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        return parseAssignment(parent, false)
                || parseComplexOperator(parent, false)
                || parseCompoundOperator(parent, isLastAlternative);
    }

    private boolean parseCompoundOperator(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (!checkNextLexeme(LexemeClass.Keyword, "Begin", isLastAlternative))
            return false;

        if (!parseOperatorsList(parent, isLastAlternative))
            return false;

        return checkNextLexeme(LexemeClass.Keyword, "End", isLastAlternative);

    }

    private boolean parseComplexOperator(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        return parseLoopOperator(parent, false)
                || parseCompoundOperator(parent, isLastAlternative);
    }

    private boolean parseLoopOperator(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        OutputTreeNode<Lexeme> whileNode;
        if (checkNextLexeme(LexemeClass.Keyword, "WHILE", isLastAlternative))
            whileNode = addLexeme(parent);
        else
            return false;

        if (!parseExpression(whileNode, true))
            return false;


        OutputTreeNode<Lexeme> doNode;
        if (checkNextLexeme(LexemeClass.Keyword, "DO", isLastAlternative))
            doNode = addLexeme(parent);
        else
            return false;

        return parseOperator(doNode, isLastAlternative);
    }

    private boolean parseAssignment(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        Lexeme toBeAssigned;
        if (checkNextLexeme(LexemeClass.Ident, null, isLastAlternative)) {
            toBeAssigned = lexemes.get(index);
            parent.addChild(toBeAssigned);
        } else {
            return false;
        }

        OutputTreeNode<Lexeme> assignmentOp;
        if (checkNextLexeme(LexemeClass.AssignmentOperator, null, isLastAlternative))
            assignmentOp = addLexeme(parent);
        else
            return false;

        assignmentOp.addChild(toBeAssigned);

        if (!parseExpression(assignmentOp, true))
            return false;

        return checkNextLexeme(LexemeClass.Separator, ";", true);
    }

    private boolean parseExpression(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        parseUnaryOperation(parent, false);
        return parseSubExpression(parent, isLastAlternative);
    }

    private boolean parseSubExpression(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Separator, "(", false)) {
            if (!parseExpression(parent, true))
                return false;

            return checkNextLexeme(LexemeClass.Separator, ")", true);
        }

        if (parseOperand(parent, false))
            return true;

        if (parseSubExpression(parent, isLastAlternative)) {

            if (!parseBinaryOperator(parent, true))
                return false;

            return parseSubExpression(parent, true);
        }

        return false;
    }

    private boolean parseBinaryOperator(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.ArithmeticOperator, null, false)) {
            addLexeme(parent);
            return true;
        }

        if (checkNextLexeme(LexemeClass.ComparisonOperator, null, isLastAlternative))
            addLexeme(parent);
        else
            return false;

        return true;
    }

    private boolean parseUnaryOperation(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.ArithmeticOperator, "-", false))
            addLexeme(parent);
        else
            return false;

        return true;
    }

    private boolean parseOperand(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
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

    private boolean parseVariablesDeclaration(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Keyword, "Var", isLastAlternative))
            return parseVariablesList(addLexeme(parent), isLastAlternative);

        else
            return false;
    }

    private boolean parseVariablesList(OutputTreeNode<Lexeme> parent, boolean isLastAlternative) {
        if (checkNextLexeme(LexemeClass.Ident, null, isLastAlternative))
            addLexeme(parent);
        else
            return false;

        if (checkNextLexeme(LexemeClass.Separator, ";", false)) {
            parseVariablesList(parent, false);
            return true;
        }

        if (checkNextLexeme(LexemeClass.Separator, ",", true))
            return parseVariablesList(parent, true);

        return false;
    }

    private boolean checkNextLexeme(@NonNull LexemeClass lexemeClass, String sign, boolean isLastAlternative) {
        if (index < lexemes.size()) {
            Lexeme lexeme = lexemes.get(index + 1);

            if (lexeme.getLexemeClass() == lexemeClass && (sign == null || lexeme.getSign().equals(sign))) {
                index++;
                return true;
            }

            if (isLastAlternative) {
                errorMessages.add(String.format(
                        "On line %d expected '%s', but found '%s'",
                        lexeme.getLine(), sign, lexeme.getSign()
                ));
            }

            return false;
        }

        if (isLastAlternative)
            errorMessages.add(String.format("Expected %s, but the end of input reached", sign));

        return false;
    }

    private OutputTreeNode<Lexeme> addLexeme(OutputTreeNode<Lexeme> node) {
        return node.addChild(lexemes.get(index));
    }
}
