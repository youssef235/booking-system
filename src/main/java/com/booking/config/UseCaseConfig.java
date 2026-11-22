package com.booking.config;

import com.booking.availability.application.CreateAvailabilityUseCase;
import com.booking.availability.application.ListMyAvailabilitiesUseCase;
import com.booking.availability.application.ListProviderAvailabilitiesUseCase;
import com.booking.availability.domain.AvailabilityRepository;
import com.booking.booking.application.BookingResultAssembler;
import com.booking.booking.application.CancelBookingUseCase;
import com.booking.booking.application.CompleteBookingUseCase;
import com.booking.booking.application.ConfirmBookingUseCase;
import com.booking.booking.application.CreateBookingUseCase;
import com.booking.booking.application.ListAvailableSlotsUseCase;
import com.booking.booking.application.ListMyBookingsUseCase;
import com.booking.booking.application.ListProviderBookingsUseCase;
import com.booking.booking.application.RejectBookingUseCase;
import com.booking.booking.domain.BookingRepository;
import com.booking.provider.application.GetMyProviderUseCase;
import com.booking.provider.application.ListProvidersUseCase;
import com.booking.provider.application.ProviderResolver;
import com.booking.provider.application.RegisterProviderUseCase;
import com.booking.provider.domain.ProviderRepository;
import com.booking.service.application.CreateServiceUseCase;
import com.booking.service.application.ListMyServicesUseCase;
import com.booking.service.application.ListProviderServicesUseCase;
import com.booking.service.domain.ServiceOfferingRepository;
import com.booking.shared.application.TransactionRunner;
import com.booking.user.application.LoginUserUseCase;
import com.booking.user.application.PasswordHasher;
import com.booking.user.application.RegisterUserUseCase;
import com.booking.user.application.TokenService;
import com.booking.user.domain.UserRepository;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                                                   Clock clock) {
        return new RegisterUserUseCase(userRepository, passwordHasher, clock);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                                             TokenService tokenService) {
        return new LoginUserUseCase(userRepository, passwordHasher, tokenService);
    }

    @Bean
    public ProviderResolver providerResolver(ProviderRepository providerRepository) {
        return new ProviderResolver(providerRepository);
    }

    @Bean
    public RegisterProviderUseCase registerProviderUseCase(ProviderRepository providerRepository, Clock clock) {
        return new RegisterProviderUseCase(providerRepository, clock);
    }

    @Bean
    public GetMyProviderUseCase getMyProviderUseCase(ProviderRepository providerRepository) {
        return new GetMyProviderUseCase(providerRepository);
    }

    @Bean
    public ListProvidersUseCase listProvidersUseCase(ProviderRepository providerRepository) {
        return new ListProvidersUseCase(providerRepository);
    }

    @Bean
    public CreateServiceUseCase createServiceUseCase(ServiceOfferingRepository serviceRepository,
                                                     ProviderResolver providerResolver, Clock clock) {
        return new CreateServiceUseCase(serviceRepository, providerResolver, clock);
    }

    @Bean
    public ListMyServicesUseCase listMyServicesUseCase(ServiceOfferingRepository serviceRepository,
                                                       ProviderResolver providerResolver) {
        return new ListMyServicesUseCase(serviceRepository, providerResolver);
    }

    @Bean
    public ListProviderServicesUseCase listProviderServicesUseCase(ServiceOfferingRepository serviceRepository) {
        return new ListProviderServicesUseCase(serviceRepository);
    }

    @Bean
    public CreateAvailabilityUseCase createAvailabilityUseCase(AvailabilityRepository availabilityRepository,
                                                               ProviderResolver providerResolver, Clock clock) {
        return new CreateAvailabilityUseCase(availabilityRepository, providerResolver, clock);
    }

    @Bean
    public ListMyAvailabilitiesUseCase listMyAvailabilitiesUseCase(AvailabilityRepository availabilityRepository,
                                                                   ProviderResolver providerResolver) {
        return new ListMyAvailabilitiesUseCase(availabilityRepository, providerResolver);
    }

    @Bean
    public ListProviderAvailabilitiesUseCase listProviderAvailabilitiesUseCase(
            AvailabilityRepository availabilityRepository, Clock clock) {
        return new ListProviderAvailabilitiesUseCase(availabilityRepository, clock);
    }

    @Bean
    public CreateBookingUseCase createBookingUseCase(BookingRepository bookingRepository,
                                                     ServiceOfferingRepository serviceRepository,
                                                     ProviderRepository providerRepository,
                                                     AvailabilityRepository availabilityRepository,
                                                     TransactionRunner transactionRunner, Clock clock) {
        return new CreateBookingUseCase(bookingRepository, serviceRepository, providerRepository,
                availabilityRepository, transactionRunner, clock);
    }

    @Bean
    public ListAvailableSlotsUseCase listAvailableSlotsUseCase(ServiceOfferingRepository serviceRepository,
                                                               ProviderRepository providerRepository,
                                                               AvailabilityRepository availabilityRepository,
                                                               BookingRepository bookingRepository, Clock clock) {
        return new ListAvailableSlotsUseCase(serviceRepository, providerRepository, availabilityRepository,
                bookingRepository, clock);
    }

    @Bean
    public ConfirmBookingUseCase confirmBookingUseCase(BookingRepository bookingRepository,
                                                       ProviderResolver providerResolver, Clock clock) {
        return new ConfirmBookingUseCase(bookingRepository, providerResolver, clock);
    }

    @Bean
    public RejectBookingUseCase rejectBookingUseCase(BookingRepository bookingRepository,
                                                     ProviderResolver providerResolver, Clock clock) {
        return new RejectBookingUseCase(bookingRepository, providerResolver, clock);
    }

    @Bean
    public CancelBookingUseCase cancelBookingUseCase(BookingRepository bookingRepository,
                                                     ProviderRepository providerRepository, Clock clock) {
        return new CancelBookingUseCase(bookingRepository, providerRepository, clock);
    }

    @Bean
    public CompleteBookingUseCase completeBookingUseCase(BookingRepository bookingRepository,
                                                         ProviderResolver providerResolver, Clock clock) {
        return new CompleteBookingUseCase(bookingRepository, providerResolver, clock);
    }

    @Bean
    public BookingResultAssembler bookingResultAssembler(ServiceOfferingRepository serviceRepository,
                                                         ProviderRepository providerRepository,
                                                         UserRepository userRepository) {
        return new BookingResultAssembler(serviceRepository, providerRepository, userRepository);
    }

    @Bean
    public ListMyBookingsUseCase listMyBookingsUseCase(BookingRepository bookingRepository,
                                                       BookingResultAssembler assembler) {
        return new ListMyBookingsUseCase(bookingRepository, assembler);
    }

    @Bean
    public ListProviderBookingsUseCase listProviderBookingsUseCase(BookingRepository bookingRepository,
                                                                   ProviderResolver providerResolver,
                                                                   BookingResultAssembler assembler) {
        return new ListProviderBookingsUseCase(bookingRepository, providerResolver, assembler);
    }
}