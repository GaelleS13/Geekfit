package fr.uha.models;

import javax.persistence.Embeddable;

@Embeddable
public class CrossingPoint {

	int crossingPointId;
	int score;

	public CrossingPoint() {
	}

	public CrossingPoint(int crossingPointId, int score) {
		this.crossingPointId = crossingPointId;
		this.score = score;
	}

	public int getCrossingPointId() {
		return crossingPointId;
	}

	public void setCrossingPointId(int crossingPointId) {
		this.crossingPointId = crossingPointId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "{crossingPointId: " + crossingPointId + ", score: " + score + "}";
	}
	
}
