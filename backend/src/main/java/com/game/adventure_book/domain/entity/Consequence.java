package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.ConsequenceType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.Objects;

/** Effect of picking an option (e.g. losing health in a fall). */
@Embeddable
public class Consequence {
    @Enumerated(EnumType.STRING)
    @Column(name = "consequence_type", length = 20)
    private ConsequenceType type;

    @Column(name = "consequence_value")
    private Integer value;

    @Column(name = "consequence_text", length = 1000)
    private String text;

    protected Consequence() {
    }

    public Consequence(ConsequenceType type, int value, String text) {
        if (value <= 0) {
            throw new IllegalArgumentException("Consequence value must be positive, got " + value);
        }
        this.type = Objects.requireNonNull(type, "type");
        this.value = value;
        this.text = text;
    }

    /** Health after applying this consequence, clamped to {@code [0, maxHealth]}. */
    public int applyTo(int health, int maxHealth) {
        int result = type == ConsequenceType.LOSE_HEALTH ? health - value : health + value;
        return Math.clamp(result, 0, maxHealth);
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
}
