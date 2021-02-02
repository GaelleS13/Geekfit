package fr.uha.models;

import javax.persistence.Embeddable;

@Embeddable
public class Segment {

	int segmentId;
	CrossingPoint previousCrossingPoint;
	CrossingPoint nextCrossingPoint;
	Obstacle obstacle;

	public Segment() {
	}

	public Segment(int segmentId, CrossingPoint previousCrossingPoint, CrossingPoint nextCrossingPoint) {
		this.segmentId = segmentId;
		this.previousCrossingPoint = previousCrossingPoint;
		this.nextCrossingPoint = nextCrossingPoint;
	}

	public int getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}

	public CrossingPoint getPreviousCrossingPoint() {
		return previousCrossingPoint;
	}

	public void setPreviousCrossingPoint(CrossingPoint previousCrossingPoint) {
		this.previousCrossingPoint = previousCrossingPoint;
	}

	public CrossingPoint getNextCrossingPoint() {
		return nextCrossingPoint;
	}

	public void setNextCrossingPoint(CrossingPoint nextCrossingPoint) {
		this.nextCrossingPoint = nextCrossingPoint;
	}

	public Obstacle getObstacle() {
		return obstacle;
	}

	public void setObstacle(Obstacle obstacle) {
		this.obstacle = obstacle;
	}

	@Override
	public String toString() {
		return "{segmentId: " + segmentId + ", previousCrossingPoint: " + previousCrossingPoint
				+ ", nextCrossingPoint: " + nextCrossingPoint + ", obstacle: " + obstacle + "}";
	}

}
