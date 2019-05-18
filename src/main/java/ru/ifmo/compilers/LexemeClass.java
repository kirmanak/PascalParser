package ru.ifmo.compilers;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Represents the class of a lexeme
 */
@RequiredArgsConstructor
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

    private static final Pattern DIGIT_PATTERN = Pattern.compile("^\\p{Digit}+$");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("^\\p{Space}+$");
    private static final Pattern LETTER_PATTERN = Pattern.compile("^\\p{Alpha}+$");

    private final Set<String> mPossibleValues;

    /**
     * Tries to find a lexeme in the provided string
     *
     * @return The class of found lexeme or {@link LexemeClass#Undefined} if not found
     */
    public static LexemeClass determine(String string) {
        if (string == null || string.isEmpty())
            return Undefined;

        return Stream.of(values())
                .filter(lexemeClass -> lexemeClass.test(string))
                .findFirst()
                .orElse(LexemeClass.Undefined);
    }

    private boolean test(String value) {
        switch (this) {
            case Separator:
                if (WHITESPACE_PATTERN.matcher(value).matches())
                    return true;

            case AssignmentOperator:
            case ArithmeticOperator:
            case ComparisonOperator:
            case Keyword:
                return mPossibleValues.contains(value);

            case Const:
                return DIGIT_PATTERN.matcher(value).matches();

            case Ident:
                return LETTER_PATTERN.matcher(value).matches();

            case Undefined:
                return false;

            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }
}
