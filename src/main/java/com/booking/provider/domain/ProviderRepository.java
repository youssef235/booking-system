package com.booking.provider.domain;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository {

    Provider save(Provider provider);

    Optional<Provider> findById(Long id);

    Optional<Provider> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<Provider> findAllAcceptingBookings();
}