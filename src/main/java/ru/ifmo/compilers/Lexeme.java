package ru.ifmo.compilers;

import lombok.Data;
import lombok.NonNull;

/**
 * Represents a lexeme found in the source code
 */
@Data
public class Lexeme {
    /**
     * The class of found lexeme
     */
    @NonNull
    private final LexemeClass mClass;

    /**
     * The sign of the lexeme
     */
    @NonNull
    private final String mSign;

    /**
     * The line of source code where it was found
     */
    private final int mLine;
}
