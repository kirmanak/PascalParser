package ru.ifmo.compilers;

/**
 * Represents a lexeme found in the source code
 */
public class Lexeme {
    /**
     * The class of found lexeme
     */
    private final LexemeClass mClass;

    /**
     * The sign of the lexeme
     */
    private final String mSign;

    /**
     * The line of source code where it was found
     */
    private final int mLine;

    Lexeme(LexemeClass mClass, String mSign, int mLine) {
        this.mClass = mClass;
        this.mSign = mSign;
        this.mLine = mLine;
    }

    public LexemeClass getLexemeClass() {
        return mClass;
    }

    public String getSign() {
        return mSign;
    }

    public int getLine() {
        return mLine;
    }

    @Override
    public String toString() {
        return "Lexeme{" +
                "mClass=" + mClass +
                ", mSign='" + mSign + '\'' +
                ", mLine=" + mLine +
                '}';
    }
}
