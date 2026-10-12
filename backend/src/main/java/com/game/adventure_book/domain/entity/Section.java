package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.SectionType;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A numbered section of a book. {@code sectionNumber} is the {@code id} used in the
 * book JSON (and in {@code gotoId}); the primary key is a surrogate because section
 * numbers may be duplicated in invalid books.
 */
@Entity
@Table(name = "book_section")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "section_number", nullable = false)
    private int sectionNumber;

    @Column(nullable = false)
    private int position;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SectionType type;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<SectionOption> options = new ArrayList<>();

    protected Section() {
    }

    public Section(int sectionNumber, SectionType type, String text) {
        this.sectionNumber = sectionNumber;
        this.type = Objects.requireNonNull(type, "type");
        this.text = Objects.requireNonNull(text, "text");
    }

    void attachTo(Book book, int position) {
        this.book = book;
        this.position = position;
    }

    /** Replaces all options; the option position becomes its API {@code optionId}. */
    public void replaceOptions(Collection<SectionOption> newOptions) {
        options.clear();
        int position = 0;
        for (SectionOption option : newOptions) {
            option.attachTo(this, position++);
            options.add(option);
        }
        if (book != null) {
            book.refreshStoryValidation();
        }
    }

    public Optional<SectionOption> findOption(int optionIndex) {
        return options.stream().filter(o -> o.getPosition() == optionIndex).findFirst();
    }

    public boolean isEnding() {
        return type == SectionType.END;
    }

    public Long getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }

    public int getPosition() {
        return position;
    }

    public SectionType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public List<SectionOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Section other && id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Section.class.hashCode();
    }
}
