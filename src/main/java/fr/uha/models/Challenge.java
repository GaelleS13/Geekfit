package fr.uha.models;

import java.util.ArrayList;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import fr.uha.dao.DAO;

@Entity
@Table
public class Challenge {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int id;
	int challengeId;
	int mode;
	int score;
	String description;
	@ElementCollection
	ArrayList<CrossingPoint> crossingPoints = new ArrayList<CrossingPoint>();
	@ElementCollection
	ArrayList<Segment> segments = new ArrayList<Segment>();
	@ElementCollection
	ArrayList<Integer> admins = new ArrayList<Integer>();

	public Challenge() {
	}

	public Challenge(int challengeId, int mode, int score) {
		this.challengeId = challengeId;
		this.mode = mode;
		this.score = score;
	}

	public ArrayList<Integer> getAdmins() {
		return admins;
	}

	public void setAdmins(ArrayList<Integer> admins) {
		this.admins = admins;
	}

	public void addAdmin(int admin) {
		this.admins.add(admin);
	}

	public void delAdmin(int admin) {
		this.admins.remove(admin);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getChallengeId() {
		return challengeId;
	}

	public void setChallengeId(int challengeId) {
		this.challengeId = challengeId;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int finalScore) {
		this.score = finalScore;
	}

	public ArrayList<CrossingPoint> getCrossingPoints() {
		return crossingPoints;
	}

	public CrossingPoint getCrossingPoint(int id) {
		for (CrossingPoint cp : crossingPoints) {
			if (cp.getCrossingPointId() == id) {
				return cp;
			}
		}
		return null;
	}

	public void setCrossingPoints(ArrayList<CrossingPoint> crossingPoints) {
		this.crossingPoints = crossingPoints;
	}

	public void addCrossingPoint(CrossingPoint cross) {
		crossingPoints.add(cross);
	}

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public void setSegments(ArrayList<Segment> segments) {
		this.segments = segments;
	}

	public void addSegment(Segment segment) {
		segments.add(segment);
	}
	

	public void delSegment(int segmentId) {
		segments.remove(getSegment(segmentId));
	}

	@Override
	public String toString() {
		return "{challengeId: " + challengeId + ", description: " + description + ", mode: " + mode + ", finalScore: "
				+ score + ", crossingPoint: " + DAO.arrayToJson(crossingPoints) + ", segment: " + DAO.arrayToJson(segments) + "}";
	}

	public Segment getSegment(int segmentId) {
		for (Segment seg : segments) {
			if (seg.getSegmentId() == segmentId) {
				return seg;
			}
		}
		return null;
	}

}
