package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.ValidationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/** One entry of a book's validation report. */
@Entity
@Table(name = "book_validation_error")
public class BookValidationError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int position;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ValidationErrorCode code;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "section_number")
    private Integer sectionNumber;

    @Column(name = "option_index")
    private Integer optionIndex;

    @Column(name = "goto_section_number")
    private Integer gotoSectionNumber;

    protected BookValidationError() {
    }

    public BookValidationError(ValidationErrorCode code, String message,
                               Integer sectionNumber, Integer optionIndex, Integer gotoSectionNumber) {
        this.code = Objects.requireNonNull(code, "code");
        this.message = Objects.requireNonNull(message, "message");
        this.sectionNumber = sectionNumber;
        this.optionIndex = optionIndex;
        this.gotoSectionNumber = gotoSectionNumber;
    }

    public static BookValidationError of(ValidationErrorCode code, String message) {
        return new BookValidationError(code, message, null, null, null);
    }

    void attachTo(Book book, int position) {
        this.book = book;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public int getPosition() {
        return position;
    }

    public ValidationErrorCode getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Integer getSectionNumber() {
        return sectionNumber;
    }

    public Integer getOptionIndex() {
        return optionIndex;
    }

    public Integer getGotoSectionNumber() {
        return gotoSectionNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof BookValidationError other && id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return BookValidationError.class.hashCode();
    }
}
