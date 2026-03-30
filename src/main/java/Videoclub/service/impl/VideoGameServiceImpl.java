package Videoclub.service.impl;

import Videoclub.dto.VideoGameDTO;
import Videoclub.entity.VideoGameCopyStatus;
import Videoclub.exception.ResourceNotFoundException;
import Videoclub.mapper.VideoGameMapper;
import Videoclub.entity.VideoGame;
import Videoclub.repository.VideoGameCopyRepository;
import Videoclub.repository.VideoGameRepository;
import Videoclub.service.VideoGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoGameServiceImpl implements VideoGameService {

    private final VideoGameRepository videoGameRepository;
    private final VideoGameMapper videoGameMapper;
    private final VideoGameCopyRepository videoGameCopyRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<VideoGameDTO> getListGames(int page, int size, String search) {
        var pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<VideoGame> games = (search != null && !search.isBlank())
                ? videoGameRepository.findByActiveTrueAndTitleContainingIgnoreCase(search.trim(), pageable)
                : videoGameRepository.findByActiveTrue(pageable);
        return games.map(game -> {
            VideoGameDTO dto = videoGameMapper.toDto(game);
            dto.setAvailableCopies((int) videoGameCopyRepository
                    .countByGame_IdAndStatus(game.getId(), VideoGameCopyStatus.AVAILABLE));
            dto.setTotalCopies((int) videoGameCopyRepository.countByGame_Id(game.getId()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public VideoGameDTO getGameById(UUID gameId) {
        VideoGame game = videoGameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));
        VideoGameDTO dto = videoGameMapper.toDto(game);
        dto.setAvailableCopies((int) videoGameCopyRepository
                .countByGame_IdAndStatus(gameId, VideoGameCopyStatus.AVAILABLE));
        dto.setTotalCopies((int) videoGameCopyRepository.countByGame_Id(gameId));
        return dto;
    }
}
