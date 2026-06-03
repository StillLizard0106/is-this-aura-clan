package com.is_this_aura_clan.CanteenQ.catalog;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.is_this_aura_clan.CanteenQ.account.UserAccount;
import com.is_this_aura_clan.CanteenQ.account.UserAccountRepository;
import com.is_this_aura_clan.CanteenQ.account.UserAuthorizationService;
import com.is_this_aura_clan.CanteenQ.account.UserRole;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;

@Service
public class AdminStaffStallAssignmentService {

    private final StaffStallRepository staffStallRepository;
    private final UserAccountRepository userAccountRepository;
    private final StallRepository stallRepository;
    private final UserAuthorizationService userAuthorizationService;

    public AdminStaffStallAssignmentService(
        StaffStallRepository staffStallRepository,
        UserAccountRepository userAccountRepository,
        StallRepository stallRepository,
        UserAuthorizationService userAuthorizationService
    ) {
        this.staffStallRepository = staffStallRepository;
        this.userAccountRepository = userAccountRepository;
        this.stallRepository = stallRepository;
        this.userAuthorizationService = userAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<AdminStaffAssignmentResponse> listAssignments(FirebaseAuthenticationPrincipal principal, UUID stallId) {
        requireAdmin(principal);
        Stall stall = stallRepository.findById(stallId)
            .orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));

        return staffStallRepository.findByStall(stall).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AdminStaffAssignmentResponse assignStaff(FirebaseAuthenticationPrincipal principal, UUID stallId, AdminAssignStaffRequest request) {
        requireAdmin(principal);
        Stall stall = stallRepository.findById(stallId)
            .orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));

        UserAccount staffUser = userAccountRepository.findByEmail(request.staffEmail())
            .orElseGet(() -> createStaffAccount(request.staffEmail()));

        if (staffUser.getRole() != UserRole.STAFF && staffUser.getRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Only staff or admin accounts can be assigned to stalls");
        }

        // Enforce one-to-one: a staff account may only be assigned to a single stall.
        // If the staff already has any assignment (to this or another stall), prevent creating a second.
        var existing = staffStallRepository.findByStaff(staffUser);
        if (!existing.isEmpty()) {
            // If already assigned to the same stall, return conflict-like behaviour
            boolean alreadyHere = existing.stream().anyMatch(a -> a.getStall().getId().equals(stall.getId()));
            if (alreadyHere) {
                throw new IllegalArgumentException("Staff is already assigned to this stall");
            }
            throw new IllegalArgumentException("This staff is already assigned to another stall");
        }

        StaffStall assignment = new StaffStall(staffUser, stall);
        StaffStall saved = staffStallRepository.save(assignment);
        return toResponse(saved);
    }

    @Transactional
    public void removeAssignment(FirebaseAuthenticationPrincipal principal, UUID stallId, UUID staffId) {
        requireAdmin(principal);
        Stall stall = stallRepository.findById(stallId)
            .orElseThrow(() -> new StallNotFoundException("No stall found for id " + stallId));

        UserAccount staffUser = userAccountRepository.findById(staffId)
            .orElseThrow(() -> new IllegalArgumentException("No staff user found for id " + staffId));

        StaffStall assignment = staffStallRepository.findByStaffAndStall(staffUser, stall)
            .orElseThrow(() -> new IllegalArgumentException("This staff is not assigned to the stall"));

        staffStallRepository.delete(assignment);
    }

    private void requireAdmin(FirebaseAuthenticationPrincipal principal) {
        userAuthorizationService.requireRole(principal, UserRole.ADMIN);
    }

    private UserAccount createStaffAccount(String staffEmail) {
        String normalizedEmail = staffEmail == null ? "" : staffEmail.trim().toLowerCase();
        String displayName = deriveDisplayName(normalizedEmail);
        UserRole role = normalizedEmail.startsWith("admin@") ? UserRole.ADMIN : UserRole.STAFF;
        UserAccount newUser = new UserAccount(displayName, null, normalizedEmail, UUID.randomUUID().toString(), role);
        return userAccountRepository.save(newUser);
    }

    private String deriveDisplayName(String email) {
        if (email == null || email.isBlank()) {
            return "Staff Member";
        }
        String localPart = email.split("@", 2)[0].replace('.', ' ').replace('_', ' ').trim();
        if (localPart.isBlank()) {
            return "Staff Member";
        }
        String[] parts = localPart.split("\\s+");
        StringBuilder name = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) continue;
            if (name.length() > 0) {
                name.append(' ');
            }
            name.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                name.append(part.substring(1).toLowerCase());
            }
        }
        return name.length() > 0 ? name.toString() : "Staff Member";
    }

    private AdminStaffAssignmentResponse toResponse(StaffStall assignment) {
        UserAccount staffUser = assignment.getStaff();
        return new AdminStaffAssignmentResponse(
            assignment.getId(),
            staffUser.getId(),
            staffUser.getEmail(),
            staffUser.getName(),
            assignment.getAssignedAt()
        );
    }
}
