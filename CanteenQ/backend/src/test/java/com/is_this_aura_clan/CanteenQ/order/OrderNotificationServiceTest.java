package com.is_this_aura_clan.CanteenQ.order;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class OrderNotificationServiceTest {

	@Test
	void publishStudentOrderUpdateSendsToRegisteredEmitter() {
		OrderNotificationService service = new OrderNotificationService();
		UUID studentId = UUID.fromString("11111111-1111-1111-1111-111111111111");
		RecordingSseEmitter emitter = new RecordingSseEmitter();

		service.registerEmitter(studentId, emitter);
		service.publishStudentOrderUpdate(studentId, new OrderStatusNotification(UUID.fromString("22222222-2222-2222-2222-222222222222"), OrderStatus.READY, "Ready for pickup."));

		assertEquals(1, emitter.payloads.size());
		assertEquals(OrderStatus.READY, ((OrderStatusNotification) emitter.payloads.get(0)).status());
	}

	@Test
	void publishStudentOrderUpdateIgnoresMissingListeners() {
		OrderNotificationService service = new OrderNotificationService();

		service.publishStudentOrderUpdate(UUID.fromString("11111111-1111-1111-1111-111111111111"), new OrderStatusNotification(UUID.fromString("22222222-2222-2222-2222-222222222222"), OrderStatus.READY, "Ready for pickup."));
	}

	private static class RecordingSseEmitter extends SseEmitter {
		private final List<Object> payloads = new ArrayList<>();

		@Override
		public void send(Object object) throws IOException {
			payloads.add(object);
		}
	}
}
