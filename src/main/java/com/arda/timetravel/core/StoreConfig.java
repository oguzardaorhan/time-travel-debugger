package com.arda.timetravel.core;

public class StoreConfig {

    private int snapshotEvery = 10;
    private int historyLimit = 500;

    public StoreConfig snapshotEvery(int n) {
        if (n < 1) throw new IllegalArgumentException("snapshotEvery must be >= 1");
        this.snapshotEvery = n;
        return this;
    }

    public StoreConfig historyLimit(int n) {
        if (n < 2) throw new IllegalArgumentException("historyLimit must be >= 2");
        this.historyLimit = n;
        return this;
    }

    public int getSnapshotEvery() {
        return snapshotEvery;
    }

    public int getHistoryLimit() {
        return historyLimit;
    }
}
