package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.BookSource;
import com.game.adventure_book.domain.model.Difficulty;
import com.game.adventure_book.domain.model.SectionType;
import com.game.adventure_book.domain.model.ValidationErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An adventure book. Books are always persisted, even when invalid; validity is
 * tracked through {@link #getValidationErrors()} and the denormalised
 * {@code valid}/{@code errorCount} columns.
 */
@Entity
@Table(name = "book")
public class Book {

    @Id
    @Column(length = 100)
    private String id;

    @Column(nullable = false)
    private String title;

    private String author;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Difficulty difficulty;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private BookSource source;

    @Column(name = "source_json", columnDefinition = "text")
    private String sourceJson;

    @Column(name = "section_count", nullable = false)
    private int sectionCount;

    @Column(name = "ending_count", nullable = false)
    private int endingCount;

    @Column(nullable = false)
    private boolean valid;

    @Column(name = "error_count", nullable = false)
    private int errorCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<BookValidationError> validationErrors = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected Book() {
    }

    public Book(String id, String title, String author, Difficulty difficulty, BookSource source) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.author = author;
        this.difficulty = difficulty;
        this.source = Objects.requireNonNull(source, "source");
    }

    /** Replaces all sections, keeping file order and refreshing counters and story validation. */
    public void replaceSections(Collection<Section> newSections) {
        sections.clear();
        int position = 0;
        for (Section section : newSections) {
            section.attachTo(this, position++);
            sections.add(section);
        }
        sectionCount = sections.size();
        endingCount = (int) sections.stream().filter(s -> s.getType() == SectionType.END).count();
        refreshStoryValidation();
    }

    void refreshStoryValidation() {
        replaceValidationErrors(validationErrors.stream()
                .filter(error -> !isStoryError(error.getCode()))
                .toList());
    }

    /** Combines import diagnostics with the story rules; an invalid book remains storable. */
    public void replaceValidationErrors(Collection<BookValidationError> errors) {
        List<BookValidationError> report = new ArrayList<>(errors);
        for (BookValidationError storyError : BookValidator.validate(sections)) {
            if (report.stream().noneMatch(error -> error.getCode() == storyError.getCode()
                    && Objects.equals(error.getSectionNumber(), storyError.getSectionNumber())
                    && Objects.equals(error.getOptionIndex(), storyError.getOptionIndex())
                    && Objects.equals(error.getGotoSectionNumber(), storyError.getGotoSectionNumber()))) {
                report.add(storyError);
            }
        }
        validationErrors.clear();
        int position = 0;
        for (BookValidationError error : report) {
            error.attachTo(this, position++);
            validationErrors.add(error);
        }
        errorCount = validationErrors.size();
        valid = errorCount == 0;
    }

    private static boolean isStoryError(ValidationErrorCode code) {
        return code == ValidationErrorCode.NO_BEGINNING
                || code == ValidationErrorCode.MULTIPLE_BEGINNINGS
                || code == ValidationErrorCode.NO_ENDING
                || code == ValidationErrorCode.INVALID_GOTO
                || code == ValidationErrorCode.NODE_WITHOUT_OPTIONS
                || code == ValidationErrorCode.DUPLICATE_SECTION_ID;
    }

    /** The unique BEGIN section, if the book is valid. */
    public Optional<Section> findBeginning() {
        return sections.stream().filter(s -> s.getType() == SectionType.BEGIN).findFirst();
    }

    public Optional<Section> findSection(int sectionNumber) {
        return sections.stream().filter(s -> s.getSectionNumber() == sectionNumber).findFirst();
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "title");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public BookSource getSource() {
        return source;
    }

    public String getSourceJson() {
        return sourceJson;
    }

    public void setSourceJson(String sourceJson) {
        this.sourceJson = sourceJson;
    }

    public int getSectionCount() {
        return sectionCount;
    }

    public int getEndingCount() {
        return endingCount;
    }

    public boolean isValid() {
        return valid;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public List<BookValidationError> getValidationErrors() {
        return Collections.unmodifiableList(validationErrors);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Book other && id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Book.class.hashCode();
    }
}
