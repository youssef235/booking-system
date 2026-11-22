package com.booking.provider.presentation;

import com.booking.provider.application.GetMyProviderUseCase;
import com.booking.provider.application.ListProvidersUseCase;
import com.booking.provider.application.ProviderResult;
import com.booking.provider.application.RegisterProviderUseCase;
import com.booking.shared.presentation.ApiResponse;
import com.booking.shared.presentation.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final RegisterProviderUseCase registerProviderUseCase;
    private final GetMyProviderUseCase getMyProviderUseCase;
    private final ListProvidersUseCase listProvidersUseCase;

    public ProviderController(RegisterProviderUseCase registerProviderUseCase,
                              GetMyProviderUseCase getMyProviderUseCase,
                              ListProvidersUseCase listProvidersUseCase) {
        this.registerProviderUseCase = registerProviderUseCase;
        this.getMyProviderUseCase = getMyProviderUseCase;
        this.listProvidersUseCase = listProvidersUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderResult>> register(@AuthenticationPrincipal Jwt jwt,
                                                                @Valid @RequestBody RegisterProviderRequest request) {
        ProviderResult result = registerProviderUseCase.execute(request.toCommand(CurrentUser.id(jwt)));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderResult>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(listProvidersUseCase.execute()));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProviderResult>> me(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(ApiResponse.ok(getMyProviderUseCase.execute(CurrentUser.id(jwt))));
    }
}