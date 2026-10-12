package com.game.adventure_book.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

/**
 * A choice at the end of a section. {@code gotoSectionNumber} is intentionally not a
 * foreign key: invalid books with dangling targets must still be storable.
 */
@Entity
@Table(name = "section_option")
public class SectionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private int position;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "goto_section_number", nullable = false)
    private int gotoSectionNumber;

    /** Null when the option has no consequence. */
    @Embedded
    private Consequence consequence;

    protected SectionOption() {
    }

    public SectionOption(String description, int gotoSectionNumber, Consequence consequence) {
        this.description = Objects.requireNonNull(description, "description");
        this.gotoSectionNumber = gotoSectionNumber;
        this.consequence = consequence;
    }

    void attachTo(Section section, int position) {
        this.section = section;
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public Section getSection() {
        return section;
    }

    public int getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }

    public int getGotoSectionNumber() {
        return gotoSectionNumber;
    }

    public Consequence getConsequence() {
        return consequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof SectionOption other && id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return SectionOption.class.hashCode();
    }
}
