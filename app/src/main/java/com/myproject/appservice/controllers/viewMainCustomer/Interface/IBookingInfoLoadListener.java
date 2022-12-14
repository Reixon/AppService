package com.myproject.appservice.controllers.viewMainCustomer.Interface;

import com.myproject.appservice.models.Booking;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(Booking booking);
    void onBookingInfoLoadFailed(String message);
}
