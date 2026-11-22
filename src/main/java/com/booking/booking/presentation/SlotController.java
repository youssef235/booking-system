package com.booking.booking.presentation;

import com.booking.booking.application.ListAvailableSlotsUseCase;
import com.booking.booking.application.SlotResult;
import com.booking.shared.presentation.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SlotController {

    private final ListAvailableSlotsUseCase listAvailableSlotsUseCase;

    public SlotController(ListAvailableSlotsUseCase listAvailableSlotsUseCase) {
        this.listAvailableSlotsUseCase = listAvailableSlotsUseCase;
    }

    @GetMapping("/services/{serviceId}/slots")
    public ResponseEntity<ApiResponse<List<SlotResult>>> slots(@PathVariable Long serviceId) {
        return ResponseEntity.ok(ApiResponse.ok(listAvailableSlotsUseCase.execute(serviceId)));
    }
}