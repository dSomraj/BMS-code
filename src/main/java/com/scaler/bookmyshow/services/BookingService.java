package com.scaler.bookmyshow.services;

import com.scaler.bookmyshow.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.scaler.bookmyshow.repositories.BookingRepository;
import com.scaler.bookmyshow.repositories.ShowRepository;
import com.scaler.bookmyshow.repositories.ShowSeatRepository;
import com.scaler.bookmyshow.repositories.UserRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private UserRepository userRepository;
    private ShowRepository showRepository;
    private ShowSeatRepository showSeatRepository;

    private PricingService pricingService;
    private BookingRepository bookingRepository;

    @Autowired
    public BookingService(UserRepository userRepository,
                          ShowRepository showRepository,
                          ShowSeatRepository showSeatRepository,
                          PricingService pricingService,
                          BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.showRepository = showRepository;
        this.showSeatRepository = showSeatRepository;
        this.pricingService = pricingService;
        this.bookingRepository = bookingRepository;
    }


    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Booking bookMovie(Long userId, List<Long> seatsIds, Long showId) {
        /*----Start lock here for TODAY---------
        * 1. Get the user from the user id
        * 2. Get the show from the show id
        * ----------Take a lock----------------
        * 3. Get the show seats from the seat ids
        * 4. Check if the seats are available
        * 5. If yes, make the status as blocked (or booking in progress)
        * ----------Release the lock------------
        * 6. Save updated show seats in DB and end the lock
        * ------ END LOCK HERE FOR TODAY ---------
        * */

        Optional<User> userOptional = userRepository.findById(userId);
        if(userOptional.isEmpty()) {
            throw new RuntimeException();
        }

        User bookedBy = userOptional.get();

        Optional<Show> showOptional = showRepository.findById(showId);
        if(showOptional.isEmpty()) {
            throw new RuntimeException();
        }

        Show bookedShow = showOptional.get();

        List<ShowSeat> showSeats = showSeatRepository.findAllById(seatsIds);


        for(ShowSeat showSeat: showSeats) {
            if(!(showSeat.getShowSeatStatus().equals(ShowSeatStatus.AVAILABLE) ||
                    (showSeat.getShowSeatStatus().equals(ShowSeatStatus.BLOCKED) &&
                            Duration.between(showSeat.getBlockedAt().toInstant(),
                                    new Date().toInstant()).toMinutes() > 15))) {
                throw new RuntimeException();
            }
        }

        List<ShowSeat> savedShowSeats = new ArrayList<>();
        for(ShowSeat showSeat: showSeats) {
            showSeat.setShowSeatStatus(ShowSeatStatus.BLOCKED);
            showSeat.setBlockedAt(new Date());
            savedShowSeats.add(showSeatRepository.save(showSeat));
        }

        Booking booking = new Booking();
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setShowSeats(savedShowSeats);
        booking.setUser(bookedBy);
        booking.setBookedAt(new Date());
        booking.setShow(bookedShow);
        booking.setAmount(pricingService.calculatePrice(savedShowSeats, bookedShow));

        Booking savedBooking = bookingRepository.save(booking);

        return savedBooking;
    }
}
