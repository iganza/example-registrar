package com.example.registrar;

public interface TokenRegistrar {

	/**
	 * Register the given application.
	 *   - same content should not already exist
	 * 
	 * Initial state of creation Application will be ACCEPTED state
	 * 
	 * 
	 * @param user User application is to be associated to
	 * @param app  The application
	 * @return ApplicationInfo identifying the accepted application
	 */
	public ApplicationInfo registerApplication(User user, Application app);
	
	/**
	 * Pay for the given application
	 * 
	 * Application must be in ACCEPTED state.
	 * Successful payment moves application into VOTING state
	 * 
	 * @param info The Application identifier
	 * @param amount Amount of payment
	 * @return If the payment was successful
	 */
	public boolean payForApplication(ApplicationInfo info);
	
	/**
	 * Enter the voting state and prime the voting window
	 *   - only if the application is in PAID state
	 * 
	 * @param info Information pertaining to application
	 * @return If the voting state was set
	 */
	public boolean vote(ApplicationInfo info);
	
	/**
	 * Vote for a given application, identified by {@link ApplicationInfo}
	 *   - a given unique user can only cast a single vote
	 * 
	 * 
	 * @param user User whom will cast the vote
	 * @param info The Application identifier
	 * @param voteForApp
	 * @return
	 */
	public boolean voteForApplication(User user, ApplicationInfo info, boolean vorForApp);
	
	/**
	 * Tally votes of a given application
	 * 
	 * 
	 * @param info The Application identifier
	 * @return If the operation was a success
	 */
	public boolean tally(ApplicationInfo info);
	
	/**
	 * Get the status of a given application 
	 * 
	 * @param info The Application identifier
	 * @return The status of the application (if found)
	 */
	public ApplicationStatus getApplicationStatus(ApplicationInfo info);
}
