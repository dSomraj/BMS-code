package com.scaler.bookmyshow.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookMovieResponseDto {
    private int amount;
    private Long bookingId;

    private ResponseStatus responseStatus;
}
