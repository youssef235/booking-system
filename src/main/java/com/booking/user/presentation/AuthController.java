package com.booking.user.presentation;

import com.booking.shared.presentation.ApiResponse;
import com.booking.user.application.LoginResult;
import com.booking.user.application.LoginUserUseCase;
import com.booking.user.application.RegisterUserUseCase;
import com.booking.user.application.UserResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResult>> register(@Valid @RequestBody RegisterUserRequest request) {
        UserResult result = registerUserUseCase.execute(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResult>> login(@Valid @RequestBody LoginUserRequest request) {
        LoginResult result = loginUserUseCase.execute(request.toCommand());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}