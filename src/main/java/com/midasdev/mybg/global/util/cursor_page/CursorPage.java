package com.midasdev.mybg.global.util.cursor_page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CursorPage<T extends LongIdentifiable> {
    private final List<T> content;
    private final Long nextCursorId;
    private final Integer pageSize;
    private final boolean hasNext;

    /**
     * @param fetchedContent pageSize+1의 limit을 적용하여 가져온 데이터
     * @param pageSize
     */
    public CursorPage(List<T> fetchedContent, int pageSize) {
        if (fetchedContent.size() > pageSize + 1) {
            throw new IllegalArgumentException(
                    String.format(
                            "Invalid fetchedContent size: expected at most %d (pageSize + 1), but got %d",
                            pageSize + 1, fetchedContent.size()));
        }

        this.pageSize = pageSize;
        this.hasNext = fetchedContent.size() > pageSize;
        if (hasNext) {
            this.nextCursorId = fetchedContent.get(pageSize - 1).getId();
            this.content = List.copyOf(fetchedContent.subList(0, pageSize));
        } else {
            this.nextCursorId = null;
            this.content = List.copyOf(fetchedContent);
        }
    }

    public <R extends LongIdentifiable> CursorPage<R> map(Function<? super T, ? extends R> mapper) {
        List<R> mappedContent = content.stream().map(mapper).collect(Collectors.toList());
        return CursorPage.<R>builder()
                .content(mappedContent)
                .nextCursorId(nextCursorId)
                .pageSize(pageSize)
                .hasNext(hasNext)
                .build();
    }
}
