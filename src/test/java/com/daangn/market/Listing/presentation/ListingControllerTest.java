package com.daangn.market.Listing.presentation;

import com.daangn.market.Listing.application.ListingCommandService;
import com.daangn.market.Listing.application.ListingQueryService;
import com.daangn.market.Listing.application.dto.ListingDetailResponse;
import com.daangn.market.Listing.application.dto.ListingImageResponse;
import com.daangn.market.Listing.application.dto.ListingResponse;
import com.daangn.market.Listing.exception.ListingNotFoundException;
import com.daangn.market.common.auth.AuthPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingCommandService listingCommandService;

    @Mock
    private ListingQueryService listingQueryService;

    @InjectMocks
    private ListingController listingController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(listingController)
                .setControllerAdvice(new ListingExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createDraftReturnsCreated() throws Exception {
        when(listingCommandService.createDraft(1L, 1)).thenReturn(100L);

        mockMvc.perform(post("/api/listings/drafts")
                        .param("region_id", "1")
                        .with(authenticated(1L)))

                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.listingId")
                        .value(100L));

        ArgumentCaptor<Long> sellerCaptor = ArgumentCaptor.forClass(Long.class);
        verify(listingCommandService).createDraft(sellerCaptor.capture(), eq(1));
        assertThat(sellerCaptor.getValue()).isEqualTo(1L);
    }

    @Test
    void getListingsUsesRequestedCursor() throws Exception {
        Slice<ListingResponse> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        when(listingQueryService.getListings(1L, 11000, 88L)).thenReturn(slice);

        mockMvc.perform(get("/api/listings")
                        .param("region_id", "11000")
                        .param("last_listing_id", "88")
                        .with(authenticated(1L)))
                .andExpect(status().isOk());

        verify(listingQueryService).getListings(1L, 11000, 88L);
    }

    @Test
    void getListingsUsesDefaultCursorWhenMissingLastListingId() throws Exception {
        Slice<ListingResponse> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        when(listingQueryService.getListings(1L, 11000, Long.MAX_VALUE)).thenReturn(slice);

        mockMvc.perform(get("/api/listings")
                        .param("region_id", "11000")
                        .with(authenticated(1L)))
                .andExpect(status().isOk());

        verify(listingQueryService).getListings(1L, 11000, Long.MAX_VALUE);
    }

    @Test
    void getListingsAlsoAcceptsLegacyCamelCaseParams() throws Exception {
        Slice<ListingResponse> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        when(listingQueryService.getListings(1L, 11000, 88L)).thenReturn(slice);

        mockMvc.perform(get("/api/listings")
                        .param("regionId", "11000")
                        .param("lastListingId", "88")
                        .with(authenticated(1L)))
                .andExpect(status().isOk());

        verify(listingQueryService).getListings(1L, 11000, 88L);
    }

    @Test
    void getListingsReturnsBadRequestWhenRegionIsMissing() throws Exception {
        mockMvc.perform(get("/api/listings").with(authenticated(1L)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LISTING_BAD_REQUEST"));
    }

    @Test
    void getListingReturnsDetail() throws Exception {
        ListingDetailResponse response = new ListingDetailResponse(
                10L,
                1L,
                null,
                null,
                "Macbook",
                "almost new",
                1L,
                1200000L,
                false,
                false,
                "PUBLISHED",
                11000,
                new BigDecimal("37.5665"),
                new BigDecimal("126.9780"),
                0L,
                List.of(new ListingImageResponse(1L, "https://img/1.png", 0)),
                Instant.now(),
                Instant.now()
        );
        when(listingQueryService.getListing(10L)).thenReturn(response);

        mockMvc.perform(get("/api/listings/{listingId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.listingId").value(10L))
                .andExpect(jsonPath("$.title").value("Macbook"))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void publishReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/listings/{listingId}/publish", 10L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(listingCommandService).publish(1L, 10L);
    }

    @Test
    void hideReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/listings/{listingId}/hide", 10L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(listingCommandService).hide(1L, 10L);
    }

    @Test
    void unhideReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/listings/{listingId}/unhide", 10L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(listingCommandService).unhide(1L, 10L);
    }

    @Test
    void reserveReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/listings/{listingId}/reserve", 10L)
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "buyerId": 200
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(listingCommandService).reserve(1L, 10L, 200L);
    }

    @Test
    void cancelReserveReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/listings/{listingId}/reserve/cancel", 10L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(listingCommandService).cancelReserve(1L, 10L);
    }

    @Test
    void markSoldOutReturnsNoContent() throws Exception {
        mockMvc.perform(post("/api/listings/{listingId}/sold-out", 10L)
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "buyerId": 200
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(listingCommandService).markSoldOut(1L, 10L, 200L);
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/listings/{listingId}", 10L).with(authenticated(1L)))
                .andExpect(status().isNoContent());

        verify(listingCommandService).remove(1L, 10L);
    }

    @Test
    void getListingReturns404WhenNotFound() throws Exception {
        when(listingQueryService.getListing(999L)).thenThrow(new ListingNotFoundException());

        mockMvc.perform(get("/api/listings/{listingId}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("LISTING_NOT_FOUND"));
    }

    @Test
    void updateReturnsNoContent() throws Exception {
        mockMvc.perform(put("/api/listings/{listingId}", 10L)
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "updated title",
                                  "description": "updated desc",
                                  "categoryId": 1,
                                  "priceAmount": 1000,
                                  "isFree": false,
                                  "hopeLocation": {
                                    "regionId": 11000,
                                    "lat": 37.5665,
                                    "lng": 126.9780
                                  },
                                  "imageUrls": ["https://img/1.png"]
                                }
                                """))
                .andExpect(status().isNoContent());

        verify(listingCommandService).update(any(Long.class), any(Long.class), any());
    }

    @Test
    void updateFailsWhenRequiredFieldMissing() throws Exception {
        mockMvc.perform(put("/api/listings/{listingId}", 10L)
                        .with(authenticated(1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "updated title"
                                }
                                """))
                .andExpect(status().isBadRequest());
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
