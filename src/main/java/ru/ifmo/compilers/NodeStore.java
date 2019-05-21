package ru.ifmo.compilers;

import lombok.Data;

/**
 * Class to be used as node storage to return from method calls a node
 */
@Data
class NodeStore {
    private OutputTreeNode<Lexeme> node;
}
