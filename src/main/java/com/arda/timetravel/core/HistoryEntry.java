package com.arda.timetravel.core;

public class HistoryEntry<S> {
    private final Action action;
    private final S snapshotState;
    private final boolean snapshot;

    public HistoryEntry(Action action, S snapshotState, boolean snapshot) {
        this.action = action;
        this.snapshotState = snapshotState;
        this.snapshot = snapshot;
    }

    public Action getAction() {
        return action;
    }

    public S getSnapshotState() {
        return snapshotState;
    }

    public boolean isSnapshot() {
        return snapshot;
    }
}
