package com.booking.booking.application;

import com.booking.booking.domain.BookingRepository;
import java.util.List;

public class ListMyBookingsUseCase {

    private final BookingRepository bookingRepository;
    private final BookingResultAssembler assembler;

    public ListMyBookingsUseCase(BookingRepository bookingRepository, BookingResultAssembler assembler) {
        this.bookingRepository = bookingRepository;
        this.assembler = assembler;
    }

    public List<BookingResult> execute(Long userId) {
        return assembler.assemble(bookingRepository.findByUserId(userId));
    }
}