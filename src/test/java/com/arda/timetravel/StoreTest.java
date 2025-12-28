package com.arda.timetravel;

import com.arda.timetravel.core.*;
import com.arda.timetravel.demo.AppState;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTest {

    @Test
    void smokeTest() {
        assertTrue(true);
    }

    @Test
    void undoRedoWorksAndFutureIsTruncated() {
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

        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "a")));
        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "b")));
        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "c")));

        assertEquals("[a, b, c]", store.getState().getItems().toString());

        store.undo();
        assertEquals("[a, b]", store.getState().getItems().toString());

        store.undo();
        assertEquals("[a]", store.getState().getItems().toString());

        store.redo();
        assertEquals("[a, b]", store.getState().getItems().toString());

        store.dispatch(Action.of("ADD_ITEM", Map.of("name", "x")));
        assertEquals("[a, b, x]", store.getState().getItems().toString());

        store.redo();
        assertEquals("[a, b, x]", store.getState().getItems().toString());
    }
}
