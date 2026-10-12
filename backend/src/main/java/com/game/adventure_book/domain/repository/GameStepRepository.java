package com.game.adventure_book.domain.repository;

import com.game.adventure_book.domain.entity.GameStep;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameStepRepository extends JpaRepository<GameStep, Long> {

    List<GameStep> findByGameIdOrderByStepAsc(UUID gameId);
}
