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

		// All three callbacks do the same thing: remove the emitter so we stop
    	// trying to send to a dead connection. The emitter itself handles closing.
		Runnable cleanup = () -> removeEmitter(studentId, emitter);
		emitter.onCompletion(cleanup); // client disconnected cleanly
		emitter.onTimeout(cleanup); // SseEmitter timeout reached (0L = never, but kept for safety)
		emitter.onError(error -> cleanup.run()); // network error or write failure
	}

	public void publishStudentOrderUpdateAfterCommit(UUID studentId, OrderStatusNotification notification) {

	// SSE events must be sent after the transaction commits, not during.
    // If we sent the event mid-transaction and the commit later failed,
    // the student would see a status update for a change that never actually happened.
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					publishStudentOrderUpdate(studentId, notification);
				}
			});
			return;
		}

		// No active transaction (e.g. called from a test or a non-transactional context) —
    	// publish immediately.
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

				// IllegalStateException is thrown by Spring when send() is called
        		// after the emitter has already been completed or timed out.
        		// We treat it the same as an IOException: remove the stale emitter.
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
