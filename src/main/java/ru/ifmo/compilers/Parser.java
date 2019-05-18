package ru.ifmo.compilers;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
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
        Random random = new Random();

        var iterator = lexemes.listIterator();
        if (iterator.hasNext())
            root = new OutputTreeNode<>(iterator.next());

        List<OutputTreeNode<Lexeme>> nodes = new ArrayList<>(lexemes.size());
        nodes.add(root);
        while (iterator.hasNext()) {
            var node = nodes.get(random.nextInt(nodes.size()));
            nodes.add(node.addChild(iterator.next()));
        }

        return this;
    }

    Optional<OutputTreeNode> getRoot() {
        return Optional.ofNullable(root);
    }
}
