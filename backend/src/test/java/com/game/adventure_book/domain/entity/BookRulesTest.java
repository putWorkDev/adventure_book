package com.game.adventure_book.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.game.adventure_book.domain.model.BookSource;
import com.game.adventure_book.domain.model.ConsequenceType;
import com.game.adventure_book.domain.model.Difficulty;
import com.game.adventure_book.domain.model.GameStatus;
import com.game.adventure_book.domain.model.SectionType;
import com.game.adventure_book.domain.model.ValidationErrorCode;
import java.util.List;
import org.junit.jupiter.api.Test;

class BookRulesTest {

    @Test
    void reportsEveryStoryRuleAndDoesNotAllowPlayingInvalidBooks() {
        Book book = book();
        book.replaceSections(List.of(
                section(1, SectionType.BEGIN, new SectionOption("Missing", 999, null)),
                section(1, SectionType.BEGIN, new SectionOption("Ending", 2, null)),
                section(3, SectionType.NODE)));
        book.replaceValidationErrors(List.of());

        assertThat(book.getValidationErrors()).extracting(BookValidationError::getCode)
                .contains(ValidationErrorCode.MULTIPLE_BEGINNINGS, ValidationErrorCode.NO_ENDING,
                        ValidationErrorCode.INVALID_GOTO, ValidationErrorCode.NODE_WITHOUT_OPTIONS,
                        ValidationErrorCode.DUPLICATE_SECTION_ID);
        assertThat(book.getErrorCount()).isEqualTo(book.getValidationErrors().size());
        assertThat(book.isValid()).isFalse();
        assertThatThrownBy(() -> Game.start(book)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void missingBeginningAndEmptyUnparseableBookAreReported() {
        Book book = book();
        book.replaceSections(List.of(section(2, SectionType.END)));
        assertThat(book.getValidationErrors()).extracting(BookValidationError::getCode)
                .containsExactly(ValidationErrorCode.NO_BEGINNING);

        Book empty = book();
        empty.replaceValidationErrors(List.of(
                BookValidationError.of(ValidationErrorCode.UNPARSEABLE, "The file is empty.")));
        assertThat(empty.getValidationErrors()).extracting(BookValidationError::getCode)
                .contains(ValidationErrorCode.UNPARSEABLE, ValidationErrorCode.NO_BEGINNING,
                        ValidationErrorCode.NO_ENDING);
        assertThatThrownBy(() -> Game.start(empty)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void normalizesTargetsAsNumbersAndRevalidatesAfterSectionReplacement() {
        Book book = book();
        book.replaceSections(List.of(
                section(1, SectionType.BEGIN, new SectionOption("Go", 500, null)),
                section(500, SectionType.END)));
        book.replaceValidationErrors(List.of());
        assertThat(book.isValid()).isTrue();
        assertThat(book.findBeginning()).isPresent();
        assertThat(book.findSection(500)).isPresent();

        book.replaceSections(List.of(section(1, SectionType.BEGIN, new SectionOption("Go", 500, null))));
        assertThat(book.isValid()).isFalse();
        assertThat(book.getValidationErrors()).extracting(BookValidationError::getCode)
                .contains(ValidationErrorCode.NO_ENDING, ValidationErrorCode.INVALID_GOTO);
        book.replaceSections(List.of(
                section(1, SectionType.BEGIN, new SectionOption("Go", 500, null)),
                section(500, SectionType.END)));
        assertThat(book.isValid()).isTrue();
        assertThat(book.getErrorCount()).isZero();

        book.findBeginning().orElseThrow().replaceOptions(List.of());
        assertThat(book.isValid()).isFalse();
        assertThat(book.getValidationErrors()).extracting(BookValidationError::getCode)
                .containsExactly(ValidationErrorCode.NODE_WITHOUT_OPTIONS);
    }

    @Test
    void gameAppliesConsequencesClampsHealthAndHonorsDeathOverEnding() {
        Book book = book();
        Section begin = section(1, SectionType.BEGIN,
                new SectionOption("Huge heal", 2,
                        new Consequence(ConsequenceType.GAIN_HEALTH, Integer.MAX_VALUE, null)),
                new SectionOption("Fatal", 2, new Consequence(ConsequenceType.LOSE_HEALTH, 11, null)));
        Section end = section(2, SectionType.END);
        book.replaceSections(List.of(begin, end));

        Game alive = Game.start(book);
        alive.choose(0, end);
        assertThat(alive.getHealth()).isEqualTo(10);
        assertThat(alive.getStatus()).isEqualTo(GameStatus.COMPLETED);

        Game dead = Game.start(book);
        dead.choose(1, end);
        assertThat(dead.getHealth()).isZero();
        assertThat(dead.getStatus()).isEqualTo(GameStatus.DIED);
        assertThat(dead.getFinishedAt()).isNotNull();
        assertThatThrownBy(() -> dead.choose(0, end)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void gameCanPauseSaveAndResumeButCannotChooseWhilePaused() {
        Book book = book();
        Section begin = section(1, SectionType.BEGIN, new SectionOption("Continue", 2, null));
        Section end = section(2, SectionType.END);
        book.replaceSections(List.of(begin, end));

        Game game = Game.start(book);
        game.pause();
        game.save();
        assertThat(game.getSavedAt()).isNotNull();
        assertThatThrownBy(() -> game.choose(0, end)).isInstanceOf(IllegalStateException.class);
        game.resume();
        game.choose(0, end);
        assertThat(game.getStatus()).isEqualTo(GameStatus.COMPLETED);
    }

    private static Book book() {
        return new Book("example", "Example", "Author", Difficulty.EASY, BookSource.UPLOAD);
    }

    private static Section section(int number, SectionType type, SectionOption... options) {
        Section section = new Section(number, type, "Story text");
        section.replaceOptions(List.of(options));
        return section;
    }
}
