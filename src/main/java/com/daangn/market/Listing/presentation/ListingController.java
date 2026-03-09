package com.daangn.market.Listing.presentation;

import com.daangn.market.Listing.application.ListingCommandService;
import com.daangn.market.Listing.application.ListingQueryService;
import com.daangn.market.Listing.application.dto.ListingDetailResponse;
import com.daangn.market.Listing.exception.ListingBadRequestException;
import com.daangn.market.Listing.presentation.dto.request.ListingReserveRequest;
import com.daangn.market.Listing.presentation.dto.request.ListingSoldOutRequest;
import com.daangn.market.Listing.presentation.dto.request.ListingUpdateRequest;
import com.daangn.market.common.auth.AuthPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingCommandService listingCommandService;
    private final ListingQueryService listingQueryService;


    @GetMapping()
    public ResponseEntity<?> getListings(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(name = "region_id", required = false) Integer regionId,
            @RequestParam(name = "last_listing_id", required = false) Long lastListingId
    ) {
        if (regionId == null) {
            throw new ListingBadRequestException("region_id is required");
        }
        long cursor = lastListingId == null ? Long.MAX_VALUE : lastListingId;
        return ResponseEntity.ok(listingQueryService.getListings(principal.memberId(), regionId, cursor));
    }

    @PostMapping("/drafts")
    public ResponseEntity<?> createDraft(@AuthenticationPrincipal AuthPrincipal principal, @RequestParam("region_id") Integer regionId) {
        Long listingId = listingCommandService.createDraft(principal.memberId(), regionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("listingId", listingId));
    }

    @GetMapping("/{listing_id}")
    public ResponseEntity<ListingDetailResponse> getListing(@PathVariable("listing_id") Long listingId) {
        return ResponseEntity.ok(listingQueryService.getListing(listingId));
    }

    @PutMapping("/{listing_id}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId,
            @Valid @RequestBody ListingUpdateRequest request
    ) {
        listingCommandService.update(principal.memberId(), listingId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listing_id}/publish")
    public ResponseEntity<?> publish(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId
    ) {
        listingCommandService.publish(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listing_id}/hide")
    public ResponseEntity<?> hide(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId
    ) {
        listingCommandService.hide(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listing_id}/unhide")
    public ResponseEntity<?> unhide(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId
    ) {
        listingCommandService.unhide(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listing_id}/reserve")
    public ResponseEntity<?> reserve(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId,
            @Valid @RequestBody ListingReserveRequest request
    ) {
        listingCommandService.reserve(principal.memberId(), listingId, request.buyerId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listing_id}/reserve/cancel")
    public ResponseEntity<?> cancelReserve(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId
    ) {
        listingCommandService.cancelReserve(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listing_id}/sold-out")
    public ResponseEntity<?> markSoldOut(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId,
            @Valid @RequestBody ListingSoldOutRequest request
    ) {
        listingCommandService.markSoldOut(principal.memberId(), listingId, request.buyerId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{listing_id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId
    ) {
        listingCommandService.remove(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }
}
