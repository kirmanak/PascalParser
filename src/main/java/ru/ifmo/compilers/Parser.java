package ru.ifmo.compilers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
class Parser {
    @NonNull
    private final List<Lexeme> lexemes;
    private ASTNode root = null;


    Parser parseProgram() {
        if (root != null)
            throw new IllegalStateException("AST was already parsed!");

        var iterator = lexemes.listIterator();
        if (iterator.hasNext())
            root = new ASTNode(iterator.next());

        while (iterator.hasNext()) {
            root.addChild(iterator.next());
        }

        return this;
    }

    Optional<ASTNode> getRoot() {
        return Optional.ofNullable(root);
    }
}
