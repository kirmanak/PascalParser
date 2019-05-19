package ru.ifmo.compilers;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Performs the syntax analysis
 */
class Parser {
    /**
     * The list of lexemes from {@link Lexer}
     */
    @NonNull
    private final List<Lexeme> lexemes;
    private OutputTreeNode<Lexeme> root = null;

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
        if (root != null)
            throw new IllegalStateException("AST was already parsed!");

        lexemes.forEach(this::onNewLexeme);

        return this;
    }

    Optional<OutputTreeNode<Lexeme>> getRoot() {
        return Optional.ofNullable(root);
    }

    private void onNewLexeme(@NonNull Lexeme lexeme) {
        if (root == null)
            root = new OutputTreeNode<>(lexeme);
        else
            root.addChild(lexeme);
    }
}
