package com.arda.timetravel.core;

import com.arda.timetravel.util.DeepCopy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Store<S extends Serializable> {

    private final Reducer<S> reducer;
    private final StoreConfig config;

    private final List<HistoryEntry<S>> history = new ArrayList<>();
    private int pointer = 0;


    private S initialState;
    private S currentState;

    public Store(S initialState, Reducer<S> reducer) {
        this(initialState, reducer, new StoreConfig());
    }

    public Store(S initialState, Reducer<S> reducer, StoreConfig config) {
        this.reducer = reducer;
        this.config = config;
        this.initialState = DeepCopy.copy(initialState);
        resetToInitial();
    }

    private void resetToInitial() {
        history.clear();
        pointer = 0;
        currentState = DeepCopy.copy(initialState);

        history.add(new HistoryEntry<>(
                Action.of("__INIT__", Map.of()),
                DeepCopy.copy(currentState),
                true
        ));
        pointer = 0;
    }

    public void dispatch(Action action) {
        if (pointer < history.size() - 1) {
            history.subList(pointer + 1, history.size()).clear();
        }

        internalDispatch(action);
    }


    private void internalDispatch(Action action) {
        currentState = DeepCopy.copy(reducer.reduce(DeepCopy.copy(currentState), action));

        boolean isSnapshot = (history.size() % config.getSnapshotEvery() == 0);

        history.add(new HistoryEntry<>(
                action,
                isSnapshot ? DeepCopy.copy(currentState) : null,
                isSnapshot
        ));

        pointer = history.size() - 1;

        enforceHistoryLimit();
    }

    private void enforceHistoryLimit() {
        int limit = config.getHistoryLimit();
        if (history.size() <= limit) return;

        int overflow = history.size() - limit;


        history.subList(1, 1 + overflow).clear();

        pointer -= overflow;
        if (pointer < 0) pointer = 0;


        currentState = computeStateAt(pointer);
    }

    public S undo() {
        if (pointer > 0) pointer--;
        currentState = computeStateAt(pointer);
        return DeepCopy.copy(currentState);
    }

    public S redo() {
        if (pointer < history.size() - 1) pointer++;
        currentState = computeStateAt(pointer);
        return DeepCopy.copy(currentState);
    }

    public S jumpTo(int index) {
        if (index < 0 || index >= history.size()) {
            throw new IllegalArgumentException("Invalid history index: " + index);
        }
        pointer = index;
        currentState = computeStateAt(pointer);
        return DeepCopy.copy(currentState);
    }


    private S computeStateAt(int index) {
        if (index == 0) {
            return DeepCopy.copy(history.get(0).getSnapshotState());
        }

        int snapIndex = index;
        while (snapIndex > 0 && !history.get(snapIndex).isSnapshot()) {
            snapIndex--;
        }

        S state;
        if (history.get(snapIndex).isSnapshot() && history.get(snapIndex).getSnapshotState() != null) {
            state = DeepCopy.copy(history.get(snapIndex).getSnapshotState());
        } else {
            state = DeepCopy.copy(initialState);
            snapIndex = 0;
        }

        for (int i = snapIndex + 1; i <= index; i++) {
            Action a = history.get(i).getAction();
            state = reducer.reduce(state, a);
        }

        return DeepCopy.copy(state);
    }

    public S getState() {
        return DeepCopy.copy(currentState);
    }

    public int getPointer() {
        return pointer;
    }

    public List<HistoryEntry<S>> getHistory() {
        return List.copyOf(history);
    }

    public StoreConfig getConfig() {
        return config;
    }



    public record ExportedTimeline<T>(int pointer, List<HistoryEntry<T>> history) {}

    public ExportedTimeline<S> exportTimeline() {
        return new ExportedTimeline<>(pointer, List.copyOf(history));
    }



    public void importTimeline(S freshInitialState, List<Action> actions, int newPointer) {
        this.initialState = DeepCopy.copy(freshInitialState);
        resetToInitial();


        for (Action a : actions) {
            internalDispatch(a);
        }

        jumpTo(Math.min(newPointer, history.size() - 1));
    }
}
