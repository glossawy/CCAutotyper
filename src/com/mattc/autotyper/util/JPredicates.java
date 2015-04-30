package com.mattc.autotyper.util;

import java.util.function.Predicate;

/**
 * Utility with the goal of creating Google Predicates that work in the
 * Java Function Framework. In the case they are not available you you REALLY need
 * {@link com.google.common.base.Predicate Google Predicate} functionality, using the {@link #of(com.google.common.base.Predicate)} method
 * will return the {@link Predicate Java Predicate} equivalent. <br />
 * <br />
 * Currently, this is not <b><i>as</i></b> robust as Google Predicates due to non-use. This
 * implementation uses Logical Expressions over the separate Predicate classes that Google
 * Predicates use (AndPredicate, NotPredicate, etc.).
 *
 * @author Matthew Crocco
 *         Created on 4/25/15
 */
public final class JPredicates {

    private JPredicates() {throw new AssertionError();}

    public static <T> Predicate<T>  alwaysTrue() {
        return (t) -> true;
    }

    public static <T> Predicate<T> alwaysFalse() {
        return (t) -> false;
    }

    public static <T> Predicate<T> isNotNull() {
        return (t) -> t != null;
    }

    public static <T> Predicate<T> isNull() {
        return (t) -> t == null;
    }

    public static <T> Predicate<T> isEqual(Object ref) {
        return (t) -> t.equals(ref);
    }

    public static <T> Predicate<T> isNotEqual(Object ref) {
        return (t) -> !t.equals(ref);
    }

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return (t) -> !predicate.test(t);
    }

    public static <T> Predicate<T> and(Predicate<T> first, Predicate<T> second) {
        return (t) -> (first.test(t) && second.test(t));
    }

    public static <T> Predicate<T> or(Predicate<T> first, Predicate<T> second) {
        return (t) -> (first.test(t) || second.test(t));
    }

    public static <T> com.google.common.base.Predicate<T> to(Predicate<T> predicate) {
        return predicate::test;
    }

    public static <T> Predicate<T> of(com.google.common.base.Predicate<T> predicate) {
        return predicate::apply;
    }
}
