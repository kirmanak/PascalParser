package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@Data
class ASTNode<T> {
    private final T content;
    private final List<ASTNode> children = new LinkedList<>();

    void print() {
        System.out.println(content);
        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().print("", !iterator.hasNext());
    }

    private void print(@NonNull String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().print(prefix + (isTail ? "    " : "│   "), !iterator.hasNext());
    }

    ASTNode<T> addChild(@NonNull T content) {
        var node = new ASTNode<T>(content);
        children.add(node);
        return node;
    }
}
