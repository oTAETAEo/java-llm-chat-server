package com.example.aisocket.project.application.dto.result;

import java.util.List;

public record CursorPageResult<T>(
        List<T> items,
        String nextCursor,
        boolean hasNext
) {
}
