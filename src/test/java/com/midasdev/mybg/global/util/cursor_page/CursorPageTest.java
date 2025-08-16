package com.midasdev.mybg.global.util.cursor_page;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CursorPageTest {

    @Test
    @DisplayName("fetchedContent가 pageSize보다 적을 때 올바른 CursorPage 생성")
    void constructor_ShouldReturnFalseHasNext_WhenFetchedContentSizeLessThanPageSize() {
        // given
        int pageSize = 5;
        List<TestLongIdentifiable> fetchedContent = createTestData(3);

        // when
        CursorPage<TestLongIdentifiable> cursorPage = new CursorPage<>(fetchedContent, pageSize);

        // then
        assertSoftly(softly -> {
            softly.assertThat(cursorPage.isHasNext()).isFalse();
            softly.assertThat(cursorPage.getContent()).hasSize(3);
            softly.assertThat(cursorPage.getNextCursorId()).isNull();
            softly.assertThat(cursorPage.getPageSize()).isEqualTo(pageSize);
        });
    }

    @Test
    @DisplayName("fetchedContent가 pageSize와 같을 때 올바른 CursorPage 생성")
    void constructor_ShouldReturnFalseHasNext_WhenFetchedContentSizeEqualsPageSize() {
        // given
        int pageSize = 5;
        List<TestLongIdentifiable> fetchedContent = createTestData(5);

        // when
        CursorPage<TestLongIdentifiable> cursorPage = new CursorPage<>(fetchedContent, pageSize);

        // then
        assertSoftly(softly -> {
            softly.assertThat(cursorPage.isHasNext()).isFalse();
            softly.assertThat(cursorPage.getContent()).hasSize(5);
            softly.assertThat(cursorPage.getNextCursorId()).isNull();
            softly.assertThat(cursorPage.getPageSize()).isEqualTo(pageSize);
        });
    }

    @Test
    @DisplayName("fetchedContent가 pageSize+1일 때 올바른 CursorPage 생성")
    void constructor_ShouldReturnTrueHasNext_WhenFetchedContentSizeEqualsPageSizePlusOne() {
        // given
        int pageSize = 5;
        List<TestLongIdentifiable> fetchedContent = createTestData(6);

        // when
        CursorPage<TestLongIdentifiable> cursorPage = new CursorPage<>(fetchedContent, pageSize);

        // then
        assertSoftly(softly -> {
            softly.assertThat(cursorPage.isHasNext()).isTrue();
            softly.assertThat(cursorPage.getContent()).hasSize(5);
            softly.assertThat(cursorPage.getNextCursorId()).isEqualTo(5L);
            softly.assertThat(cursorPage.getPageSize()).isEqualTo(pageSize);
        });
    }

    @Test
    @DisplayName("fetchedContent가 pageSize+2 이상일 때 예외 발생")
    void constructor_ShouldThrowException_WhenFetchedContentSizeGreaterThanPageSizePlusOne() {
        // given
        int pageSize = 5;
        List<TestLongIdentifiable> fetchedContent = createTestData(8);

        // when & then
        assertThatThrownBy(() -> new CursorPage<>(fetchedContent, pageSize))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("map 메서드로 타입 변환 시 올바른 CursorPage 생성")
    void map_ShouldReturnCorrectCursorPage_WhenTypeTransformation() {
        // given
        int pageSize = 3;
        List<TestLongIdentifiable> fetchedContent = createTestData(4);
        CursorPage<TestLongIdentifiable> originalPage = new CursorPage<>(fetchedContent, pageSize);

        // when
        CursorPage<MappedTestLongIdentifiable> mappedPage = originalPage.map(item ->
            new MappedTestLongIdentifiable(item.getId(), "mapped_" + item.getId()));

        // then
        assertSoftly(softly -> {
            softly.assertThat(mappedPage.getContent()).hasSize(3);
            softly.assertThat(mappedPage.isHasNext()).isTrue();
            softly.assertThat(mappedPage.getNextCursorId()).isEqualTo(3L);
            softly.assertThat(mappedPage.getPageSize()).isEqualTo(pageSize);
        });
    }

    @Test
    @DisplayName("빈 리스트로 CursorPage 생성 시 올바른 초기화")
    void constructor_ShouldReturnCorrectCursorPage_WhenEmptyList() {
        // given
        int pageSize = 5;
        List<TestLongIdentifiable> fetchedContent = List.of();

        // when
        CursorPage<TestLongIdentifiable> cursorPage = new CursorPage<>(fetchedContent, pageSize);

        // then
        assertSoftly(softly -> {
            softly.assertThat(cursorPage.isHasNext()).isFalse();
            softly.assertThat(cursorPage.getContent()).isEmpty();
            softly.assertThat(cursorPage.getNextCursorId()).isNull();
            softly.assertThat(cursorPage.getPageSize()).isEqualTo(pageSize);
        });
    }

    private static class TestLongIdentifiable implements LongIdentifiable {
        private final Long id;

        public TestLongIdentifiable(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }

    // map 테스트를 위한 추가 테스트용 클래스
    private static class MappedTestLongIdentifiable implements LongIdentifiable {
        private final Long id;
        private final String name;

        public MappedTestLongIdentifiable(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private List<TestLongIdentifiable> createTestData(int size) {
        return IntStream.rangeClosed(1, size)
                        .mapToObj(i -> new TestLongIdentifiable((long) i))
                        .toList();
    }
}
