package com.game.adventure_book.domain.repository;

import com.game.adventure_book.domain.entity.Game;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/** Games. Use {@link GameSpecifications} with {@code findAll(spec, pageable)} for the resume list. */
public interface GameRepository extends JpaRepository<Game, UUID>, JpaSpecificationExecutor<Game> {

    /** Everything needed to render the game screen (header + current section + choices). */
    @EntityGraph(attributePaths = {"book", "currentSection", "currentSection.options"})
    Optional<Game> findWithStateById(UUID id);

    /** Game listing; book and current section are fetched to avoid N+1 queries. */
    @Override
    @EntityGraph(attributePaths = {"book", "currentSection"})
    Page<Game> findAll(Specification<Game> spec, Pageable pageable);
}
