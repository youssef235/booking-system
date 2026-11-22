package com.booking.booking.presentation;

import com.booking.booking.application.BookingResult;
import com.booking.booking.application.CancelBookingUseCase;
import com.booking.booking.application.CompleteBookingUseCase;
import com.booking.booking.application.ConfirmBookingUseCase;
import com.booking.booking.application.CreateBookingUseCase;
import com.booking.booking.application.ListMyBookingsUseCase;
import com.booking.booking.application.ListProviderBookingsUseCase;
import com.booking.booking.application.RejectBookingUseCase;
import com.booking.shared.presentation.ApiResponse;
import com.booking.shared.presentation.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final CreateBookingUseCase createBookingUseCase;
    private final ConfirmBookingUseCase confirmBookingUseCase;
    private final RejectBookingUseCase rejectBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;
    private final CompleteBookingUseCase completeBookingUseCase;
    private final ListMyBookingsUseCase listMyBookingsUseCase;
    private final ListProviderBookingsUseCase listProviderBookingsUseCase;

    public BookingController(CreateBookingUseCase createBookingUseCase,
                             ConfirmBookingUseCase confirmBookingUseCase,
                             RejectBookingUseCase rejectBookingUseCase,
                             CancelBookingUseCase cancelBookingUseCase,
                             CompleteBookingUseCase completeBookingUseCase,
                             ListMyBookingsUseCase listMyBookingsUseCase,
                             ListProviderBookingsUseCase listProviderBookingsUseCase) {
        this.createBookingUseCase = createBookingUseCase;
        this.confirmBookingUseCase = confirmBookingUseCase;
        this.rejectBookingUseCase = rejectBookingUseCase;
        this.cancelBookingUseCase = cancelBookingUseCase;
        this.completeBookingUseCase = completeBookingUseCase;
        this.listMyBookingsUseCase = listMyBookingsUseCase;
        this.listProviderBookingsUseCase = listProviderBookingsUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResult>> create(@AuthenticationPrincipal Jwt jwt,
                                                             @Valid @RequestBody CreateBookingRequest request) {
        BookingResult result = createBookingUseCase.execute(request.toCommand(CurrentUser.id(jwt)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<List<BookingResult>>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.ok(listMyBookingsUseCase.execute(CurrentUser.id(jwt))));
    }

    @GetMapping("/provider")
    public ResponseEntity<ApiResponse<List<BookingResult>>> forProvider(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.ok(listProviderBookingsUseCase.execute(CurrentUser.id(jwt))));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<BookingResult>> confirm(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(confirmBookingUseCase.execute(CurrentUser.id(jwt), id)));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<BookingResult>> reject(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(rejectBookingUseCase.execute(CurrentUser.id(jwt), id)));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResult>> cancel(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(cancelBookingUseCase.execute(CurrentUser.id(jwt), id)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<BookingResult>> complete(@AuthenticationPrincipal Jwt jwt,
                                                               @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(completeBookingUseCase.execute(CurrentUser.id(jwt), id)));
    }
}