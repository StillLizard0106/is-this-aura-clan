# CanteenQ Compliance Checklist

Legend:
- `[x]` Met
- `[~]` Partially met or still needs end-to-end verification
- `[ ]` Not yet met

## Workflow Checklist
- [x] Demo bootstrap seeds staff account, stall, menu items, and staff assignment.
- [x] Student authentication is handled through Firebase-backed account sync.
- [x] Students can browse stalls and menu items.
- [x] Students can place orders with cart items and a pickup slot.
- [x] Students can track order status and view order history.
- [x] Students can cancel orders within the allowed window.
- [x] Staff can view incoming queue orders for assigned stalls.
- [x] Staff can update order status through the queue flow.
- [x] Staff can reject pending orders before preparation.
- [x] The scheduler marks expired READY orders as UNCLAIMED.
- [x] Order transitions are written to the audit trail.

## Project Requirements Checklist
- [x] Pickup slot is enforced at least 15 minutes in the future.
- [x] Pickup slot is restricted to within 1 week of the current time.
- [x] Pickup slot is restricted to 7:00 AM through 6:00 PM.
- [x] One active order per student per stall is enforced.
- [x] Queue number is generated per stall per day.
- [x] Each stall has a maximum active queue of 100 orders.
- [x] Student UI shows queue usage and remaining slots.
- [x] Staff dashboard and reporting endpoints are present.
- [x] SSE order notifications are wired for order updates.
- [x] Demo data uses the current staff account and stall bootstrap flow.
- [~] Full end-to-end frontend/backend integration still needs final verification.
- [~] Deployment and staging readiness are still pending.

## Notes
- The checklist reflects the current codebase and the updated docs in `Project_Requirements.txt` and `Context/workflow.md`.
- If a future change alters queue or pickup rules, update this file alongside the requirements document.
