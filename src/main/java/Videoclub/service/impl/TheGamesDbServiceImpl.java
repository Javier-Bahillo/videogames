package Videoclub.service.impl;

import Videoclub.dto.gamesdb.GamesDbGameResult;
import Videoclub.dto.gamesdb.GamesDbGenresResponse;
import Videoclub.dto.gamesdb.GamesDbPlatformResult;
import Videoclub.dto.gamesdb.GamesDbPlatformsResponse;
import Videoclub.dto.gamesdb.GamesDbPublishersResponse;
import Videoclub.dto.gamesdb.GamesDbSearchResponse;
import Videoclub.service.TheGamesDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheGamesDbServiceImpl implements TheGamesDbService {

    @Value("${thegamesdb.api.key}")
    private String apiKey;

    @Value("${thegamesdb.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    @Override
    public List<GamesDbGameResult> searchGames(String name) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/Games/ByGameName")
                .queryParam("apikey", apiKey)
                .queryParam("name", name)
                .queryParam("fields", "overview,rating,genres,publishers")
                .queryParam("include", "boxart,platform")
                .build(false)
                .toUriString();

        GamesDbSearchResponse response = restTemplate.getForObject(url, GamesDbSearchResponse.class);
        return mapGames(response, fetchGenres(), fetchPublishers());
    }

    @Override
    public GamesDbGameResult getGameById(int gameId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/Games/ByGameID")
                .queryParam("apikey", apiKey)
                .queryParam("id", gameId)
                .queryParam("fields", "overview,rating,genres,publishers")
                .queryParam("include", "boxart,platform")
                .build(false)
                .toUriString();

        GamesDbSearchResponse response = restTemplate.getForObject(url, GamesDbSearchResponse.class);
        List<GamesDbGameResult> results = mapGames(response, fetchGenres(), fetchPublishers());
        return results.isEmpty() ? null : results.get(0);
    }

    private Map<String, String> fetchGenres() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/Genres")
                    .queryParam("apikey", apiKey)
                    .build(false)
                    .toUriString();
            GamesDbGenresResponse resp = restTemplate.getForObject(url, GamesDbGenresResponse.class);
            if (resp == null || resp.getData() == null || resp.getData().getGenres() == null) return Map.of();
            Map<String, String> result = new java.util.HashMap<>();
            resp.getData().getGenres().forEach((k, v) -> result.put(k, v.getName()));
            return result;
        } catch (Exception e) {
            log.error("Error fetching genres from TheGamesDB: {}", e.getMessage());
            return Map.of();
        }
    }

    private Map<String, String> fetchPublishers() {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/Publishers")
                    .queryParam("apikey", apiKey)
                    .build(false)
                    .toUriString();
            GamesDbPublishersResponse resp = restTemplate.getForObject(url, GamesDbPublishersResponse.class);
            if (resp == null || resp.getData() == null || resp.getData().getPublishers() == null) return Map.of();
            Map<String, String> result = new java.util.HashMap<>();
            resp.getData().getPublishers().forEach((k, v) -> result.put(k, v.getName()));
            return result;
        } catch (Exception e) {
            log.error("Error fetching publishers from TheGamesDB: {}", e.getMessage());
            return Map.of();
        }
    }

    @Override
    public List<GamesDbPlatformResult> getPlatforms() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/Platforms")
                .queryParam("apikey", apiKey)
                .build(false)
                .toUriString();

        GamesDbPlatformsResponse response = restTemplate.getForObject(url, GamesDbPlatformsResponse.class);
        if (response == null || response.getData() == null || response.getData().getPlatforms() == null) {
            return List.of();
        }

        return response.getData().getPlatforms().values().stream()
                .map(p -> new GamesDbPlatformResult(p.getId(), p.getName(), p.getAlias()))
                .sorted(Comparator.comparing(GamesDbPlatformResult::getName))
                .collect(Collectors.toList());
    }

    private List<GamesDbGameResult> mapGames(GamesDbSearchResponse response,
                                              Map<String, String> genreMap,
                                              Map<String, String> publisherMap) {
        if (response == null || response.getData() == null || response.getData().getGames() == null) {
            return List.of();
        }

        List<GamesDbSearchResponse.RawGame> games = response.getData().getGames();
        GamesDbSearchResponse.IncludeSection include = response.getInclude();

        String thumbBase = "";
        Map<String, List<GamesDbSearchResponse.BoxartItem>> boxartData = Map.of();
        Map<String, GamesDbSearchResponse.PlatformItem> platformData = Map.of();

        if (include != null) {
            if (include.getBoxart() != null) {
                if (include.getBoxart().getBaseUrl() != null && include.getBoxart().getBaseUrl().getThumb() != null) {
                    thumbBase = include.getBoxart().getBaseUrl().getThumb();
                }
                if (include.getBoxart().getData() != null) {
                    boxartData = include.getBoxart().getData();
                }
            }
            if (include.getPlatform() != null && include.getPlatform().getData() != null) {
                platformData = include.getPlatform().getData();
            }
        }

        List<GamesDbGameResult> results = new ArrayList<>();
        for (GamesDbSearchResponse.RawGame game : games) {
            String coverUrl    = resolveCover(thumbBase, boxartData, game.getId());
            String platformName = resolvePlatform(platformData, game.getPlatform());
            String genre       = resolveIds(game.getGenres(), genreMap);
            String producer    = resolveIds(game.getPublishers(), publisherMap);

            GamesDbGameResult result = new GamesDbGameResult(
                    game.getId(),
                    game.getGameTitle(),
                    platformName,
                    game.getReleaseDate(),
                    coverUrl,
                    game.getOverview(),
                    game.getRating(),
                    genre,
                    producer
            );
            results.add(result);
        }
        return results;
    }

    private String resolveIds(List<Integer> ids, Map<String, String> nameMap) {
        if (ids == null || ids.isEmpty()) return "";
        return ids.stream()
                .map(id -> nameMap.getOrDefault(String.valueOf(id), ""))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));
    }

    private String resolveCover(String thumbBase,
                                Map<String, List<GamesDbSearchResponse.BoxartItem>> boxartData,
                                Integer gameId) {
        if (gameId == null) return "";
        List<GamesDbSearchResponse.BoxartItem> items = boxartData.get(String.valueOf(gameId));
        if (items == null || items.isEmpty()) return "";

        return items.stream()
                .filter(b -> "boxart".equals(b.getType()) && "front".equals(b.getSide()))
                .findFirst()
                .or(() -> items.stream().filter(b -> "boxart".equals(b.getType())).findFirst())
                .or(() -> items.stream().findFirst())
                .map(b -> thumbBase + b.getFilename())
                .orElse("");
    }

    private String resolvePlatform(Map<String, GamesDbSearchResponse.PlatformItem> platformData,
                                   Integer platformId) {
        if (platformId == null) return "";
        GamesDbSearchResponse.PlatformItem p = platformData.get(String.valueOf(platformId));
        return p != null ? p.getName() : String.valueOf(platformId);
    }
}
