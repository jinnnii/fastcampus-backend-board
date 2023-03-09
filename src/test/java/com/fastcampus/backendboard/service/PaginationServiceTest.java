package com.fastcampus.backendboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@DisplayName("Business Logic - Pagination")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = PaginationService.class)
class PaginationServiceTest {
    private final PaginationService sut;


    PaginationServiceTest(@Autowired PaginationService paginationService) {
        this.sut = paginationService;
    }

    @DisplayName("현재 페이지 번호와 총 페이지 수를 주면 페이징 바 리스트를 생성한다.")
    @MethodSource
    @ParameterizedTest(name = "[{index}] {0},{1} -> {2}")
    void givenCurrAndTotalPageNumber_whenCalculating_thenReturnsPaginationBarNumbers(int current, int total, List<Integer> expected) {
        //Given

        //When
        List<Integer> actual = sut.getPaginationBarNumbers(current,total);
        //Then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> givenCurrAndTotalPageNumber_whenCalculating_thenReturnsPaginationBarNumbers() {
        return Stream.of(
                arguments(0,13,List.of(0,1,2,3,4)),
                arguments(1,13,List.of(0,1,2,3,4)),
                arguments(2,13,List.of(0,1,2,3,4)),
                arguments(3,13,List.of(1,2,3,4,5)),
                arguments(4,13,List.of(2,3,4,5,6)),
                arguments(10,13,List.of(8,9,10,11,12)),
                arguments(11,13,List.of(9,10,11,12)),
                arguments(12,13,List.of(10,11,12))
        );
    }

    @DisplayName("현재 설정되어있는 페이징 바 길이를 알려준다.")
    @Test
    void testReturnBarLength() {
        //Given

        //When
        int barLength = sut.getBarLength();

        //Then
        assertThat(barLength).isEqualTo(5);

    }
}
