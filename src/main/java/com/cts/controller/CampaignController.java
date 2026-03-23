package com.cts.controller;



import com.cts.common.ApiResponse;
import com.cts.dto.CampaignGetDto;
import com.cts.dto.CreateCampaignRequest;
import com.cts.enums.ApprovalStatus;
import com.cts.security.AuthContextService;
import com.cts.service.CampaignService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final AuthContextService authContextService;

    @PreAuthorize("hasRole('MARKETING_MANAGER')")
    @PostMapping
    public ResponseEntity<ApiResponse<CampaignGetDto>> create(@Valid @RequestBody CreateCampaignRequest request,
                                                              Authentication authentication) {
        Long createdBy = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<CampaignGetDto>builder()
                .success(true)
                .message("Campaign created and sent for approval")
                .data(campaignService.create(request, createdBy))
                .build());
    }

    @PreAuthorize("hasRole('STORE_MANAGER')")
    @PatchMapping("/{campaignId}/status")
    public ResponseEntity<ApiResponse<CampaignGetDto>> updateStatus(@PathVariable Long campaignId,
                                                                    @RequestParam ApprovalStatus status,
                                                                    Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        return ResponseEntity.ok(ApiResponse.<CampaignGetDto>builder()
                .success(true)
                .message("Campaign status updated")
                .data(campaignService.approveOrReject(campaignId, status, actorUserId))
                .build());
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CampaignGetDto>>> active(@RequestParam Integer age) {
        return ResponseEntity.ok(ApiResponse.<List<CampaignGetDto>>builder()
                .success(true)
                .message("Active campaigns")
                .data(campaignService.activeByAge(age))
                .build());
    }

    @PreAuthorize("hasAnyRole('MARKETING_MANAGER','STORE_MANAGER')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CampaignGetDto>>> all() {
        return ResponseEntity.ok(ApiResponse.<List<CampaignGetDto>>builder()
                .success(true)
                .message("Campaigns fetched")
                .data(campaignService.all())
                .build());
    }

    @PreAuthorize("hasAnyRole('MARKETING_MANAGER','STORE_MANAGER')")
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long campaignId, Authentication authentication) {
        Long actorUserId = authContextService.currentInternalUser(authentication).getUserId();
        campaignService.delete(campaignId, actorUserId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Campaign deleted")
                .data(null)
                .build());
    }
}
