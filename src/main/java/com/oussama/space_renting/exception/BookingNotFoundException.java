package com.oussama.space_renting.exception;

public class BookingNotFoundException extends RuntimeException {


    public BookingNotFoundException( String message) {
        super(message);
    }
}
