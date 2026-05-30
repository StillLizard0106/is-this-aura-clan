package com.is_this_aura_clan.CanteenQ.order;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class OrderNotificationService {

	private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> studentEmitters = new ConcurrentHashMap<>();

	public SseEmitter subscribeStudent(UUID studentId) {
		SseEmitter emitter = new SseEmitter(0L);
		registerEmitter(studentId, emitter);
		return emitter;
	}

	void registerEmitter(UUID studentId, SseEmitter emitter) {
		studentEmitters.computeIfAbsent(studentId, ignored -> new CopyOnWriteArrayList<>()).add(emitter);
		Runnable cleanup = () -> removeEmitter(studentId, emitter);
		emitter.onCompletion(cleanup);
		emitter.onTimeout(cleanup);
		emitter.onError(error -> cleanup.run());
	}

	public void publishStudentOrderUpdateAfterCommit(UUID studentId, OrderStatusNotification notification) {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					publishStudentOrderUpdate(studentId, notification);
				}
			});
			return;
		}

		publishStudentOrderUpdate(studentId, notification);
	}

	void publishStudentOrderUpdate(UUID studentId, OrderStatusNotification notification) {
		List<SseEmitter> emitters = studentEmitters.get(studentId);
		if (emitters == null || emitters.isEmpty()) {
			return;
		}

		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(notification);
			} catch (IOException | IllegalStateException exception) {
				removeEmitter(studentId, emitter);
			}
		}
	}

	private void removeEmitter(UUID studentId, SseEmitter emitter) {
		List<SseEmitter> emitters = studentEmitters.get(studentId);
		if (emitters == null) {
			return;
		}
		emitters.remove(emitter);
		if (emitters.isEmpty()) {
			studentEmitters.remove(studentId);
		}
	}
}
