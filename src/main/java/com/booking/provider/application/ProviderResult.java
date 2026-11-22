package com.booking.provider.application;

import com.booking.provider.domain.Provider;
import com.booking.provider.domain.ProviderStatus;

public record ProviderResult(Long id, Long userId, String name, String description, ProviderStatus status) {

    public static ProviderResult from(Provider provider) {
        return new ProviderResult(provider.getId(), provider.getUserId(), provider.getName(),
                provider.getDescription(), provider.getStatus());
    }
}