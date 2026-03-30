package Videoclub.controller;

import Videoclub.dto.VideoGameDTO;
import Videoclub.service.VideoGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class VideoGameController {

    private final VideoGameService gameService;

    @GetMapping
    public ResponseEntity<Page<VideoGameDTO>> listGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(gameService.getListGames(page, size, search));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<VideoGameDTO> getGameByID(@PathVariable UUID gameId) {
        return ResponseEntity.ok(gameService.getGameById(gameId));
    }
}
