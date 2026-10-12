package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.SectionType;
import com.game.adventure_book.domain.model.ValidationErrorCode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Checks the story rules on the normalized representation of a book. */
public final class BookValidator {

    private BookValidator() {
    }

    public static List<BookValidationError> validate(Collection<Section> sections) {
        List<BookValidationError> errors = new ArrayList<>();
        Set<Integer> ids = new HashSet<>();
        int beginnings = 0;
        int endings = 0;

        for (Section section : sections) {
            int number = section.getSectionNumber();
            if (!ids.add(number)) {
                errors.add(new BookValidationError(ValidationErrorCode.DUPLICATE_SECTION_ID,
                        "Section " + number + " has a duplicate id.", number, null, null));
            }
            if (section.getType() == SectionType.BEGIN) {
                beginnings++;
            } else if (section.getType() == SectionType.END) {
                endings++;
            }
            if (section.getType() != SectionType.END && section.getOptions().isEmpty()) {
                errors.add(new BookValidationError(ValidationErrorCode.NODE_WITHOUT_OPTIONS,
                        "Section " + number + " is not an END section but has no options.", number, null, null));
            }
        }

        if (beginnings == 0) {
            errors.add(BookValidationError.of(ValidationErrorCode.NO_BEGINNING, "Book has no BEGIN section."));
        } else if (beginnings > 1) {
            errors.add(BookValidationError.of(ValidationErrorCode.MULTIPLE_BEGINNINGS,
                    "Book has more than one BEGIN section."));
        }
        if (endings == 0) {
            errors.add(BookValidationError.of(ValidationErrorCode.NO_ENDING, "Book has no END section."));
        }

        for (Section section : sections) {
            for (SectionOption option : section.getOptions()) {
                if (!ids.contains(option.getGotoSectionNumber())) {
                    errors.add(new BookValidationError(ValidationErrorCode.INVALID_GOTO,
                            "Option " + option.getPosition() + " of section " + section.getSectionNumber()
                                    + " points to section " + option.getGotoSectionNumber() + ", which does not exist.",
                            section.getSectionNumber(), option.getPosition(), option.getGotoSectionNumber()));
                }
            }
        }
        return errors;
    }
}
