package com.game.adventure_book.domain.model;

public enum GameStatus {
    IN_PROGRESS,
    PAUSED,
    COMPLETED,
    DIED,
    ABANDONED;

    public boolean isTerminal() {
        return this == COMPLETED || this == DIED || this == ABANDONED;
    }
}
