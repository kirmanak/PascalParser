package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
class ASTNode {
    private final Lexeme lexeme;
    private final List<ASTNode> children = new LinkedList<>();

    void print() {
        print("", true);
    }

    private void print(@NonNull String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + lexeme);

        Optional<ASTNode> lastChild = Optional.empty();
        for (var child : children) {
            child.print(prefix + (isTail ? "    " : "│   "), false);
            lastChild = Optional.of(child);
        }

        lastChild.ifPresent(node -> node.print(prefix + (isTail ? "    " : "│   "), true));
    }

    ASTNode addChild(@NonNull Lexeme lexeme) {
        var node = new ASTNode(lexeme);
        children.add(node);
        return node;
    }
}
