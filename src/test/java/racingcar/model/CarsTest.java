package racingcar.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import racingcar.exception.car_racing_manager.NotExistCarException;
import racingcar.exception.car_racing_manager.NotUniqueCarNameException;
import racingcar.helper.TestNumberGenerator;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CarsTest {

    private Cars cars;

    @BeforeEach
    void init() {
        cars = Cars.createWith(List.of("test1", "test2", "test3", "test4"));
    }

    @MethodSource("moveResultProvider")
    @ParameterizedTest
    void 자동차들을_전진시키고_자동차가_전진_조건을_만족하면_전진한다(int testNumber, List<Integer> expected) {
        // when
        cars.moveAll(new TestNumberGenerator(testNumber));

        // then
        assertThat(cars.getResult().values()).containsExactlyElementsOf(expected);
    }

    private static Stream<Arguments> moveResultProvider() {
        return Stream.of(
                Arguments.of(1, List.of(0, 0, 0, 0)),
                Arguments.of(4, List.of(1, 1, 1, 1)),
                Arguments.of(7, List.of(1, 1, 1, 1)));
    }

    @Test
    void 게임의_참여한_모든_자동차의_이름과_자동차의_현재_위치를_반환한다() {
        // when
        Map<String, Integer> result = cars.getResult();

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(4);
            softly.assertThat(result.keySet()).containsExactly("test1", "test2", "test3", "test4");
            softly.assertThat(result.values()).containsExactly(0, 0, 0, 0);
        });
    }

    @Test
    void 게임에_우승자를_반환한다() {
        // given
        cars.moveAll(new TestNumberGenerator(6));

        // when
        List<String> winners = cars.getWinner();

        // then
        assertSoftly(softly -> {
            softly.assertThat(winners).hasSize(4);
            softly.assertThat(winners).containsExactly("test1", "test2", "test3", "test4");
        });
    }

    @Test
    void 게임의_참여하는_자동차_중에_중복된_이름이_존재하면_예외를_발생시킨다() {
        // when & then
        assertThatThrownBy(() -> Cars.createWith(List.of("test1", "test1", "test2")))
                .isInstanceOf(NotUniqueCarNameException.class);
    }

    @Test
    void 게임의_참여중인_자동차가_없으면_예외를_발생시킨다() {
        // when & then
        assertThatThrownBy(() -> Cars.createWith(List.of()))
               .isInstanceOf(NotExistCarException.class);
    }
}
