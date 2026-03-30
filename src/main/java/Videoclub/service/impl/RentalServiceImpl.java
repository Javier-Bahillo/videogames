package Videoclub.service.impl;

import Videoclub.dto.CreateRentalRequest;
import Videoclub.dto.RentalDTO;
import Videoclub.entity.GameCopy;
import Videoclub.entity.Rental;
import Videoclub.entity.User;
import Videoclub.entity.VideoGameCopyStatus;
import Videoclub.exception.BusinessException;
import Videoclub.exception.ResourceNotFoundException;
import Videoclub.repository.RentalRepository;
import Videoclub.repository.VideoGameCopyRepository;
import Videoclub.service.EmailService;
import Videoclub.service.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private static final BigDecimal LATE_FEE_PER_DAY = BigDecimal.valueOf(1.50);

    private final RentalRepository rentalRepository;
    private final VideoGameCopyRepository gameCopyRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public RentalDTO createRental(User user, CreateRentalRequest request) {
        GameCopy copy = gameCopyRepository
                .findFirstByGame_IdAndStatus(request.getGameId(), VideoGameCopyStatus.AVAILABLE)
                .orElseThrow(() -> new BusinessException("No available copies for this game"));

        copy.setStatus(VideoGameCopyStatus.RENTED);
        copy.setUpdatedAt(LocalDate.now());
        copy.setUpdateBy(user.getUsername());
        gameCopyRepository.save(copy);

        Rental rental = Rental.builder()
                .user(user)
                .gameCopy(copy)
                .startAt(LocalDate.now())
                .dueAt(LocalDate.now().plusDays(7))
                .status("ACTIVE")
                .rentalPriceStart(copy.getGame().getRentalPrice())
                .lateFee(BigDecimal.ZERO)
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .createdBy(user.getUsername())
                .updateBy(user.getUsername())
                .build();

        Rental saved = rentalRepository.save(rental);
        log.info("Rental created for user '{}', game '{}'", user.getUsername(), copy.getGame().getTitle());

        emailService.sendRentalEmail(
                user.getEmail(), user.getName(),
                copy.getGame().getTitle(),
                saved.getStartAt(), saved.getDueAt(),
                saved.getRentalPriceStart()
        );
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDTO> getMyRentals(User user) {
        return rentalRepository.findByUser_Id(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RentalDTO returnRental(UUID rentalId, User user) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Rental", rentalId));

        if (!rental.getUser().getId().equals(user.getId())) {
            throw new BusinessException("This rental does not belong to the current user");
        }
        if ("RETURNED".equals(rental.getStatus())) {
            throw new BusinessException("This rental has already been returned");
        }

        LocalDate today = LocalDate.now();
        rental.setReturnedAt(today);
        rental.setStatus("RETURNED");
        rental.setUpdatedAt(today);
        rental.setUpdateBy(user.getUsername());

        if (today.isAfter(rental.getDueAt())) {
            long daysLate = ChronoUnit.DAYS.between(rental.getDueAt(), today);
            rental.setLateFee(BigDecimal.valueOf(daysLate).multiply(LATE_FEE_PER_DAY));
        } else {
            rental.setLateFee(BigDecimal.ZERO);
        }

        GameCopy copy = rental.getGameCopy();
        copy.setStatus(VideoGameCopyStatus.AVAILABLE);
        copy.setUpdatedAt(today);
        copy.setUpdateBy(user.getUsername());
        gameCopyRepository.save(copy);

        Rental returned = rentalRepository.save(rental);
        log.info("Rental {} returned by user '{}'", rentalId, user.getUsername());

        emailService.sendReturnEmail(
                rental.getUser().getEmail(), rental.getUser().getName(),
                rental.getGameCopy().getGame().getTitle(),
                today, returned.getLateFee()
        );
        return toDto(returned);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentalDTO> listAllRentals() {
        return rentalRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private RentalDTO toDto(Rental rental) {
        LocalDate today = LocalDate.now();
        boolean overdue = "ACTIVE".equals(rental.getStatus()) && today.isAfter(rental.getDueAt());
        BigDecimal projectedLateFee = overdue
                ? BigDecimal.valueOf(ChronoUnit.DAYS.between(rental.getDueAt(), today)).multiply(LATE_FEE_PER_DAY)
                : rental.getLateFee();

        return RentalDTO.builder()
                .id(rental.getId())
                .startAt(rental.getStartAt())
                .dueAt(rental.getDueAt())
                .returnedAt(rental.getReturnedAt())
                .status(rental.getStatus())
                .rentalPriceStart(rental.getRentalPriceStart())
                .lateFee(rental.getLateFee())
                .projectedLateFee(projectedLateFee)
                .overdue(overdue)
                .gameCopyId(rental.getGameCopy().getId())
                .gameId(rental.getGameCopy().getGame().getId())
                .gameTitle(rental.getGameCopy().getGame().getTitle())
                .userId(rental.getUser().getId())
                .build();
    }
}
