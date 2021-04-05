package com.example.registrar;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Application {

	private String      id;
	private User      user;
	private String content;
	private ApplicationState state = ApplicationState.CREATED;
	private Instant votingPeriodEnd;
	private Instant acceptedAt;
	private Map<User, Boolean> votes;
	
	public Application() {
		super();
		this.votes = new HashMap<>();
	}
	
	public String getId() {
		return id;
	}

	public Map<User, Boolean> getVotes() {
		return votes;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Instant getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(Instant acceptedAt) {
		this.acceptedAt = acceptedAt;
	}

	public ApplicationState getState() {
		return state;
	}

	public void setState(ApplicationState state) {
		this.state = state;
	}

	

	public Instant getVotingPeriodEnd() {
		return votingPeriodEnd;
	}

	public void setVotingPeriodEnd(Instant votingPeriodEnd) {
		this.votingPeriodEnd = votingPeriodEnd;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public void vote(User user, boolean voteFor) {
		votes.put(user, voteFor);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Application other = (Application) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
}
