package com.is_this_aura_clan.CanteenQ.secure;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.is_this_aura_clan.CanteenQ.auth.FirebaseAuthenticationPrincipal;
import com.is_this_aura_clan.CanteenQ.auth.FirebaseRequestAttributes;

@RestController
@RequestMapping("/api/reporting")
public class ReportingController {

    private final StaffReportService staffReportService;

    public ReportingController(StaffReportService staffReportService) {
        this.staffReportService = staffReportService;
    }

    @GetMapping("/daily")
    public ResponseEntity<DailyReportResponse> daily(
        @RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate
    ) {
        StaffReportResponse full = staffReportService.getSummary(principal, startDate, endDate);
        DailyReportResponse daily = new DailyReportResponse(
            full.totalStalls(),
            full.totalOrders(),
            full.totalRevenue(),
            full.ordersToday(),
            full.activeOrders(),
            full.pendingOrders(),
            full.preparingOrders(),
            full.readyOrders(),
            full.completedToday(),
            full.cancelledToday(),
            full.unclaimedToday()
        );
        return ResponseEntity.ok(daily);
    }

    @GetMapping("/stalls")
    public ResponseEntity<List<StaffStallReportResponse>> stalls(
        @RequestAttribute(FirebaseRequestAttributes.PRINCIPAL) FirebaseAuthenticationPrincipal principal,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate
    ) {
        return ResponseEntity.ok(staffReportService.getStallBreakdowns(principal, startDate, endDate));
    }
}
