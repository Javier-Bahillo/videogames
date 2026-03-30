package Videoclub.repository;

import Videoclub.entity.GameCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VideoGameCopyRepository extends JpaRepository<GameCopy, UUID> {

     List<GameCopy> findByGame_Id(UUID gameId);
     java.util.Optional<GameCopy> findFirstByGame_IdAndStatus(UUID gameId, Videoclub.entity.VideoGameCopyStatus status);
     long countByGame_Id(UUID gameId);
     long countByGame_IdAndStatus(UUID gameId, Videoclub.entity.VideoGameCopyStatus status);

}