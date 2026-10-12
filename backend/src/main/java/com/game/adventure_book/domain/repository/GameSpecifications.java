package com.game.adventure_book.domain.repository;

import com.game.adventure_book.domain.entity.Game;
import com.game.adventure_book.domain.model.GameStatus;
import java.util.Collection;
import org.springframework.data.jpa.domain.Specification;

/** Composable filters for {@code GET /games}. Null / empty arguments mean "no filter". */
public final class GameSpecifications {

    private GameSpecifications() {
    }

    public static Specification<Game> bookIdEquals(String bookId) {
        if (bookId == null || bookId.isBlank()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("book").get("id"), bookId);
    }

    public static Specification<Game> statusIn(Collection<GameStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    public static Specification<Game> matching(String bookId, Collection<GameStatus> statuses) {
        return bookIdEquals(bookId).and(statusIn(statuses));
    }
}
