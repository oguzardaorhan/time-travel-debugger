package com.arda.timetravel;

import com.arda.timetravel.core.*;
import com.arda.timetravel.demo.AppState;
import com.arda.timetravel.storage.TimelineIO;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Reducer<AppState> reducer = (state, action) -> {
            switch (action.getType()) {
                case "ADD_ITEM" -> state.getItems().add((String) action.getPayload().get("name"));
                case "REMOVE_LAST" -> {
                    if (!state.getItems().isEmpty()) state.getItems().remove(state.getItems().size() - 1);
                }
            }
            return state;
        };

        StoreConfig cfg = new StoreConfig().snapshotEvery(2).historyLimit(50);
        Store<AppState> store = new Store<>(new AppState(), reducer, cfg);

        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "milk")));
        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "bread")));
        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "eggs")));

        System.out.println("Current: " + store.getState());

        System.out.println("Undo: " + store.undo());
        System.out.println("Undo: " + store.undo());
        System.out.println("Redo: " + store.redo());

        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "tea")));
        System.out.println("After new action: " + store.getState());

        System.out.println("Redo (should stay): " + store.redo());

        try {
            var exported = store.exportTimeline();
            Path file = Paths.get("timeline.json");

            TimelineIO.save(
                    file,
                    exported.pointer(),
                    store.getConfig().getSnapshotEvery(),
                    store.getConfig().getHistoryLimit(),
                    exported.history()
            );
            System.out.println("Saved timeline to: " + file.toAbsolutePath());

            var dto = TimelineIO.load(file);

            List<Action> actions = dto.entries().stream()
                    .skip(1)
                    .map(TimelineIO::toAction)
                    .toList();

            Store<AppState> restored = new Store<>(new AppState(), reducer, cfg);
            restored.importTimeline(new AppState(), actions, dto.pointer());

            System.out.println("Restored pointer: " + restored.getPointer());
            System.out.println("Restored state: " + restored.getState());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
