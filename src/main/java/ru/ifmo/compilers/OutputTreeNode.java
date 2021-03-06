package ru.ifmo.compilers;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a node in tree
 *
 * @param <T> the type of the content in the node
 */
@AllArgsConstructor
@EqualsAndHashCode
class OutputTreeNode<T> {
    /**
     * The content to represent
     */
    @NonNull
    private final String content;

    /**
     * The list of child nodes
     */
    @Getter
    private final List<OutputTreeNode<T>> children = new LinkedList<>();

    /**
     * Creates a new tree node
     *
     * @param content the content to be represented
     */
    private OutputTreeNode(@NonNull T content) {
        this.content = content.toString();
    }

    /**
     * Prints the node as root node, then prints its children
     *
     * @param out where to print the data
     */
    void print(@NonNull PrintStream out) {
        out.println(content);

        var iterator = children.iterator();
        while (iterator.hasNext())
            iterator.next().printAsChild("", iterator.hasNext(), out);
    }

    /**
     * Adds a new child to the node
     *
     * @param content the content of new node
     * @return the created node
     */
    @NonNull
    OutputTreeNode<T> addChild(@NonNull T content) {
        var node = new OutputTreeNode<>(content);
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

    /**
     * Writes {@link OutputTreeNode#print} result to String
     *
     * @return the resulting String instance
     */
    @Override
    public String toString() {
        var bytes = new ByteArrayOutputStream();

        try (bytes) {
            print(new PrintStream(bytes));
        } catch (Throwable e) {
            return super.toString();
        }

        return bytes.toString();
    }

    /**
     * Checks whether this node has children or not
     *
     * @return true if children.size() > 0, false otherwise
     */
    boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Adds all children from the {@param newChildren} to this node
     *
     * @param newChildren children to add
     */
    void addChildren(Collection<OutputTreeNode<T>> newChildren) {
        children.addAll(newChildren);
    }
}
