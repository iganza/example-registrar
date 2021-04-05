package com.example.registrar;

import java.time.Instant;
import java.time.Period;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token registrar implementation which stores all data in memory.
 * 
 * 
 * @author iganza
 *
 */
public class MemoryTokenRegistrar implements TokenRegistrar {
	
	private static final Period TWO_WEEKS = Period.ofWeeks(2);
	private static final Double PAYMENT_AMOUNT = Double.valueOf("4.44");
	
	private PaymentService paymentService;
	
	private Map<String, Application> applicationMap = new ConcurrentHashMap<>();
	
	public MemoryTokenRegistrar(PaymentService paymentService) {
		super();
		this.paymentService = paymentService;
	}

	private void validateNewApplication(Application app) {
		if (app.getContent() == null) {
			throw new IllegalArgumentException("Content must be set!");
		}
	}
	
	@Override
	public ApplicationInfo registerApplication(User user, Application app) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}
		if (app == null) {
			throw new IllegalArgumentException("Application cannot be null");
		}
		validateNewApplication(app);
		
		// got this far, we can accept the application
		// accepting the application here means associating the user,
		// updating the state and storing the application
		
		app.setId(generateUniqueId());
		app.setState(ApplicationState.ACCEPTED);
		app.setUser(user);
		app.setAcceptedAt(Instant.now());
		
		applicationMap.put(app.getId(), app);
		
		return makeApplicationInfo(app);
	}
	
	

	@Override
	public boolean payForApplication(ApplicationInfo info) {
		Application app = findApplicationById(info.getId());
		if (app.getState() != ApplicationState.ACCEPTED) {
			throw new IllegalArgumentException("Cannot pay for Application: must be in ACCEPTED state");
		}
		
		boolean paymentResult = paymentService.makePayment(app.getUser(), PAYMENT_AMOUNT);
		if (paymentResult) {
			app.setState(ApplicationState.PAID);
			return true;
		}
		
		return false;
	}

	@Override
	public boolean voteForApplication(User user, ApplicationInfo info, boolean voteForApp) {
		Application app = findApplicationById(info.getId());
		if (app.getState() != ApplicationState.VOTING) {
			throw new IllegalArgumentException("Cannot vote for Application: must be in VOTING state");
		}
		
		if (Instant.now().isAfter(app.getVotingPeriodEnd())) {
			return false;
		}
		
		app.vote(user, voteForApp);
		
		return true;
	}

	@Override
	public boolean tally(ApplicationInfo info) {
		Application app = findApplicationById(info.getId());
		if (app.getState() != ApplicationState.VOTING) {
			throw new IllegalArgumentException("Cannot vote for Application: must be in VOTING state");
		}
		
		if (Instant.now().isBefore(app.getVotingPeriodEnd())) {
			return false; // voting period has not expired yet
		}
		
		long votesFor = app
				.getVotes()
				.values()
				.stream()
				.filter(p -> p.equals(Boolean.TRUE))
				.count();
		
		long votesAgainst = app
				.getVotes()
				.values()
				.stream()
				.filter(p -> p.equals(Boolean.FALSE))
				.count();
		
		if (votesFor > votesAgainst) {
			app.setState(ApplicationState.APPROVED);
		}
		
		else if (votesFor == votesAgainst) {
			app.setState(ApplicationState.TIED);
		}
		
		else {
			app.setState(ApplicationState.REJECTED);
		}
		
		return true;
	}

	@Override
	public ApplicationStatus getApplicationStatus(ApplicationInfo info) {
		Application app = findApplicationById(info.getId());
		if (app != null) {
			return makeApplicationStatus(app);
		}
		else {
			throw new IllegalArgumentException("No application found for given info");
		}
	}

	/**
	 * Enter the voting state:
	 *   - current state must be PAID
	 *   - voting period begins at the state transition
	 * 
	 */
	@Override
	public boolean vote(ApplicationInfo info) {
		Application app = findApplicationById(info.getId());
		if (app.getState() != ApplicationState.PAID) {
			return false;
		}
		app.setVotingPeriodEnd(Instant.now().plus(TWO_WEEKS));
		app.setState(ApplicationState.VOTING);
		
		return true;
	}
	
	private String generateUniqueId() {
		// this is making the assumption there would never be an 'id' collision
		// ...not necessary true and would need to be looked at
		return UUID.randomUUID().toString();
	}
	
	private ApplicationInfo makeApplicationInfo(Application app) {
		return new ApplicationInfo(app.getId(), app.getState(), app.getAcceptedAt());
	}
	
	private ApplicationStatus makeApplicationStatus(Application app) {
		return new ApplicationStatus(app.getState());
	}
	
	private Application findApplicationById(String id) {
		Application app = applicationMap.get(id);
		if (id == null) {
			throw new IllegalArgumentException("Unable to find application with id: " + id);
		}
		return app;
	}

}
