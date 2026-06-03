package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/admin/stalls/{stallId}/assignments")
public class AdminStaffStallAssignmentController {

    private final AdminStaffStallAssignmentService adminAssignmentService;

    public AdminStaffStallAssignmentController(AdminStaffStallAssignmentService adminAssignmentService) {
        this.adminAssignmentService = adminAssignmentService;
    }

    @GetMapping
    public ResponseEntity<List<AdminStaffAssignmentResponse>> listAssignments(
        @RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
        @PathVariable UUID stallId
    ) {
        return ResponseEntity.ok(adminAssignmentService.listAssignments(principal, stallId));
    }

    @PostMapping
    public ResponseEntity<AdminStaffAssignmentResponse> assignStaff(
        @RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
        @PathVariable UUID stallId,
        @Valid @RequestBody AdminAssignStaffRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(adminAssignmentService.assignStaff(principal, stallId, request));
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> removeAssignment(
        @RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
        @PathVariable UUID stallId,
        @PathVariable UUID staffId
    ) {
        adminAssignmentService.removeAssignment(principal, stallId, staffId);
        return ResponseEntity.noContent().build();
    }
}
