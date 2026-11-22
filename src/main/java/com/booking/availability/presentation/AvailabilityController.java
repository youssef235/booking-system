package com.booking.availability.presentation;

import com.booking.availability.application.AvailabilityResult;
import com.booking.availability.application.CreateAvailabilityUseCase;
import com.booking.availability.application.ListMyAvailabilitiesUseCase;
import com.booking.availability.application.ListProviderAvailabilitiesUseCase;
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
@RequestMapping("/api/v1")
public class AvailabilityController {

    private final CreateAvailabilityUseCase createAvailabilityUseCase;
    private final ListMyAvailabilitiesUseCase listMyAvailabilitiesUseCase;
    private final ListProviderAvailabilitiesUseCase listProviderAvailabilitiesUseCase;

    public AvailabilityController(CreateAvailabilityUseCase createAvailabilityUseCase,
                                  ListMyAvailabilitiesUseCase listMyAvailabilitiesUseCase,
                                  ListProviderAvailabilitiesUseCase listProviderAvailabilitiesUseCase) {
        this.createAvailabilityUseCase = createAvailabilityUseCase;
        this.listMyAvailabilitiesUseCase = listMyAvailabilitiesUseCase;
        this.listProviderAvailabilitiesUseCase = listProviderAvailabilitiesUseCase;
    }

    @PostMapping("/availabilities")
    public ResponseEntity<ApiResponse<AvailabilityResult>> create(@AuthenticationPrincipal Jwt jwt,
                                                                  @Valid @RequestBody CreateAvailabilityRequest request) {
        AvailabilityResult result = createAvailabilityUseCase.execute(request.toCommand(CurrentUser.id(jwt)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping("/availabilities/mine")
    public ResponseEntity<ApiResponse<List<AvailabilityResult>>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.ok(listMyAvailabilitiesUseCase.execute(CurrentUser.id(jwt))));
    }

    @GetMapping("/providers/{providerId}/availabilities")
    public ResponseEntity<ApiResponse<List<AvailabilityResult>>> byProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(ApiResponse.ok(listProviderAvailabilitiesUseCase.execute(providerId)));
    }
}