package com.example.registrar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.Period;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.registrar.Application;
import com.example.registrar.ApplicationInfo;
import com.example.registrar.ApplicationState;
import com.example.registrar.ApplicationStatus;
import com.example.registrar.MemoryTokenRegistrar;
import com.example.registrar.PaymentService;
import com.example.registrar.TokenRegistrar;
import com.example.registrar.User;

class MemoryTokenRegistrarTest {
	
	private PaymentService paymentService;
	private TokenRegistrar tokenRegistrar;
	
	@BeforeEach
	void setup() {
		this.paymentService = Mockito.mock(PaymentService.class);
		this.tokenRegistrar = new MemoryTokenRegistrar(paymentService);
	}
	
	// normally we would want many more tests
	// for now only the entire process with a 'good' flow is shown

	@Test
	void testTokenProcess() {
		Application app = new Application();
		app.setContent("appcontent");
		
		when(paymentService.makePayment(any(), any())).thenReturn(true);
		
		// register new app
		ApplicationInfo info = tokenRegistrar.registerApplication(makeTestUser(), app);
		assertNotNull(info);
		assertEquals(ApplicationState.ACCEPTED, info.getState());
		
		// make the payment
		boolean result = tokenRegistrar.payForApplication(info);
		assertTrue(result);
		
		ApplicationStatus status = tokenRegistrar.getApplicationStatus(info);
		assertNotNull(status);
		assertEquals(ApplicationState.PAID, status.getApplicationState());
		
		// enter voting state
		result = tokenRegistrar.vote(info);
		assertTrue(result);
		assertNotNull(app.getVotingPeriodEnd());
		
		status = tokenRegistrar.getApplicationStatus(info);
		assertEquals(ApplicationState.VOTING, status.getApplicationState());
		
		// do some voting
		for(int i = 0; i < 100; i++) {
			result = tokenRegistrar.voteForApplication(makeTestUser(), info, true);
			assertTrue(result);
		}
		for(int i = 0; i < 99; i++ ) {
			result = tokenRegistrar.voteForApplication(makeTestUser(), info, false);
			assertTrue(result);
		}
		
		// Artificially adjust the voting period end (so it is ended...) 
		app.setVotingPeriodEnd(Instant.now().minus(Period.ofWeeks(3)));
		
		result = tokenRegistrar.tally(info);
		assertTrue(result);
		
		status = tokenRegistrar.getApplicationStatus(info);
		assertEquals(ApplicationState.APPROVED, status.getApplicationState());
	}

	private User makeTestUser() {
		User user = new User();
		user.setFirstName("first-" + UUID.randomUUID().toString());
		user.setLastName("last-" + UUID.randomUUID().toString());
		return user;
	}

}
