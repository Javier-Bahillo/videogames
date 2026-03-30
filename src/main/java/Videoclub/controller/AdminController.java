package Videoclub.controller;

import Videoclub.dto.AdminCreateUserRequest;
import Videoclub.dto.RentalDTO;
import Videoclub.dto.UpdateVideoGameDTO;
import Videoclub.dto.UserProfileDTO;
import Videoclub.dto.VideoGameCopyDTO;
import Videoclub.dto.VideoGameDTO;
import Videoclub.service.AdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminServiceImpl;

    @PostMapping("/games")
    public ResponseEntity<VideoGameDTO> createGame(@Valid @RequestBody VideoGameDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.createGame(request));
    }

    @PutMapping("/games/{id}")
    public ResponseEntity<VideoGameDTO> updateGame(@PathVariable UUID id,
                                                    @Valid @RequestBody UpdateVideoGameDTO request) {
        return ResponseEntity.ok(adminServiceImpl.updateGame(id, request));
    }

    @PatchMapping("/games/{id}")
    public ResponseEntity<VideoGameDTO> toggleGameActive(@PathVariable UUID id,
                                                          @RequestParam boolean active) {
        return ResponseEntity.ok(adminServiceImpl.toggleActive(id, active));
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable UUID id) {
        adminServiceImpl.deleteGame(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/games/{gameId}/copies")
    public ResponseEntity<List<VideoGameCopyDTO>> createCopies(@PathVariable UUID gameId,
                                                                @Min(1) @RequestParam int amount) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.createCopies(gameId, amount));
    }

    @DeleteMapping("/games/{gameId}/copies")
    public ResponseEntity<Void> removeCopies(@PathVariable UUID gameId,
                                              @Min(1) @RequestParam int amount) {
        adminServiceImpl.removeCopies(gameId, amount);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/games/{gameId}/copies")
    public ResponseEntity<List<VideoGameCopyDTO>> listCopies(@PathVariable UUID gameId) {
        return ResponseEntity.ok(adminServiceImpl.listCopies(gameId));
    }

    @GetMapping("/rentals")
    public ResponseEntity<List<RentalDTO>> listAllRentals() {
        return ResponseEntity.ok(adminServiceImpl.listAllRentals());
    }

    @DeleteMapping("/rentals/{id}")
    public ResponseEntity<Void> deleteRental(@PathVariable UUID id) {
        adminServiceImpl.deleteRental(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/games/all")
    public ResponseEntity<List<VideoGameDTO>> listAllGames() {
        return ResponseEntity.ok(adminServiceImpl.listAllGames());
    }

    @PostMapping("/users")
    public ResponseEntity<UserProfileDTO> createUser(@Valid @RequestBody AdminCreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminServiceImpl.createUser(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserProfileDTO>> listUsers() {
        return ResponseEntity.ok(adminServiceImpl.listUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        adminServiceImpl.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Void> toggleUser(@PathVariable UUID id, @RequestParam boolean active) {
        adminServiceImpl.toggleUserStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
