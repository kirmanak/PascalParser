package ru.ifmo.compilers;

import lombok.NonNull;

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
    private OutputTreeNode<Lexeme> root = new OutputTreeNode<>("AST");

    /**
     * Constructs a new instance of parser
     *
     * @param lexemes the list of lexemes to be parsed
     */
    Parser(@NonNull List<Lexeme> lexemes) {
        this.lexemes = lexemes;
    }

    @NonNull
    Parser parseProgram() {
        if (root.getChildCount() > 0)
            throw new IllegalStateException("AST was already parsed!");

        lexemes.forEach(this::onNewLexeme);

        return this;
    }

    OutputTreeNode<Lexeme> getRoot() {
        return root;
    }

    private void onNewLexeme(@NonNull Lexeme lexeme) {
        root.addChild(lexeme);
    }
}
