package com.daangn.market.Listing.presentation;

import com.daangn.market.Listing.application.ListingCommandService;
import com.daangn.market.Listing.application.ListingQueryService;
import com.daangn.market.Listing.application.dto.ListingDetailResponse;
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
            @RequestParam Integer regionId,
            @RequestParam(required = false) Long lastListingId
    ) {

        long cursor = lastListingId == null ? Long.MAX_VALUE : lastListingId;
        return ResponseEntity.ok(listingQueryService.getListings(principal.memberId(), regionId, cursor));
    }

    @PostMapping("/drafts")
    public ResponseEntity<?> createDraft(@AuthenticationPrincipal AuthPrincipal principal, @RequestParam Integer regionId) {
        Long listingId = listingCommandService.createDraft(principal.memberId(), regionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("listingId", listingId));
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<ListingDetailResponse> getListing(@PathVariable Long listingId) {
        return ResponseEntity.ok(listingQueryService.getListing(listingId));
    }

    @PutMapping("/{listingId}")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId,
            @Valid @RequestBody ListingUpdateRequest request
    ) {
        listingCommandService.update(principal.memberId(), listingId, request.toCommand());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/publish")
    public ResponseEntity<?> publish(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId
    ) {
        listingCommandService.publish(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/hide")
    public ResponseEntity<?> hide(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId
    ) {
        listingCommandService.hide(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/unhide")
    public ResponseEntity<?> unhide(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId
    ) {
        listingCommandService.unhide(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/reserve")
    public ResponseEntity<?> reserve(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId,
            @Valid @RequestBody ListingReserveRequest request
    ) {
        listingCommandService.reserve(principal.memberId(), listingId, request.buyerId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/reserve/cancel")
    public ResponseEntity<?> cancelReserve(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId
    ) {
        listingCommandService.cancelReserve(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/sold-out")
    public ResponseEntity<?> markSoldOut(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId,
            @Valid @RequestBody ListingSoldOutRequest request
    ) {
        listingCommandService.markSoldOut(principal.memberId(), listingId, request.buyerId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{listingId}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable Long listingId
    ) {
        listingCommandService.remove(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }
}
