package com.game.adventure_book.domain.repository;

import com.game.adventure_book.domain.entity.Section;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, Long> {

    /**
     * Section of a book by its JSON id. Only unambiguous for valid books
     * (invalid books may contain duplicate section numbers).
     */
    @EntityGraph(attributePaths = "options")
    Optional<Section> findByBookIdAndSectionNumber(String bookId, int sectionNumber);

    @EntityGraph(attributePaths = "options")
    List<Section> findByBookIdOrderByPositionAsc(String bookId);
}
