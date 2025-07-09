package com.midasdev.mybg.global.util;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CursorPage<T> {
    private final List<T> content;
    private final Long nextCursorId;
    private final boolean hasNext;
}
