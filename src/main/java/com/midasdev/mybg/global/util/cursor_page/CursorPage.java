package com.midasdev.mybg.global.util.cursor_page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CursorPage<T> {
    private final List<T> content;
    private final Long nextCursorId;
    private final boolean hasNext;

    public <R> CursorPage<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mappedContent = content.stream().map(mapper).collect(Collectors.toList());
        return new CursorPage<>(mappedContent, nextCursorId, hasNext);
    }
}
