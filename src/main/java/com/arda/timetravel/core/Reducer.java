package com.arda.timetravel.core;

@FunctionalInterface
public interface Reducer<S> {
    S reduce(S currentState, Action action);
}
