package com.game.adventure_book.domain.entity;

import jakarta.persistence.AttributeOverride;
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
import java.time.Instant;
import java.util.Objects;

/** One choice made during a game (history entry). Immutable once created. */
@Entity
@Table(name = "game_step")
public class GameStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false, updatable = false)
    private Game game;

    @Column(nullable = false, updatable = false)
    private int step;

    @Column(name = "from_section_number", nullable = false, updatable = false)
    private int fromSectionNumber;

    @Column(name = "option_index", nullable = false, updatable = false)
    private int optionIndex;

    @Column(name = "option_description", nullable = false, length = 1000, updatable = false)
    private String optionDescription;

    @Column(name = "to_section_number", nullable = false, updatable = false)
    private int toSectionNumber;

    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "consequence_type", length = 20, updatable = false))
    @AttributeOverride(name = "value", column = @Column(name = "consequence_value", updatable = false))
    @AttributeOverride(name = "text", column = @Column(name = "consequence_text", length = 1000, updatable = false))
    @AttributeOverride(name = "healthBefore", column = @Column(name = "consequence_health_before", updatable = false))
    @AttributeOverride(name = "healthAfter", column = @Column(name = "consequence_health_after", updatable = false))
    private AppliedConsequence consequence;

    @Column(name = "health_after", nullable = false, updatable = false)
    private int healthAfter;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected GameStep() {
    }

    GameStep(Game game, int step, int fromSectionNumber, SectionOption option, int toSectionNumber,
             AppliedConsequence consequence, int healthAfter, Instant createdAt) {
        this.game = Objects.requireNonNull(game, "game");
        this.step = step;
        this.fromSectionNumber = fromSectionNumber;
        this.optionIndex = option.getPosition();
        this.optionDescription = option.getDescription();
        this.toSectionNumber = toSectionNumber;
        this.consequence = consequence;
        this.healthAfter = healthAfter;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public int getStep() {
        return step;
    }

    public int getFromSectionNumber() {
        return fromSectionNumber;
    }

    public int getOptionIndex() {
        return optionIndex;
    }

    public String getOptionDescription() {
        return optionDescription;
    }

    public int getToSectionNumber() {
        return toSectionNumber;
    }

    public AppliedConsequence getConsequence() {
        return consequence;
    }

    public int getHealthAfter() {
        return healthAfter;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof GameStep other && id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return GameStep.class.hashCode();
    }
}
