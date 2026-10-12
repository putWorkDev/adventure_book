package com.game.adventure_book.domain.repository;

import com.game.adventure_book.domain.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * Books. Use {@link BookSpecifications} with {@code findAll(spec, pageable)} for
 * search / filter / sort on the home page.
 */
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = "validationErrors")
    Optional<Book> findWithValidationErrorsById(String id);

    /**
     * Book with its sections (editor view, validation, starting a game). Section options
     * are lazy and loaded in batches ({@code hibernate.default_batch_fetch_size}) to avoid
     * a cartesian product between collections.
     */
    @EntityGraph(attributePaths = "sections")
    Optional<Book> findWithSectionsById(String id);

    /** Distinct authors, e.g. to populate the author filter. */
    @Query("select distinct b.author from Book b where b.author is not null order by b.author")
    List<String> findDistinctAuthors();
}
