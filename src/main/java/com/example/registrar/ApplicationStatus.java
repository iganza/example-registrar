package com.example.registrar;

public class ApplicationStatus {

	private ApplicationState applicationState;

	public ApplicationStatus(ApplicationState applicationState) {
		super();
		this.applicationState = applicationState;
	}

	public ApplicationState getApplicationState() {
		return applicationState;
	}
	
}
