package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@Data
class ASTNode {
    private final Lexeme lexeme;
    private final List<ASTNode> children = new LinkedList<>();

    void print() {
        System.out.println(lexeme);
        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().print("", !iterator.hasNext());
    }

    private void print(@NonNull String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + lexeme);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().print(prefix + (isTail ? "    " : "│   "), !iterator.hasNext());
    }

    ASTNode addChild(@NonNull Lexeme lexeme) {
        var node = new ASTNode(lexeme);
        children.add(node);
        return node;
    }
}
