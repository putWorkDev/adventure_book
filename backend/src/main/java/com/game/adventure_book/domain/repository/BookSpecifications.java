package com.game.adventure_book.domain.repository;

import com.game.adventure_book.domain.entity.Book;
import com.game.adventure_book.domain.model.Difficulty;
import java.util.Collection;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

/** Composable filters for {@code GET /books}. Null / empty arguments mean "no filter". */
public final class BookSpecifications {

    private BookSpecifications() {
    }

    /** Case-insensitive "contains" match on title or author. */
    public static Specification<Book> search(String term) {
        if (term == null || term.isBlank()) {
            return unrestricted();
        }
        String pattern = "%" + escapeLike(term.trim().toLowerCase(Locale.ROOT)) + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern, '\\'),
                cb.like(cb.lower(root.get("author")), pattern, '\\'));
    }

    public static Specification<Book> difficultyIn(Collection<Difficulty> difficulties) {
        if (difficulties == null || difficulties.isEmpty()) {
            return unrestricted();
        }
        return (root, query, cb) -> root.get("difficulty").in(difficulties);
    }

    /** Case-insensitive exact match on author. */
    public static Specification<Book> authorEquals(String author) {
        if (author == null || author.isBlank()) {
            return unrestricted();
        }
        String normalized = author.trim().toLowerCase(Locale.ROOT);
        return (root, query, cb) -> cb.equal(cb.lower(root.get("author")), normalized);
    }

    public static Specification<Book> validEquals(Boolean valid) {
        if (valid == null) {
            return unrestricted();
        }
        return (root, query, cb) -> cb.equal(root.get("valid"), valid);
    }

    public static Specification<Book> matching(String search, Collection<Difficulty> difficulties,
                                               String author, Boolean valid) {
        return search(search)
                .and(difficultyIn(difficulties))
                .and(authorEquals(author))
                .and(validEquals(valid));
    }

    private static Specification<Book> unrestricted() {
        return (root, query, cb) -> cb.conjunction();
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
