package fr.uha.models;

import java.util.ArrayList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	int userId;
	@ElementCollection
	ArrayList<Integer> challenges = new ArrayList<Integer>();;
	String username;
	String password;
	boolean admin;

	public User() {
	}

	public User(int userId, String username, String password) {
		this.admin = false;
		this.userId = userId;
		this.username = username;
		this.password = password;
	}

	public String toString() {
		return "{username: " + username + ", password: " + password + ", admin: "
				+ admin + "}";
	}
	

	public ArrayList<Integer> getChallenges() {
		return challenges;
	}

	public void setChallenges(ArrayList<Integer> challenges) {
		this.challenges = challenges;
	}
	
	public void addChallenge(int challenge) {
		this.challenges.add(challenge);
	}

	public void delChallenge(int challenge) {
		this.challenges.remove(challenge);
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
