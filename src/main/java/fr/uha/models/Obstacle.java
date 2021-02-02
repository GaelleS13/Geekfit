package fr.uha.models;

import javax.persistence.Embeddable;

@Embeddable
public class Obstacle {

	int obstacleId;
	String obstacle;

	public Obstacle() {
	}

	public Obstacle(int obstacleId, String obstacle) {
		this.obstacle = obstacle;
		this.obstacleId = obstacleId;
	}

	public int getObstacleId() {
		return obstacleId;
	}

	public void setObstacleId(int obstacleId) {
		this.obstacleId = obstacleId;
	}

	public String getObstacle() {
		return obstacle;
	}

	public void setObstacle(String obstacle) {
		this.obstacle = obstacle;
	}

	@Override
	public String toString() {
		return "{obstacleId: " + obstacleId + ", obstacle: " + obstacle + "}";
	}
	
}
