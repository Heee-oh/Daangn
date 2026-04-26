package com.daangn.market.member.presentation;

import com.daangn.market.common.auth.AuthPrincipal;
import com.daangn.market.member.application.MemberCommandService;
import com.daangn.market.member.application.MemberQueryService;
import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;
import com.daangn.market.member.application.dto.MemberSignupCommand;
import com.daangn.market.member.application.dto.MemberUpdateCommand;
import com.daangn.market.member.presentation.dto.request.NicknameRequest;
import com.daangn.market.member.presentation.dto.request.ProfileImageRequest;
import com.daangn.market.member.presentation.dto.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;
    private final MemberQueryService memberQueryService;

    @GetMapping("/me/regions")
    public ResponseEntity<?> myRegion(@AuthenticationPrincipal AuthPrincipal principal) {
        List<MemberRegionResponse> myRegions = memberQueryService.getMyRegions(principal.memberId());
        return ResponseEntity.ok(myRegions);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal AuthPrincipal principal) {
        MemberResponse me = memberQueryService.getMe(principal.memberId());
        return ResponseEntity.ok(me);
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMemberInfo(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody MemberUpdateCommand command) {
        memberCommandService.updateMemberInfo(principal.memberId(), command);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/profile-image")
    public ResponseEntity<?> updateProfileImage(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody ProfileImageRequest request) {
        memberCommandService.updateProfileImage(principal.memberId(), request.profileImage());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<?> updateNickname(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestBody NicknameRequest request) {
        memberCommandService.updateNickname(principal.memberId(), request.nickname());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/interests/{listing_id}")
    public ResponseEntity<?> addInterest(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId) {
        memberCommandService.addInterest(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/interests/{listing_id}")
    public ResponseEntity<?> deleteInterest(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId) {
        memberCommandService.deleteInterest(principal.memberId(), listingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/interests")
    public ResponseEntity<?> interests(
            @AuthenticationPrincipal AuthPrincipal principal,
            @RequestParam(required = false) Long lastInterestId,
            @RequestParam(defaultValue = "20") int size) {

        long cursor = lastInterestId == null ? Long.MAX_VALUE : lastInterestId;
        return ResponseEntity.ok(memberQueryService.getMyInterests(principal.memberId(), cursor, size));
    }

    @GetMapping("/me/interests/{listing_id}")
    public ResponseEntity<?> interestStatus(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("listing_id") Long listingId) {
        boolean interested = memberQueryService.isInterested(principal.memberId(), listingId);
        return ResponseEntity.ok(Map.of("interested", interested));
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> withdraw(@AuthenticationPrincipal AuthPrincipal principal) {
        memberCommandService.withdraw(principal.memberId());
        return ResponseEntity.noContent().build();
    }






}
