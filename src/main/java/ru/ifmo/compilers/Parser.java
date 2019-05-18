package ru.ifmo.compilers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
class Parser {
    @NonNull
    private final List<Lexeme> lexemes;
    private OutputTreeNode<Lexeme> root = null;

    Parser parseProgram() {
        if (root != null)
            throw new IllegalStateException("AST was already parsed!");

        var iterator = lexemes.listIterator();
        if (iterator.hasNext())
            root = new OutputTreeNode<>(iterator.next());

        OutputTreeNode<Lexeme> lastNode = root;
        while (iterator.hasNext()) {
            if (new Random().nextBoolean())
                lastNode = lastNode.addChild(iterator.next());
            else
                lastNode.addChild(iterator.next());
        }

        return this;
    }

    Optional<OutputTreeNode> getRoot() {
        return Optional.ofNullable(root);
    }
}
