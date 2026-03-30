package Videoclub.service;

import Videoclub.dto.CreateRentalRequest;
import Videoclub.dto.RentalDTO;
import Videoclub.entity.User;

import java.util.List;
import java.util.UUID;

public interface RentalService {
    RentalDTO createRental(User user, CreateRentalRequest request);
    List<RentalDTO> getMyRentals(User user);
    RentalDTO returnRental(UUID rentalId, User user);
    List<RentalDTO> listAllRentals();
}