package com.is_this_aura_clan.CanteenQ.secure;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/staff/reporting")
public class StaffReportController {

	private final StaffReportService staffReportService;

	public StaffReportController(StaffReportService staffReportService) {
		this.staffReportService = staffReportService;
	}

	@GetMapping("/summary")
	public ResponseEntity<StaffReportResponse> summary(
		@RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
		@RequestParam(required = false) LocalDate startDate,
		@RequestParam(required = false) LocalDate endDate
	) {
		return ResponseEntity.ok(staffReportService.getSummary(principal, startDate, endDate));
	}
}
