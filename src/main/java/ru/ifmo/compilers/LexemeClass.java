package ru.ifmo.compilers;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Represents the class of a lexeme
 */
public enum LexemeClass {
    /**
     * Assignment operator: ":="
     */
    AssignmentOperator(Set.of(":=")),
    /**
     * An arithmetic operator: "+", "-", "*", "/".
     */
    ArithmeticOperator(Set.of("+", "-", "*", "/")),

    /**
     * A comparison operator: "<", ">", "=".
     */
    ComparisonOperator(Set.of("<", ">", "=")),

    /**
     * A separator: ",", ";", "(", ")" and whitespaces.
     */
    Separator(Set.of(",", ";", "(", ")")),

    /**
     * A keyword: "Var", "Begin", "End", "End.", "WHILE", "DO".
     */
    Keyword(Set.of("Var", "Begin", "End", "End.", "WHILE", "DO")),

    /**
     * A numeric constant.
     */
    Const(Collections.emptySet()),

    /**
     * An identifier, for example a name of a variable.
     * Contains only alphabetic characters.
     */
    Ident(Collections.emptySet()),

    /**
     * An incorrect lexeme.
     */
    Undefined(Collections.emptySet());

    private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\p{javaDigit}+$");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("^\\p{javaWhitespace}+$");
    private static final Pattern LETTER_PATTERN = Pattern.compile("^\\p{javaAlphabetic}+$");

    private final Set<String> mPossibleValues;

    LexemeClass(final Set<String> possibleValues) {
        this.mPossibleValues = possibleValues;
    }

    /**
     * Tries to find a lexeme in the provided string
     *
     * @return The class of found lexeme or {@link LexemeClass#Undefined} if not found
     */
    public static LexemeClass determine(final String string) {
        if (string == null)
            return Undefined;

        for (final String assignmentOperator : AssignmentOperator.getPossibleValues())
            if (assignmentOperator.equals(string))
                return AssignmentOperator;

        for (final String arithmeticOperator : ArithmeticOperator.getPossibleValues())
            if (arithmeticOperator.equals(string))
                return ArithmeticOperator;

        for (final String comparisonOperator : ComparisonOperator.getPossibleValues())
            if (comparisonOperator.equals(string))
                return ComparisonOperator;

        for (final String keyword : Keyword.getPossibleValues())
            if (keyword.equals(string))
                return Keyword;

        for (final String keyword : Separator.getPossibleValues())
            if (keyword.equals(string))
                return Separator;

        if (WHITESPACE_PATTERN.matcher(string).matches())
            return Separator;

        if (DIGIT_PATTERN.matcher(string).matches())
            return Const;

        if (LETTER_PATTERN.matcher(string).matches())
            return Ident;

        return Undefined;
    }

    public Set<String> getPossibleValues() {
        return mPossibleValues;
    }
}
