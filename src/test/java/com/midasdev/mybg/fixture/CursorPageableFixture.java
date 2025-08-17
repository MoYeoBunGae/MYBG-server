package com.midasdev.mybg.fixture;

import com.midasdev.mybg.global.util.cursor_page.CursorPageable;
import com.midasdev.mybg.global.util.cursor_page.SortOrder;

public class CursorPageableFixture {
    public static CursorPageable create() {
        return CursorPageable.builder()
                .lastCursorId(0L)
                .pageSize(10)
                .sortOrder(SortOrder.ASC)
                .build();
    }

}
