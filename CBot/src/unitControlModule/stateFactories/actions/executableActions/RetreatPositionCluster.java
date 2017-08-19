package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapiMath.Point;
import bwapiMath.Vector;

// TODO: UML ADD JAVADOC
/**
 * RetreatPositionCluster.java --- 
 * @author P H - 19.08.2017
 *
 */
public class RetreatPositionCluster {
	
	private RetreatPositionValidator retreatPositionValidator;
	
	private int radiusDefault = 1;
	private RetreatPositionClusterElement[][] endpositionClusterElements;
	private int radiusCurrent = this.radiusDefault;
	private int sizeMax= 0;
	private int sizeCurrent = 0;
	private int countInvalid = 0;
	
	private RetreatUnit leader;
	private Point leaderPosition;
	private Vector directionVectorX;
	private Vector directionVectorY;
	
	private int distanceBetweenUnits;
	private int matrixAngle;
	
	public RetreatPositionCluster(RetreatUnit leader, Point leaderPosition, int distanceBetweenUnits, int matrixAngle, RetreatPositionValidator retreatPositionValidator) {
		this.retreatPositionValidator = retreatPositionValidator;
		this.distanceBetweenUnits = distanceBetweenUnits;
		this.matrixAngle = matrixAngle;
		
		// Add all leader information before generating the matrix since it depends on the inserted information.
		this.leader = leader;
		this.leaderPosition = leaderPosition;
		this.endpositionClusterElements = this.generateMatrix();
		this.countInvalid = this.countInvalidPositions();
		
		// Update the size (= Leader).
		this.updateSizeMax();
		this.sizeCurrent = 1;
		
		// Add the initial Unit in the middle.
		this.endpositionClusterElements[this.radiusCurrent][this.radiusCurrent].setUnit(this.leader);
	}
	
	// -------------------- Functions


	private RetreatPositionClusterElement[][] generateMatrix() {
		RetreatPositionClusterElement[][] endpositionClusterElements = new RetreatPositionClusterElement[this.radiusCurrent * 2 + 1][this.radiusCurrent * 2 + 1];
		
		// The Vector pointing towards the leader's end position.
		Vector vecLeaderToRetreatPos = new Vector(this.leader.defineCurrentPosition(), this.leaderPosition);
		vecLeaderToRetreatPos.setToLength(this.distanceBetweenUnits);
		
		// Two Vectors for applying the orientation to the generation of the retreat positions. Both are necessary since the positions are mapped onto a 2d map.
		this.directionVectorX = new Vector(this.leaderPosition.getX(), this.leaderPosition.getY(), vecLeaderToRetreatPos.getDirX(), vecLeaderToRetreatPos.getDirY());
		this.directionVectorY = this.directionVectorX.clone();
		this.directionVectorY.rotateLeftDEG(this.matrixAngle);
		
		// Instantiate every used matrix element. This takes the orientation of the retreat Position to the leader into account. The generated Positions are generated in a 90° angle and a parallel order to the initial retreatPposition.
		for (int i = 0; i < endpositionClusterElements.length; i++) {
			for (int j = 0; j < endpositionClusterElements[i].length; j++) {
				int multiplyAmountX = (i - this.radiusCurrent);
				int multiplyAmountY = (j - this.radiusCurrent);
				Vector vecPosX = this.directionVectorX.clone();
				Vector vecPosY = this.directionVectorY.clone();
				
				// Apply the different multipliers to the Vectors specifying the positions.
				vecPosX.setDirX(vecPosX.getDirX() * multiplyAmountX);
				vecPosX.setDirY(vecPosX.getDirY() * multiplyAmountX);
				vecPosY.setDirX(vecPosY.getDirX() * multiplyAmountY);
				vecPosY.setDirY(vecPosY.getDirY() * multiplyAmountY);
				
				// Combine the position Vectors to generate the Vector pointing towards the final position.
				Vector vecCombined = new Vector(this.leaderPosition.getX(), this.leaderPosition.getY(), vecPosX.getDirX() + vecPosY.getDirX(), vecPosX.getDirY() + vecPosY.getDirY());
				Point finalPosition = new Point(vecCombined.getX() + (int)(vecCombined.getDirX()), vecCombined.getY() + (int)(vecCombined.getDirY()), Point.Type.POSITION);
				
				// The final Position must be validated. If the check fails, no Unit can be assigned to it (I.e. out of bounds or in other Polygon).
				endpositionClusterElements[i][j] = new RetreatPositionClusterElement(finalPosition, this.retreatPositionValidator.validatePosition(finalPosition));
			}
		}
		
		return endpositionClusterElements;
	}
	
	private int countInvalidPositions() {
		int invalidPositionCount = 0;
		
		for (RetreatPositionClusterElement[] endpositionClusterElements2 : endpositionClusterElements) {
			for (RetreatPositionClusterElement endpositionClusterElement : endpositionClusterElements2) {
				if(!endpositionClusterElement.isValid()) {
					invalidPositionCount++;
				}
			}
		}
		return invalidPositionCount;
	}
	
	public void addUnit(RetreatUnit iRetreatUnit) {
		RetreatPositionClusterElement closestFreeEndposition = null;
		Vector vecClosestFreeEndposition = null;
		
		this.checkCapacity();

		// Find the closest free position in the matrix for the Unit.
		for (RetreatPositionClusterElement[] endpositionClusterElements : endpositionClusterElements) {
			for (RetreatPositionClusterElement endpositionClusterElement : endpositionClusterElements) {
				if(endpositionClusterElement.isFree()) {
					Vector vecEndpositionClusterElement = new Vector(iRetreatUnit.defineCurrentPosition(), endpositionClusterElement.getPosition());
					
					if(closestFreeEndposition == null || vecEndpositionClusterElement.length() < vecClosestFreeEndposition.length()) {
						closestFreeEndposition = endpositionClusterElement;
						vecClosestFreeEndposition = vecEndpositionClusterElement;
					}
				}
			}
		}
		
		// Insert the new Unit at the found matrix space.
		closestFreeEndposition.setUnit(iRetreatUnit);
		
		this.sizeCurrent++;
	}
	
	private void checkCapacity() {
		while(this.sizeCurrent + this.countInvalid >= this.sizeMax) {
			this.increaseCapacity();
			this.updateSizeMax();
		}
	}
	
	private void increaseCapacity() {
		this.radiusCurrent++;
		RetreatPositionClusterElement[][] newUnitMatrix = this.generateMatrix();
		
		// Copy the references into the new matrix:
		for(int i = 0; i < this.endpositionClusterElements.length; i++) {
			for(int j = 0; j < this.endpositionClusterElements[i].length; j++) {
				newUnitMatrix[i + 1][j + 1] = this.endpositionClusterElements[i][j];
			}
		}
		
		this.countInvalid = this.countInvalidPositions();
		this.endpositionClusterElements = newUnitMatrix;
	}
	
	private void updateSizeMax() {
		this.sizeMax = this.endpositionClusterElements.length * this.endpositionClusterElements[this.endpositionClusterElements.length - 1].length;
	}
	
	// TODO: WIP Needed?
	private void decreaseCapacity() {
		
	}
	
	// TODO: WIP DEBUG INFO
	public void display() {
		for (RetreatPositionClusterElement[] endpositionClusterElements : endpositionClusterElements) {
			for (RetreatPositionClusterElement endpositionClusterElement : endpositionClusterElements) {
				if(endpositionClusterElement.isFree()) {
					endpositionClusterElement.getPosition().display(new Color(0, 255, 0));
				} else if(!endpositionClusterElement.isValid()) {
					endpositionClusterElement.getPosition().display(new Color(255, 0, 0));
				} else {
					endpositionClusterElement.getPosition().display(new Color(255, 128, 0));
				}
			}
		}
	}
	
	// ------------------------------ Getter / Setter

}
