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
        if (mClass == null || mSign == null || mSign.length() == 0)
            throw new IllegalArgumentException("Lexeme can not contain empty values");

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
        return String.format("Lexeme { mClass = %-20s, mSign = '%-10s', mLine = %-3d }", mClass, mSign, mLine);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Lexeme lexeme = (Lexeme) o;

        if (mLine != lexeme.mLine)
            return false;
        if (mClass != lexeme.mClass)
            return false;
        return mSign.equals(lexeme.mSign);

    }

    @Override
    public int hashCode() {
        int result = mClass.hashCode();
        result = 31 * result + mSign.hashCode();
        result = 31 * result + mLine;
        return result;
    }
}
