package com.example.aisocket.project.adapter.in.dto.response;

import com.example.aisocket.project.application.dto.result.CursorPageResult;

import java.util.List;
import java.util.function.Function;

public record CursorPageResponse<T>(
        List<T> items,
        String nextCursor,
        boolean hasNext
) {

    public static <T, R> CursorPageResponse<R> from(CursorPageResult<T> result, Function<T, R> mapper) {
        return new CursorPageResponse<>(
                result.items().stream().map(mapper).toList(),
                result.nextCursor(),
                result.hasNext()
        );
    }
}
