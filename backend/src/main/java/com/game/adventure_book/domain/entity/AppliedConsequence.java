package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.ConsequenceType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Snapshot of a consequence that was actually applied during a game, with the
 * health before/after (health is clamped, so the delta may differ from {@code value}).
 * Column names are overridden by the owning entity.
 */
@Embeddable
public class AppliedConsequence {
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ConsequenceType type;

    private Integer value;

    @Column(length = 1000)
    private String text;

    private Integer healthBefore;

    private Integer healthAfter;

    protected AppliedConsequence() {
    }

    public AppliedConsequence(Consequence consequence, int healthBefore, int healthAfter) {
        this.type = consequence.getType();
        this.value = consequence.getValue();
        this.text = consequence.getText();
        this.healthBefore = healthBefore;
        this.healthAfter = healthAfter;
    }

    public ConsequenceType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public int getHealthBefore() {
        return healthBefore;
    }

    public int getHealthAfter() {
        return healthAfter;
    }
}
