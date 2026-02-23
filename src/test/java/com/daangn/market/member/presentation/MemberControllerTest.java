package com.daangn.market.member.presentation;

import com.daangn.market.common.auth.AuthPrincipal;
import com.daangn.market.member.application.MemberCommandService;
import com.daangn.market.member.application.MemberQueryService;
import com.daangn.market.member.application.dto.InterestResponse;
import com.daangn.market.member.application.dto.MemberRegionResponse;
import com.daangn.market.member.application.dto.MemberResponse;
import com.daangn.market.member.application.dto.MemberSignupCommand;
import com.daangn.market.member.application.dto.MemberUpdateCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberCommandService memberCommandService;

    @Mock
    private MemberQueryService memberQueryService;

    @InjectMocks
    private MemberController memberController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void signupReturnsCreated() throws Exception {
        when(memberCommandService.signup(any(MemberSignupCommand.class))).thenReturn(100L);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "alice",
                                  "phoneNumber": "01012345678"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberId").value(100L));

        ArgumentCaptor<MemberSignupCommand> captor = ArgumentCaptor.forClass(MemberSignupCommand.class);
        verify(memberCommandService).signup(captor.capture());
        assertThat(captor.getValue().nickname()).isEqualTo("alice");
        assertThat(captor.getValue().phoneNumber()).isEqualTo("01012345678");
    }

    @Test
    void meReturnsMemberResponse() throws Exception {
        when(memberQueryService.getMe(1L)).thenReturn(new MemberResponse("nick", "img.png", 365));

        mockMvc.perform(get("/api/members/me").with(authenticated(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("nick"));

        verify(memberQueryService).getMe(1L);
    }

    @Test
    void myRegionsReturnsList() throws Exception {
        when(memberQueryService.getMyRegions(1L)).thenReturn(List.of(
                new MemberRegionResponse(10L, 11000, Instant.now(), true, "Seocho")
        ));

        mockMvc.perform(get("/api/members/me/regions").with(authenticated(1L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].regionId").value(11000));

        verify(memberQueryService).getMyRegions(1L);
    }

    @Test
    void updateMemberInfoReturnsNoContent() throws Exception {
        mockMvc.perform(patch("/api/members/me")
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MemberUpdateCommand("newNick", "img2.png"))))
                .andExpect(status().isNoContent());

        verify(memberCommandService).updateMemberInfo(1L, new MemberUpdateCommand("newNick", "img2.png"));
    }

    @Test
    void updateProfileImageReturnsNoContent() throws Exception {
        mockMvc.perform(put("/api/members/me/profile-image")
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"profileImage": "a.png"}
                                """))
                .andExpect(status().isNoContent());

        verify(memberCommandService).updateProfileImage(1L, "a.png");
    }

    @Test
    void updateNicknameReturnsNoContent() throws Exception {
        mockMvc.perform(patch("/api/members/me/nickname")
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nickname": "next"}
                                """))
                .andExpect(status().isNoContent());

        verify(memberCommandService).updateNickname(1L, "next");
    }

    @Test
    void addInterestReturnsNoContent() throws Exception {
        mockMvc.perform(put("/api/members/me/interests/{listingId}", 77L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(memberCommandService).addInterest(1L, 77L);
    }

    @Test
    void deleteInterestReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/members/me/interests/{listingId}", 77L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(memberCommandService).deleteInterest(1L, 77L);
    }

    @Test
    void interestsUsesDefaultCursorAndSize() throws Exception {
        Slice<InterestResponse> slice = new SliceImpl<>(
                List.of(new InterestResponse(10L, 900L)),
                PageRequest.of(0, 20),
                false
        );
        when(memberQueryService.getMyInterests(1L, Long.MAX_VALUE, 20)).thenReturn(slice);

        mockMvc.perform(get("/api/members/me/interests").with(authenticated(1L)))
                .andExpect(status().isOk());

        verify(memberQueryService).getMyInterests(1L, Long.MAX_VALUE, 20);
    }

    @Test
    void withdrawReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/members/me").with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(memberCommandService).withdraw(1L);
    }

    private RequestPostProcessor authenticated(Long memberId) {
        return request -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(new UsernamePasswordAuthenticationToken(new AuthPrincipal(memberId), null));
            SecurityContextHolder.setContext(context);
            return request;
        };
    }
}
