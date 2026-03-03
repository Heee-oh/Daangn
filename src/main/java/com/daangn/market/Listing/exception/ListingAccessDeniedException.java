package com.daangn.market.Listing.exception;

public class ListingAccessDeniedException extends RuntimeException {
    public ListingAccessDeniedException() {
        super("Only the seller can modify this listing");
    }
}

