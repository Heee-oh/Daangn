package com.daangn.market.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @PostMapping
    public ResponseEntity<?> signup() {
        return ResponseEntity.ok("");
    }

    @GetMapping("/me/regions")
    public ResponseEntity<?> myRegion() {
        return ResponseEntity.ok("");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {

        return ResponseEntity.ok("");
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateMemberInfo() {
        return ResponseEntity.ok("");
    }

    @PutMapping("/me/profile-image")
    public ResponseEntity<?> updateProfileImage() {
        return ResponseEntity.ok("");
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<?> updateNickname() {
        return ResponseEntity.ok("");
    }

    @PutMapping("me/interests/{listingId}")
    public ResponseEntity<?> addInterest() {
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/me/interests/{listingId}")
    public ResponseEntity<?> deleteInterest() {
        return ResponseEntity.ok("");
    }


    @GetMapping("/me/interests")
    public ResponseEntity<?> interests() {
        return ResponseEntity.ok("");

    }

    @DeleteMapping("/me")
    public ResponseEntity<?> withdraw() {

        return ResponseEntity.ok("");
    }



}
