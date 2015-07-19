package com.mattc.autotyper.util;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility with the goal of creating Google Predicates that work in the
 * Java Function Framework. In the case they are not available you you REALLY need
 * {@link com.google.common.base.Predicate Google Predicate} functionality, using the {@link #of(com.google.common.base.Predicate)} method
 * will return the {@link Predicate Java Predicate} equivalent. <br />
 * <br />
 * Currently, this is not <b><em>as</em></b> robust as Google Predicates due to non-use. This
 * implementation uses Logical Expressions over the separate Predicate classes that Google
 * Predicates use (AndPredicate, NotPredicate, etc.).
 *
 * @author Matthew Crocco
 *         Created on 4/25/15
 */
public final class JPredicates {

    private JPredicates() {
        throw new AssertionError();
    }

    /**
     * @return A Predicate that always returns True
     */
    public static <T> Predicate<T> alwaysTrue() {
        return (t) -> true;
    }

    /**
     * @return A Predicate that always returns False
     */
    public static <T> Predicate<T> alwaysFalse() {
        return (t) -> false;
    }

    /**
     * @return A Predicate that returns true if the object is not null
     */
    public static <T> Predicate<T> isNotNull() {
        return (t) -> t != null;
    }

    /**
     * @return A Predicate that returns true if the object is null
     */
    public static <T> Predicate<T> isNull() {
        return (t) -> t == null;
    }

    /**
     * @param ref Test Reference, what all tests will compare to
     * @param <T> Type Of Predicate
     * @return A Predicate that returns true if the test object equals the test reference based on {@link Object#equals(Object)}
     */
    public static <T> Predicate<T> isEqual(Object ref) {
        return (t) -> t.equals(ref);
    }

    /**
     * @param ref Test Reference, what all tests will compare to
     * @param <T> Type Of Predicate
     * @return A Predicate that returns true if the test object DOES NOT equal the test reference based on {@link Object#equals(Object)}
     */
    public static <T> Predicate<T> isNotEqual(Object ref) {
        return (t) -> !t.equals(ref);
    }

    /**
     * @param predicate Predicate to Negate
     * @param <T>       Type of Predicate
     * @return The Negated Predicate of the Given Predicate
     */
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return (t) -> !predicate.test(t);
    }

    /**
     * @param first  First Predicate in Combination
     * @param second Second Predicate in Combination
     * @param <T>    Type of Predicate
     * @return The Predicate yielding the result of first && second
     */
    public static <T> Predicate<T> and(Predicate<T> first, Predicate<T> second) {
        return (t) -> (first.test(t) && second.test(t));
    }

    /**
     * @param first  First Predicate in Combination
     * @param second Second Predicate in Combination
     * @param <T>    Type of Predicate
     * @return The Predicate yielding the result of first || second
     */
    public static <T> Predicate<T> or(Predicate<T> first, Predicate<T> second) {
        return (t) -> (first.test(t) || second.test(t));
    }

    /**
     * Converts the Given {@link Predicate} to a {@link com.google.common.base.Predicate}.
     *
     * @param predicate Java Predicate
     * @param <T>       Predicate Type
     * @return Google Predicate converted from Java Predicate
     */
    public static <T> com.google.common.base.Predicate<T> to(Predicate<T> predicate) {
        return predicate::test;
    }

    // Why doesn't predicate just implement Function? Besides autoboxing...
    public static <T> Function<T, Boolean> asFunction(Predicate<T> predicate) {
        return predicate::test;
    }

    /**
     * Converts the Given {@link com.google.common.base.Predicate} to a {@link Predicate}.
     *
     * @param predicate Google Predicate
     * @param <T>       Predicate Type
     * @return Java Predicate converted from Google Predicate
     */
    public static <T> Predicate<T> of(com.google.common.base.Predicate<T> predicate) {
        return predicate::apply;
    }

    public static <T> com.google.common.base.Function<T, Boolean> asFunction(com.google.common.base.Predicate<T> predicate) {
        return predicate::apply;
    }
}
