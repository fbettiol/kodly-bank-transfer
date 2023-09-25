package com.dws.challenge;

import com.dws.challenge.service.InMemoryLocksHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//TODO: use a library to improve multithreading testing.
@ExtendWith(SpringExtension.class)
@SpringBootTest
class InMemoryLocksHandlerTest {

	private InMemoryLocksHandler uut;

	@BeforeEach
	public void setUp() {
		this.uut = new InMemoryLocksHandler();
	}

	@Test
	void testLock() {
		//Given - When
		uut.lockAccount("key");

		//Then
		assertTrue(uut.isLocked("key"));
	}

	@Test
	void testLockUnLock() {
		//Given - When
		uut.lockAccount("key");
		uut.unlockAccount("key");

		//Then
		assertFalse(uut.isLocked("key"));
	}

}
