package com.game.adventure_book.domain.entity;

import com.game.adventure_book.domain.model.GameStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A play-through of a book. Progress is persisted on every move; {@code savedAt}
 * marks the last explicit save point.
 *
 * <p>Rules: the player starts with {@value #DEFAULT_MAX_HEALTH} health on the BEGIN
 * section. A choice applies its consequence first (health clamped to
 * {@code [0, maxHealth]}), then moves to the target section. Health 0 ends the game
 * as {@link GameStatus#DIED}, which takes priority over reaching an END section
 * ({@link GameStatus#COMPLETED}).
 */
@Entity
@Table(name = "game")
public class Game {

    public static final int DEFAULT_MAX_HEALTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false, updatable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "current_section_id", nullable = false)
    private Section currentSection;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GameStatus status;

    @Column(nullable = false)
    private int health;

    @Column(name = "max_health", nullable = false, updatable = false)
    private int maxHealth;

    @Column(name = "step_count", nullable = false)
    private int stepCount;

    @Embedded
    @AttributeOverride(name = "type", column = @Column(name = "last_consequence_type", length = 20))
    @AttributeOverride(name = "value", column = @Column(name = "last_consequence_value"))
    @AttributeOverride(name = "text", column = @Column(name = "last_consequence_text", length = 1000))
    @AttributeOverride(name = "healthBefore", column = @Column(name = "last_consequence_health_before"))
    @AttributeOverride(name = "healthAfter", column = @Column(name = "last_consequence_health_after"))
    private AppliedConsequence lastConsequence;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("step ASC")
    private List<GameStep> steps = new ArrayList<>();

    @Column(name = "started_at", nullable = false, updatable = false)
    private Instant startedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "saved_at")
    private Instant savedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Version
    private Long version;

    protected Game() {
    }

    private Game(Book book, Section beginning, int maxHealth) {
        this.book = book;
        this.currentSection = beginning;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.status = GameStatus.IN_PROGRESS;
    }

    /** Starts a new game on the book's BEGIN section with full health. */
    public static Game start(Book book) {
        Objects.requireNonNull(book, "book");
        if (!book.isValid()) {
            throw new IllegalStateException("Book '" + book.getId() + "' is invalid and cannot be played");
        }
        Section beginning = book.findBeginning()
                .orElseThrow(() -> new IllegalStateException("Book '" + book.getId() + "' has no BEGIN section"));
        return new Game(book, beginning, DEFAULT_MAX_HEALTH);
    }

    /**
     * Applies the option at {@code optionIndex} of the current section and moves to
     * {@code target}, which must be the section the option points to.
     *
     * @return the recorded history entry
     */
    public GameStep choose(int optionIndex, Section target) {
        requireStatus(GameStatus.IN_PROGRESS, "make a choice");
        SectionOption option = currentSection.findOption(optionIndex)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Option " + optionIndex + " doesn't exist in section " + currentSection.getSectionNumber()));
        Objects.requireNonNull(target, "target");
        if (target.getSectionNumber() != option.getGotoSectionNumber() || !book.equals(target.getBook())) {
            throw new IllegalArgumentException("Target section " + target.getSectionNumber()
                    + " doesn't match option gotoId " + option.getGotoSectionNumber());
        }

        Instant now = Instant.now();
        int healthBefore = health;
        AppliedConsequence applied = null;
        if (option.getConsequence() != null) {
            health = option.getConsequence().applyTo(health, maxHealth);
            applied = new AppliedConsequence(option.getConsequence(), healthBefore, health);
        }

        int fromSectionNumber = currentSection.getSectionNumber();
        currentSection = target;
        lastConsequence = applied;
        stepCount++;
        updatedAt = now;

        if (health == 0) {
            finish(GameStatus.DIED, now);
        } else if (target.isEnding()) {
            finish(GameStatus.COMPLETED, now);
        }

        GameStep step = new GameStep(this, stepCount, fromSectionNumber, option,
                target.getSectionNumber(), applied, health, now);
        steps.add(step);
        return step;
    }

    public void pause() {
        requireStatus(GameStatus.IN_PROGRESS, "pause");
        status = GameStatus.PAUSED;
        updatedAt = Instant.now();
    }

    public void resume() {
        requireStatus(GameStatus.PAUSED, "resume");
        status = GameStatus.IN_PROGRESS;
        updatedAt = Instant.now();
    }

    public void stop() {
        if (status != GameStatus.IN_PROGRESS && status != GameStatus.PAUSED) {
            throw invalidTransition("stop");
        }
        finish(GameStatus.ABANDONED, Instant.now());
    }

    public void save() {
        if (status != GameStatus.IN_PROGRESS && status != GameStatus.PAUSED) {
            throw invalidTransition("save");
        }
        Instant now = Instant.now();
        savedAt = now;
        updatedAt = now;
    }

    private void finish(GameStatus terminalStatus, Instant now) {
        status = terminalStatus;
        finishedAt = now;
        updatedAt = now;
    }

    private void requireStatus(GameStatus expected, String action) {
        if (status != expected) {
            throw invalidTransition(action);
        }
    }

    private IllegalStateException invalidTransition(String action) {
        return new IllegalStateException("Cannot " + action + " a game that is " + status);
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (startedAt == null) {
            startedAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    public UUID getId() {
        return id;
    }

    public Book getBook() {
        return book;
    }

    public Section getCurrentSection() {
        return currentSection;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getStepCount() {
        return stepCount;
    }

    public AppliedConsequence getLastConsequence() {
        return lastConsequence;
    }

    public List<GameStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getSavedAt() {
        return savedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Game other && id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Game.class.hashCode();
    }
}
