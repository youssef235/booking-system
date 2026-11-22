package com.booking.user.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.booking.user.application.LoginUserUseCase;
import com.booking.user.application.RegisterUserUseCase;
import com.booking.user.application.UserResult;
import com.booking.user.domain.EmailAlreadyExistsException;
import com.booking.user.domain.InvalidUserException;
import com.booking.user.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    private static final String URL = "/api/v1/auth/register";
    private static final String VALID_BODY =
            "{\"name\":\"Youssef\",\"email\":\"youssef@example.com\",\"password\":\"Secret123\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;

    @MockitoBean
    private LoginUserUseCase loginUserUseCase;

    @Test
    void register_withValidBody_returns201WithoutPassword() throws Exception {
        when(registerUserUseCase.execute(any()))
                .thenReturn(new UserResult(1L, "Youssef", "youssef@example.com", UserRole.USER, true));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("youssef@example.com"))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void register_withBlankNameAndBadEmail_returns400WithFieldErrors() throws Exception {
        String body = "{\"name\":\"\",\"email\":\"not-an-email\",\"password\":\"Secret123\"}";

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.error.fieldErrors.length()").value(2));
    }

    @Test
    void register_withExistingEmail_returns409() throws Exception {
        when(registerUserUseCase.execute(any()))
                .thenThrow(new EmailAlreadyExistsException("youssef@example.com"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void register_withDomainRuleViolation_returns400() throws Exception {
        when(registerUserUseCase.execute(any()))
                .thenThrow(new InvalidUserException("Password must be at least 8 characters"));

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.error.message").value("Password must be at least 8 characters"));
    }

    @Test
    void register_withMalformedJson_returns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{\"name\":"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
    }

    @Test
    void register_withEmptyBody_returns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("MALFORMED_REQUEST"));
    }
}
