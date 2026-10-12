package com.game.adventure_book.domain.model;

public enum ValidationErrorCode {
    NO_BEGINNING,
    MULTIPLE_BEGINNINGS,
    NO_ENDING,
    INVALID_GOTO,
    NODE_WITHOUT_OPTIONS,
    DUPLICATE_SECTION_ID,
    /** Consequence with an unknown type or a value that is not a positive integer. */
    INVALID_CONSEQUENCE,
    UNPARSEABLE
}
