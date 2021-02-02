package fr.uha.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Suggestion {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	int suggestionId;
	String username;
	String description;
	boolean validated;
	int voteYes;
	int voteNo;

	public Suggestion() {
	}

	public Suggestion(int suggestionId, String username, String description) {
		this.validated = false;
		this.suggestionId = suggestionId;
		this.username = username;
		this.description = description;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public int getVoteYes() {
		return voteYes;
	}

	public void setVoteYes(int voteYes) {
		this.voteYes = voteYes;
	}

	public void addVoteYes() {
		this.voteYes++;
	}

	public void addVoteNo() {
		this.voteNo++;
	}

	public int getVoteNo() {
		return voteNo;
	}

	public void setVoteNo(int voteNo) {
		this.voteNo = voteNo;
	}

	public int getSuggestionId() {
		return suggestionId;
	}

	public void setSuggestionId(int suggestionId) {
		this.suggestionId = suggestionId;
	}

	public String getUsername() {
		return username;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "{id: " + suggestionId + ", username: " + username + ", description: " + description + ", voteYes: " + voteYes
				+ ", voteNo: " + voteNo + "}";
	}
	
}
