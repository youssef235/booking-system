package com.booking.service.presentation;

import com.booking.service.application.CreateServiceUseCase;
import com.booking.service.application.ListMyServicesUseCase;
import com.booking.service.application.ListProviderServicesUseCase;
import com.booking.service.application.ServiceResult;
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
public class ServiceController {

    private final CreateServiceUseCase createServiceUseCase;
    private final ListMyServicesUseCase listMyServicesUseCase;
    private final ListProviderServicesUseCase listProviderServicesUseCase;

    public ServiceController(CreateServiceUseCase createServiceUseCase,
                             ListMyServicesUseCase listMyServicesUseCase,
                             ListProviderServicesUseCase listProviderServicesUseCase) {
        this.createServiceUseCase = createServiceUseCase;
        this.listMyServicesUseCase = listMyServicesUseCase;
        this.listProviderServicesUseCase = listProviderServicesUseCase;
    }

    @PostMapping("/services")
    public ResponseEntity<ApiResponse<ServiceResult>> create(@AuthenticationPrincipal Jwt jwt,
                                                             @Valid @RequestBody CreateServiceRequest request) {
        ServiceResult result = createServiceUseCase.execute(request.toCommand(CurrentUser.id(jwt)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping("/services/mine")
    public ResponseEntity<ApiResponse<List<ServiceResult>>> mine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.ok(listMyServicesUseCase.execute(CurrentUser.id(jwt))));
    }

    @GetMapping("/providers/{providerId}/services")
    public ResponseEntity<ApiResponse<List<ServiceResult>>> byProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(ApiResponse.ok(listProviderServicesUseCase.execute(providerId)));
    }
}