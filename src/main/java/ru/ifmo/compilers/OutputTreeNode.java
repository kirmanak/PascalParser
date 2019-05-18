package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a node in tree
 *
 * @param <T> the type of the content in the node
 */
@Data
class OutputTreeNode<T> {
    /**
     * The content to represent
     */
    @NonNull
    private final T content;

    /**
     * The list of child nodes
     */
    private final List<OutputTreeNode> children = new LinkedList<>();

    /**
     * Prints the node as root node and its children
     */
    void print() {
        System.out.println(content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild("", iterator.hasNext());
    }

    /**
     * Prints the node as child node
     *
     * @param prefix  the prefix to be printed before the node
     * @param hasNext whether the root node has more children or not
     */
    private void printAsChild(@NonNull String prefix, boolean hasNext) {
        System.out.println(prefix + (hasNext ? "├── " : "└── ") + content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild(prefix + (hasNext ? "│   " : "    "), iterator.hasNext());
    }

    /**
     * Adds a new child to the node
     *
     * @param content the content of new node
     * @return the created node
     */
    OutputTreeNode<T> addChild(@NonNull T content) {
        var node = new OutputTreeNode<T>(content);
        children.add(node);
        return node;
    }
}
