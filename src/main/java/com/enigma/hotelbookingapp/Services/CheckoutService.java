package com.enigma.hotelbookingapp.Services;

import com.enigma.hotelbookingapp.Entities.Booking;

public interface CheckoutService {

    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
