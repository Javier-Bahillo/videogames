package Videoclub.controller;

import Videoclub.dto.VideoGameDTO;
import Videoclub.dto.gamesdb.GamesDbGameResult;
import Videoclub.dto.gamesdb.GamesDbPlatformResult;
import Videoclub.service.AdminService;
import Videoclub.service.TheGamesDbService;
import Videoclub.service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/gamesdb")
@RequiredArgsConstructor
public class TheGamesDbController {

    private final TheGamesDbService theGamesDbService;
    private final TranslationService translationService;
    private final AdminService adminService;

    @GetMapping("/search")
    public List<GamesDbGameResult> searchGames(@RequestParam String name) {
        return theGamesDbService.searchGames(name);
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<GamesDbGameResult> getGame(@PathVariable int id) {
        GamesDbGameResult game = theGamesDbService.getGameById(id);
        return game != null ? ResponseEntity.ok(game) : ResponseEntity.notFound().build();
    }

    @GetMapping("/platforms")
    public List<GamesDbPlatformResult> getPlatforms() {
        return theGamesDbService.getPlatforms();
    }

    @GetMapping("/translate")
    public Map<String, String> translate(
            @RequestParam String text,
            @RequestParam(defaultValue = "es") String lang) {
        return Map.of("translated", translationService.translate(text, lang));
    }

    @PostMapping("/import/{gamedbId}")
    public ResponseEntity<VideoGameDTO> importGame(
            @PathVariable int gamedbId,
            @RequestParam(defaultValue = "3.99") double rentalPrice,
            @RequestParam(defaultValue = "1") int copies) {

        GamesDbGameResult game = theGamesDbService.getGameById(gamedbId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        String overview = game.getOverview() != null && !game.getOverview().isBlank()
                ? game.getOverview() : null;
        String translated = overview != null
                ? translationService.translateToSpanish(overview)
                : "Sin descripción";
        String descriptionEs = translated.length() > 250
                ? translated.substring(0, 250) : translated;

        VideoGameDTO dto = VideoGameDTO.builder()
                .title(game.getTitle())
                .description(descriptionEs)
                .platform(game.getPlatform() != null && !game.getPlatform().isBlank()
                        ? game.getPlatform() : "Desconocida")
                .genre(game.getGenre() != null && !game.getGenre().isBlank()
                        ? game.getGenre() : "Sin categoría")
                .producer(game.getProducer() != null && !game.getProducer().isBlank()
                        ? game.getProducer() : "Desconocido")
                .pegi(game.getRating() != null && !game.getRating().isBlank()
                        ? game.getRating() : "Sin clasificar")
                .releaseDate(parseDate(game.getReleaseDate()))
                .rentalPrice(BigDecimal.valueOf(rentalPrice))
                .coverUrl(game.getCoverUrl() != null ? game.getCoverUrl() : "")
                .active(true)
                .build();

        VideoGameDTO created = adminService.createGame(dto);
        if (copies > 0) {
            adminService.createCopies(created.getId(), copies);
        }
        return ResponseEntity.ok(created);
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return LocalDate.now();
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            try {
                int year = Integer.parseInt(dateStr.trim().substring(0, 4));
                return LocalDate.of(year, 1, 1);
            } catch (Exception ex) {
                return LocalDate.now();
            }
        }
    }
}
