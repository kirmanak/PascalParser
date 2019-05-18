package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

import java.io.OutputStream;
import java.io.PrintStream;
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
     * Wraps OutputStream to PrintStream.
     * Returns the argument if it is actually a PrintStream
     * or creates a new one otherwise.
     *
     * @param out the stream to be wrapped
     * @return the argument itself or a new PrintStream instance
     */
    private static PrintStream getOutput(@NonNull OutputStream out) {
        return out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
    }

    /**
     * Prints the node as root node, then prints its children
     *
     * @param out where to print the data
     */
    void print(@NonNull OutputStream out) {
        PrintStream stream = getOutput(out);

        stream.println(content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild("", iterator.hasNext(), stream);
    }

    /**
     * Adds a new child to the node
     *
     * @param content the content of new node
     * @return the created node
     */
    @NonNull
    OutputTreeNode<T> addChild(@NonNull T content) {
        var node = new OutputTreeNode<T>(content);
        children.add(node);
        return node;
    }

    /**
     * Prints the node as child node
     *
     * @param prefix  the prefix to be printed before the node
     * @param hasNext whether the root node has more children or not
     * @param stream  the stream to write output
     */
    private void printAsChild(@NonNull String prefix, boolean hasNext, @NonNull PrintStream stream) {
        stream.println(prefix + "│   ");
        stream.println(prefix + (hasNext ? "├── " : "└── ") + content);

        var newPrefix = prefix + (hasNext ? "│   " : "    ");

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild(newPrefix, iterator.hasNext(), stream);
    }
}
