package com.daangn.market.appointment.presentation;

import com.daangn.market.appointment.application.AppointmentCommandService;
import com.daangn.market.appointment.application.dto.AppointmentResponse;
import com.daangn.market.appointment.application.dto.TradePromptResponse;
import com.daangn.market.appointment.presentation.dto.request.AppointmentCreateRequest;
import com.daangn.market.common.auth.AuthPrincipal;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AppointmentController {

    private final AppointmentCommandService appointmentCommandService;

    @PostMapping("/chat-rooms/{chat_room_id}/appointment")
    public ResponseEntity<AppointmentResponse> schedule(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("chat_room_id") Long chatRoomId,
            @Valid @RequestBody AppointmentCreateRequest request
    ) {
        return ResponseEntity.ok(appointmentCommandService.schedule(
                principal.memberId(),
                chatRoomId,
                request.meetAt(),
                request.reminderMinutes()
        ));
    }

    @DeleteMapping("/appointments/{appointment_id}")
    public ResponseEntity<Void> cancel(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("appointment_id") Long appointmentId
    ) {
        appointmentCommandService.cancel(principal.memberId(), appointmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/appointments/trade-prompt")
    public ResponseEntity<?> getTradePrompt(@AuthenticationPrincipal AuthPrincipal principal) {
        Optional<TradePromptResponse> prompt = appointmentCommandService.getTradePrompt(principal.memberId());
        return prompt.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/appointments/{appointment_id}/trade-prompt/dismiss")
    public ResponseEntity<Void> dismissTradePrompt(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable("appointment_id") Long appointmentId
    ) {
        appointmentCommandService.dismissTradePrompt(principal.memberId(), appointmentId);
        return ResponseEntity.noContent().build();
    }
}
