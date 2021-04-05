package com.example.registrar;

import java.time.Instant;

public class ApplicationInfo {
	
	private String id;
	private ApplicationState state;
	private Instant acceptedAt;
	
	public ApplicationInfo(String id, ApplicationState state, Instant acceptedAt) {
		super();
		this.id = id;
		this.state = state;
		this.acceptedAt = acceptedAt;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ApplicationState getState() {
		return state;
	}
	public void setState(ApplicationState state) {
		this.state = state;
	}
	public Instant getAcceptedAt() {
		return acceptedAt;
	}
	public void setAcceptedAt(Instant acceptedAt) {
		this.acceptedAt = acceptedAt;
	}
	
}
