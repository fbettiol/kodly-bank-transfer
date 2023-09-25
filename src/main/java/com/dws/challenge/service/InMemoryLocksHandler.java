package com.dws.challenge.service;

import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In memory lock handler implementation to ensure mutual exclusion.
 */
@Service
public class InMemoryLocksHandler implements LocksHandler {

	private final Map<String, ReentrantLock> LOCKS_IN_USE = new ConcurrentHashMap<>();

	public void lockAccount(@NotNull String key) {
		LOCKS_IN_USE.computeIfAbsent(key, k -> new ReentrantLock());
		LOCKS_IN_USE.get(key).lock();
	}

	public void unlockAccount(@NotNull String key) {
		if (LOCKS_IN_USE.get(key) != null && LOCKS_IN_USE.get(key).isLocked()) {
			LOCKS_IN_USE.get(key).unlock();
		}
	}

	public boolean isLocked(@NotNull String accountId){
		return (LOCKS_IN_USE.get(accountId)!=null && LOCKS_IN_USE.get(accountId).isLocked());
	}

}
