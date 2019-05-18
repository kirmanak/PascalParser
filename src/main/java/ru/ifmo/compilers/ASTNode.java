package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@Data
class ASTNode<T> {
    @NonNull
    private final T content;
    private final List<ASTNode> children = new LinkedList<>();

    void printAsChild() {
        System.out.println(content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild("", iterator.hasNext());
    }

    private void printAsChild(@NonNull String prefix, boolean hasNext) {
        System.out.println(prefix + (hasNext ? "├── " : "└── ") + content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild(prefix + (hasNext ? "│   " : "    "), iterator.hasNext());
    }

    ASTNode<T> addChild(@NonNull T content) {
        var node = new ASTNode<T>(content);
        children.add(node);
        return node;
    }
}
