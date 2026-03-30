package Videoclub.service.impl;

import Videoclub.dto.AdminCreateUserRequest;
import Videoclub.dto.RentalDTO;
import Videoclub.dto.UpdateVideoGameDTO;
import Videoclub.dto.UserProfileDTO;
import Videoclub.dto.VideoGameCopyDTO;
import Videoclub.dto.VideoGameDTO;
import Videoclub.entity.GameCopy;
import Videoclub.entity.Rental;
import Videoclub.entity.User;
import Videoclub.entity.VideoGameCopyStatus;
import Videoclub.exception.BusinessException;
import Videoclub.exception.ResourceNotFoundException;
import Videoclub.mapper.VideoGameMapper;
import Videoclub.entity.VideoGame;
import Videoclub.repository.RentalRepository;
import Videoclub.repository.UserRepository;
import Videoclub.repository.VideoGameCopyRepository;
import Videoclub.repository.VideoGameRepository;
import Videoclub.service.AdminService;
import Videoclub.service.EmailService;
import Videoclub.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final VideoGameRepository videoGameRepository;
    private final VideoGameMapper videoGameMapper;
    private final VideoGameCopyRepository videoGameCopyRepository;
    private final RentalService rentalService;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private String currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";
    }

    @Override
    @Transactional
    public VideoGameDTO createGame(VideoGameDTO videoGameDTO) {
        if (videoGameRepository.existsByTitle(videoGameDTO.getTitle())) {
            throw new BusinessException("A game with title '" + videoGameDTO.getTitle() + "' already exists");
        }
        String actor = currentUser();
        VideoGame videoGame = videoGameMapper.toEntity(videoGameDTO);
        videoGame.setCreatedAt(LocalDate.now());
        videoGame.setUpdatedAt(LocalDate.now());
        videoGame.setCreatedBy(actor);
        videoGame.setUpdatedBy(actor);

        return videoGameMapper.toDto(videoGameRepository.save(videoGame));
    }

    @Override
    @Transactional
    public VideoGameDTO updateGame(UUID id, UpdateVideoGameDTO videoGameDTO) {
        VideoGame game = videoGameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game", id));

        game.setTitle(videoGameDTO.getTitle());
        game.setDescription(videoGameDTO.getDescription());
        game.setPlatform(videoGameDTO.getPlatform());
        game.setGenre(videoGameDTO.getGenre());
        game.setProducer(videoGameDTO.getProducer());
        game.setPegi(videoGameDTO.getPegi());
        game.setReleaseDate(videoGameDTO.getReleaseDate());
        game.setUpdatedAt(LocalDate.now());
        game.setUpdatedBy(currentUser());

        return videoGameMapper.toDto(videoGameRepository.save(game));
    }

    @Override
    @Transactional
    public VideoGameDTO toggleActive(UUID id, boolean active) {
        VideoGame game = videoGameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game", id));

        game.setActive(active);
        game.setUpdatedAt(LocalDate.now());
        game.setUpdatedBy(currentUser());

        return videoGameMapper.toDto(videoGameRepository.save(game));
    }

    @Override
    @Transactional
    public void deleteGame(UUID id) {
        VideoGame game = videoGameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game", id));

        game.setActive(false);
        game.setUpdatedAt(LocalDate.now());
        game.setUpdatedBy(currentUser());
        videoGameRepository.save(game);
        log.info("Game '{}' deactivated by {}", game.getTitle(), currentUser());
    }

    @Override
    @Transactional
    public List<VideoGameCopyDTO> createCopies(UUID gameId, int amount) {
        VideoGame game = videoGameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", gameId));

        String actor = currentUser();
        List<GameCopy> copies = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            GameCopy copy = new GameCopy();
            copy.setGame(game);
            copy.setStatus(VideoGameCopyStatus.AVAILABLE);
            copy.setCreatedAt(LocalDate.now());
            copy.setUpdatedAt(LocalDate.now());
            copy.setCreatedBy(actor);
            copy.setUpdateBy(actor);
            copies.add(videoGameCopyRepository.save(copy));
        }

        return copies.stream()
                .map(videoGameMapper::toDtoCopy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int removeCopies(UUID gameId, int amount) {
        if (!videoGameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game", gameId);
        }
        int removed = 0;
        for (int i = 0; i < amount; i++) {
            var copy = videoGameCopyRepository.findFirstByGame_IdAndStatus(gameId, VideoGameCopyStatus.AVAILABLE);
            if (copy.isEmpty()) break;
            videoGameCopyRepository.delete(copy.get());
            removed++;
        }
        return removed;
    }

    @Override
    public List<RentalDTO> listAllRentals() {
        return rentalService.listAllRentals();
    }

    @Override
    public List<VideoGameCopyDTO> listCopies(UUID gameId) {
        if (!videoGameRepository.existsById(gameId)) {
            throw new ResourceNotFoundException("Game", gameId);
        }
        return videoGameCopyRepository.findByGame_Id(gameId).stream()
                .map(videoGameMapper::toDtoCopy)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRental(UUID rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", rentalId));

        if ("ACTIVE".equals(rental.getStatus())) {
            GameCopy copy = rental.getGameCopy();
            copy.setStatus(VideoGameCopyStatus.AVAILABLE);
            copy.setUpdatedAt(LocalDate.now());
            videoGameCopyRepository.save(copy);
        }
        rentalRepository.deleteById(rentalId);
        log.info("Rental {} deleted by {}", rentalId, currentUser());
    }

    @Override
    @Transactional
    public UserProfileDTO createUser(AdminCreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email '" + request.getEmail() + "' is already registered");
        }

        String role = "ADMIN".equals(request.getRole()) ? "ADMIN" : "USER";
        String actor = currentUser();

        User user = User.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .status("ACTIVE")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .createdBy(actor)
                .updateBy(actor)
                .build();

        user = userRepository.save(user);
        emailService.sendWelcomeEmail(user.getEmail(), user.getName(), user.getSurname(),
                user.getUsername(), user.getRole(), user.getCreatedAt());

        log.info("User '{}' created by {}", user.getUsername(), actor);

        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public List<UserProfileDTO> listUsers() {
        return userRepository.findByRole("USER").stream()
                .map(u -> UserProfileDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .name(u.getName())
                        .surname(u.getSurname())
                        .email(u.getEmail())
                        .role(u.getRole())
                        .status(u.getStatus())
                        .createdAt(u.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!"USER".equals(user.getRole())) {
            throw new BusinessException("Admin accounts cannot be deleted");
        }
        user.setStatus("INACTIVE");
        user.setUpdatedAt(LocalDate.now());
        user.setUpdateBy(currentUser());
        userRepository.save(user);
        log.info("User '{}' deactivated by {}", user.getUsername(), currentUser());
    }

    @Override
    @Transactional
    public void toggleUserStatus(UUID userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setStatus(active ? "ACTIVE" : "INACTIVE");
        user.setUpdatedAt(LocalDate.now());
        user.setUpdateBy(currentUser());
        userRepository.save(user);

        if (active) {
            emailService.sendReactivationEmail(user.getEmail(), user.getName());
        }
        log.info("User '{}' status set to {} by {}", user.getUsername(), user.getStatus(), currentUser());
    }

    @Override
    public List<VideoGameDTO> listAllGames() {
        return videoGameRepository.findAll().stream()
                .map(game -> {
                    VideoGameDTO dto = videoGameMapper.toDto(game);
                    dto.setAvailableCopies((int) videoGameCopyRepository.countByGame_IdAndStatus(
                            game.getId(), VideoGameCopyStatus.AVAILABLE));
                    dto.setTotalCopies((int) videoGameCopyRepository.countByGame_Id(game.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
