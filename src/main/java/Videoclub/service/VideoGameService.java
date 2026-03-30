package Videoclub.service;

import Videoclub.dto.VideoGameDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface VideoGameService {

    Page<VideoGameDTO> getListGames(int page, int size, String search);

    VideoGameDTO getGameById(UUID gameId);
}
