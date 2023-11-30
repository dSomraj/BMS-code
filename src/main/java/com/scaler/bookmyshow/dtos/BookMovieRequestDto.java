package com.scaler.bookmyshow.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookMovieRequestDto {
    private List<Long> showSeatIds;
    private Long userId;
    private Long showId;
}
