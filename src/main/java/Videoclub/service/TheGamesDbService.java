package Videoclub.service;

import Videoclub.dto.gamesdb.GamesDbGameResult;
import Videoclub.dto.gamesdb.GamesDbPlatformResult;

import java.util.List;

public interface TheGamesDbService {
    List<GamesDbGameResult> searchGames(String name);
    GamesDbGameResult getGameById(int gameId);
    List<GamesDbPlatformResult> getPlatforms();
}
