package com.mattc.autotyper.gui.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.mattc.autotyper.util.JPredicates;

import java.util.stream.Collectors;

/**
 * Needs Documentation
 *
 * @author Glossawy
 *         Created on 4/25/15
 */
public final class AutoCompleteUtils {

    private AutoCompleteUtils() {
        throw new AssertionError();
    }

    public static ObservableList<String> selectCompletionCandidates(final ObservableList<String> data, String base, boolean sort) {
        if (base.isEmpty())
            return FXCollections.observableArrayList(data);

        ObservableList<StringWrapper> wrappers = FXCollections.observableArrayList();
        final ObservableList<String> candidates = FXCollections.observableArrayList();

        for (final String s : data) {
            wrappers.add(new StringWrapper(s));
        }

        wrappers = selectCompletionCandidates(wrappers, new StringWrapper(base), sort);

        for (final StringWrapper wrapper : wrappers) {
            candidates.add(wrapper.toString());
        }

        return candidates;
    }

    public static <T extends AutoCompleteObject<T>> ObservableList<T> selectCompletionCandidates(final ObservableList<T> data, final T base, final boolean sort) {
        final ObservableList<T> candidates = data.stream()
                .filter(JPredicates.and(JPredicates.isNotNull(), base::isValidCandidate))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        if (sort)
            FXCollections.sort(candidates, AutoCompleteObject::compareTo);

        return candidates;
    }

    private static class StringWrapper implements AutoCompleteObject<StringWrapper> {

        private final String str;
        private final String compStr;

        private StringWrapper(String str) {
            this.str = str;
            this.compStr = str.toLowerCase();
        }

        @Override
        public boolean isValidCandidate(StringWrapper base) {
            return base.compStr.contains(this.compStr);
        }

        @Override
        public int compareTo(StringWrapper other) {
            return this.str.compareTo(other.str);
        }

        @Override
        public String toString() {
            return this.str;
        }

    }

}
