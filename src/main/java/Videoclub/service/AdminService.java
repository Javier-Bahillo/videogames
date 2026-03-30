package Videoclub.service;

import Videoclub.dto.AdminCreateUserRequest;
import Videoclub.dto.RentalDTO;
import Videoclub.dto.UpdateVideoGameDTO;
import Videoclub.dto.UserProfileDTO;
import Videoclub.dto.VideoGameCopyDTO;
import Videoclub.dto.VideoGameDTO;

import java.util.List;
import java.util.UUID;

public interface AdminService {
    VideoGameDTO createGame(VideoGameDTO request);

    VideoGameDTO updateGame(UUID id, UpdateVideoGameDTO request);

    VideoGameDTO toggleActive(UUID id, boolean active);

    void deleteGame(UUID id);

    List<VideoGameCopyDTO> createCopies(UUID gameId, int amount);

    int removeCopies(UUID gameId, int amount);

    List<VideoGameCopyDTO> listCopies(UUID gameId);

    List<RentalDTO> listAllRentals();

    void deleteRental(UUID rentalId);

    UserProfileDTO createUser(AdminCreateUserRequest request);

    List<VideoGameDTO> listAllGames();

    List<UserProfileDTO> listUsers();

    void deleteUser(UUID userId);

    void toggleUserStatus(UUID userId, boolean active);
}
