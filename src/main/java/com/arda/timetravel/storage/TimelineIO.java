package com.arda.timetravel.storage;

import com.arda.timetravel.core.Action;
import com.arda.timetravel.core.HistoryEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class TimelineIO {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private TimelineIO() {}


    public record TimelineDTO(
            String formatVersion,
            String createdAt,
            int pointer,
            int snapshotEvery,
            int historyLimit,
            List<EntryDTO> entries
    ) {}

    public record EntryDTO(
            String type,
            Map<String, Object> payload,
            boolean snapshot
    ) {}


    public static <S> void save(
            Path file,
            int pointer,
            int snapshotEvery,
            int historyLimit,
            List<HistoryEntry<S>> history
    ) throws IOException {

        List<EntryDTO> entries = history.stream()
                .map(h -> new EntryDTO(
                        h.getAction().getType(),
                        h.getAction().getPayload(),
                        h.isSnapshot()
                ))
                .toList();

        TimelineDTO dto = new TimelineDTO(
                "1.0",
                Instant.now().toString(),
                pointer,
                snapshotEvery,
                historyLimit,
                entries
        );

        Files.writeString(file, GSON.toJson(dto));
    }


    public static TimelineDTO load(Path file) throws IOException {
        String json = Files.readString(file);
        return GSON.fromJson(json, TimelineDTO.class);
    }


    public static Action toAction(EntryDTO dto) {
        return Action.of(
                dto.type(),
                dto.payload() == null ? Map.of() : dto.payload()
        );
    }
}
