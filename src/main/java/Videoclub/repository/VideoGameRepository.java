package Videoclub.repository;

import Videoclub.entity.VideoGame;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VideoGameRepository extends JpaRepository<VideoGame, UUID> {

    Optional<VideoGame> findById(UUID gameId);
    boolean existsByTitle(@NotBlank(message = "Title is required") String title);
    Page<VideoGame> findByActiveTrue(Pageable pageable);
    Page<VideoGame> findByActiveTrueAndTitleContainingIgnoreCase(String title, Pageable pageable);

}
