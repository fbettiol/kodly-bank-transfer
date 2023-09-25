package com.dws.challenge.service;

/**
 * Interface providing functionalities to lock accounts given an ID. Each time an account needs to be updated, the lock
 * for such account should be acquired first.
 */
public interface LocksHandler {

	/**
	 * It acquires the lock for a given account ID. If the account is already locked, it waits until it is unlocked.
	 *
	 * @param accountId - id of the account to be locked
	 */
	public void lockAccount(String accountId);

	/**
	 * It releases the lock for a given account ID. If the account was not locked, it has no effect.
	 *
	 * @param accountId - id of the account to be unlocked
	 */
	public void unlockAccount(String accountId);

	/**
	 * It checks if an account is locked
	 *
	 * @param accountId - id of the account to be unlocked
	 * @return - true if the account is locked by someone else. Otherwise, false.
	 */
	public boolean isLocked(String accountId);
}
