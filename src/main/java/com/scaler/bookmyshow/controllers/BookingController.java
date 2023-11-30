package com.scaler.bookmyshow.controllers;

import com.scaler.bookmyshow.models.Booking;
import com.scaler.bookmyshow.dtos.BookMovieRequestDto;
import com.scaler.bookmyshow.dtos.BookMovieResponseDto;
import com.scaler.bookmyshow.dtos.ResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.scaler.bookmyshow.services.BookingService;

@Controller
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public BookMovieResponseDto bookMovie(BookMovieRequestDto bookMovieRequestDto) {
        BookMovieResponseDto bookMovieResponseDto = new BookMovieResponseDto();

        Booking booking;

        try {
            booking = bookingService.bookMovie(
                    bookMovieRequestDto.getUserId(),
                    bookMovieRequestDto.getShowSeatIds(),
                    bookMovieRequestDto.getShowId()
            );

            bookMovieResponseDto.setResponseStatus(ResponseStatus.SUCCESS);
            bookMovieResponseDto.setBookingId(booking.getId());
            bookMovieResponseDto.setAmount(booking.getAmount());
        } catch(Exception ex) {
            bookMovieResponseDto.setResponseStatus(ResponseStatus.FAILURE);
        }

        return bookMovieResponseDto;
    }
}
