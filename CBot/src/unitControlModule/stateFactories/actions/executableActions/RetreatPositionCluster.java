package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;

import bwapi.Color;
import bwapiMath.Point;
import bwapiMath.Vector;

/**
 * RetreatPositionCluster.java --- A Class for storing retreat Positions /
 * Points as well as generating them. It stores the members and generates a
 * matrix with {@link RetreatPositionClusterElement} for each member. The
 * generation of these elements is handled in a generation function. <br>
 * This function uses three Vectors:
 * <ul>
 * <li>The Vector from the actual members to the leader Unit and therefore the
 * direction from the former to the latter.</li>
 * <li>One direction Vector on the X-Axis that is used for generating the
 * coordinate of the final Point.</li>
 * <li>One direction Vector on the Y-Axis that is used for generating the
 * coordinate of the final Point.</li>
 * </ul>
 * The last two are the {@link #directionVectorX} and {@link #directionVectorY}
 * properties of this Class.
 * 
 * @author P H - 19.08.2017
 *
 */
public class RetreatPositionCluster {

	private RetreatPositionValidator retreatPositionValidator;

	// The initial default radius at which Points are being generated. Also
	// determines the size of the initial matrix (2*n+1).
	private int radiusDefault = 1;
	private RetreatPositionClusterElement[][] clusterElementMatrix;
	private HashMap<RetreatUnit, RetreatPositionClusterElement> mappedUnitsToClusterElements = new HashMap<>();
	// The current radius of at which Points will be generated around the
	// leader. This stores the STEPS (1, 2, 3, ...) not the distance around
	// the leader.
	private int radiusCurrent = this.radiusDefault;

	// Storages for information regarding the current state of the matrix:
	private int sizeMax = 0;
	private int sizeCurrent = 0;
	private int countInvalid = 0;

	// Leader specific information:
	private RetreatUnit leader;
	private Point leaderPosition;
	// Both direction Vectors that are used for generating the Points on the
	// map.
	private Vector directionVectorX;
	private Vector directionVectorY;

	// The distance that is kept between the generated Points.
	private int distanceBetweenUnits;
	// The angles which influence the direction Vectors.
	private int matrixTurnAngleXDEG;
	private int matrixTurnAngleYDEG;

	/**
	 * @param leader
	 *            the Unit that is the leader of the cluster. Mainly used for
	 *            determining the direction to other Units for a more accurate
	 *            choosing of retreat Positions.
	 * @param leaderPosition
	 *            the Position around which the other ones are being created
	 *            around.
	 * @param distanceBetweenUnits
	 *            the distance that is kept between the different retreat
	 *            Positions.
	 * @param matrixTurnAngleXDEG
	 *            the angle at which the X-direction Vector of the retreat
	 *            Position generator is being turned.
	 * @param matrixTurnAngleYDEG
	 *            the angle at which the Y-direction Vector of the retreat
	 *            Position generator is being turned.
	 * @param retreatPositionValidator
	 *            the validator that is being used for verifying any generated
	 *            retreat Positions.
	 */
	public RetreatPositionCluster(RetreatUnit leader, Point leaderPosition, int distanceBetweenUnits,
			int matrixTurnAngleXDEG, int matrixTurnAngleYDEG, RetreatPositionValidator retreatPositionValidator) {
		this.retreatPositionValidator = retreatPositionValidator;
		this.distanceBetweenUnits = distanceBetweenUnits;
		this.matrixTurnAngleXDEG = matrixTurnAngleXDEG;
		this.matrixTurnAngleYDEG = matrixTurnAngleYDEG;

		// Add all leader information before generating the matrix since it
		// depends on the inserted information.
		this.leader = leader;
		this.leaderPosition = leaderPosition;
		this.clusterElementMatrix = this.generateMatrix();
		this.countInvalid = this.countInvalidPositions();

		// Update the size (= Leader).
		this.updateSizeMax();
		this.sizeCurrent = 1;

		// Add the initial Unit in the middle.
		this.clusterElementMatrix[this.radiusCurrent][this.radiusCurrent].setUnit(this.leader);
	}

	// -------------------- Functions

	/**
	 * Function for generating a new matrix of
	 * {@link RetreatPositionClusterElement}s with already initialized elements
	 * and assigned Points. The current radius is taken into account when
	 * generating a new matrix. Also this function does NOT copy any existing
	 * references. It is only returning a new matrix without any assigned Units.
	 * 
	 * @return a newly generated matrix of
	 *         {@link RetreatPositionClusterElement}s.
	 */
	private RetreatPositionClusterElement[][] generateMatrix() {
		RetreatPositionClusterElement[][] endpositionClusterElements = new RetreatPositionClusterElement[this.radiusCurrent
				* 2 + 1][this.radiusCurrent * 2 + 1];

		// The Vector pointing towards the leader's end position.
		Vector vecLeaderToRetreatPos = new Vector(this.leader.defineCurrentPosition(), this.leaderPosition);
		vecLeaderToRetreatPos.setToLength(this.distanceBetweenUnits);

		// Two Vectors for applying the orientation to the generation of the
		// retreat positions. Both are necessary since the positions are mapped
		// onto a 2d map.
		this.directionVectorX = new Vector(this.leaderPosition.getX(), this.leaderPosition.getY(),
				vecLeaderToRetreatPos.getDirX(), vecLeaderToRetreatPos.getDirY());
		this.directionVectorY = this.directionVectorX.clone();
		this.directionVectorX.rotateLeftDEG(this.matrixTurnAngleXDEG);
		this.directionVectorY.rotateLeftDEG(this.matrixTurnAngleYDEG);

		// Instantiate every used matrix element. This takes the orientation of
		// the retreat Position to the leader into account.
		for (int i = 0; i < endpositionClusterElements.length; i++) {
			for (int j = 0; j < endpositionClusterElements[i].length; j++) {
				int multiplyAmountX = (i - this.radiusCurrent);
				int multiplyAmountY = (j - this.radiusCurrent);
				Vector vecPosX = this.directionVectorX.clone();
				Vector vecPosY = this.directionVectorY.clone();

				// Apply the different multipliers to the Vectors specifying the
				// positions.
				vecPosX.setDirX(vecPosX.getDirX() * multiplyAmountX);
				vecPosX.setDirY(vecPosX.getDirY() * multiplyAmountX);
				vecPosY.setDirX(vecPosY.getDirX() * multiplyAmountY);
				vecPosY.setDirY(vecPosY.getDirY() * multiplyAmountY);

				// Combine the position Vectors to generate the Vector pointing
				// towards the final position.
				Vector vecCombined = new Vector(this.leaderPosition.getX(), this.leaderPosition.getY(),
						vecPosX.getDirX() + vecPosY.getDirX(), vecPosX.getDirY() + vecPosY.getDirY());
				Point finalPosition = new Point(vecCombined.getX() + (int) (vecCombined.getDirX()),
						vecCombined.getY() + (int) (vecCombined.getDirY()), Point.Type.POSITION);

				// If the check fails, no Unit can be assigned to it (I.e. out
				// of bounds).
				endpositionClusterElements[i][j] = new RetreatPositionClusterElement(finalPosition,
						this.retreatPositionValidator.validatePosition(finalPosition));
			}
		}

		return endpositionClusterElements;
	}

	/**
	 * Function for counting all invalid Points of the current cluster elements.
	 * 
	 * @return the number of cluster elements that contain an invalid Point.
	 */
	private int countInvalidPositions() {
		int invalidPositionCount = 0;

		for (RetreatPositionClusterElement[] endpositionClusterElements : this.clusterElementMatrix) {
			for (RetreatPositionClusterElement endpositionClusterElement : endpositionClusterElements) {
				if (!endpositionClusterElement.isValid()) {
					invalidPositionCount++;
				}
			}
		}
		return invalidPositionCount;
	}

	// TODO: Possible Change: Add the ability to remove a Unit from the Cluster,
	// making it's spot free again. ALso add a function for decreasing the size
	// of the matrix when too many Units leave (Problem: leader leaves!).
	/**
	 * Function for adding a new Unit to the cluster of
	 * {@link RetreatPositionClusterElement}s. The Unit is assigned to a Point
	 * that it can retreat to.
	 * 
	 * @param retreatUnit
	 *            the Unit that is added to the cluster of retreat Units.
	 */
	public void addUnit(RetreatUnit retreatUnit) {
		// Generate a Vector that represents the relation of the retreat Unit
		// towards the leader of the cluster. This way the Units try to keep the
		// general direction towards the leader if possible.
		Vector vecRetreatUnitLeader = new Vector(this.leader.defineCurrentPosition(),
				retreatUnit.defineCurrentPosition());

		// The direction of the previously generated Vector is applied to a new
		// base starting at the retreat Position of the leader.
		Vector vecAppliedDirection = new Vector(this.leaderPosition.getX(), this.leaderPosition.getY(),
				vecRetreatUnitLeader.getDirX(), vecRetreatUnitLeader.getDirY());
		Point appliedDirectionPoint = new Point(vecAppliedDirection.getX() + (int) (vecAppliedDirection.getDirX()),
				vecAppliedDirection.getY() + (int) (vecAppliedDirection.getDirY()), Point.Type.POSITION);

		this.checkCapacity();

		// Find the closest free cluster element that can be accessed and add
		// the Unit to it.
		RetreatPositionClusterElement closestFreeClusterElement = this
				.extractClosestFreeClusterElement(appliedDirectionPoint);
		closestFreeClusterElement.setUnit(retreatUnit);

		this.mappedUnitsToClusterElements.put(retreatUnit, closestFreeClusterElement);
		this.sizeCurrent++;
	}

	/**
	 * Function for checking the capacity of the matrix and increasing it's size
	 * if necessary.
	 */
	private void checkCapacity() {
		while (this.sizeCurrent + this.countInvalid >= this.sizeMax) {
			this.increaseCapacity();
			this.updateSizeMax();
		}
	}

	/**
	 * Function for extracting the closest free cluster element from the
	 * available ones in the matrix. This function takes the current direction
	 * of the Unit to the stored leader into account to find the most
	 * appropriate {@link RetreatPositionClusterElement} whose stored Point is
	 * the closest one to the end of the applied Vector.
	 * 
	 * @param appliedDirectionPoint
	 *            the Point which resembles the direction of the Unit to the
	 *            leader applied to the leader's stored
	 *            {@link RetreatPositionClusterElement} Point. The closest free,
	 *            valid cluster element is the one the Unit must be assigned to.
	 * @return the cluster element that is most suited for the Unit to be
	 *         assigned to.
	 */
	private RetreatPositionClusterElement extractClosestFreeClusterElement(Point appliedDirectionPoint) {
		RetreatPositionClusterElement closestFreeClusterElement = null;
		Vector vecClosestFreeEndposition = null;

		// Find the closest free position in the matrix for the Unit.
		for (RetreatPositionClusterElement[] endpositionClusterElements : this.clusterElementMatrix) {
			for (RetreatPositionClusterElement endpositionClusterElement : endpositionClusterElements) {
				if (endpositionClusterElement.isFree()) {
					Vector vecEndpositionClusterElement = new Vector(appliedDirectionPoint,
							endpositionClusterElement.getPosition());

					if (closestFreeClusterElement == null
							|| vecEndpositionClusterElement.length() < vecClosestFreeEndposition.length()) {
						closestFreeClusterElement = endpositionClusterElement;
						vecClosestFreeEndposition = vecEndpositionClusterElement;
					}
				}
			}
		}

		return closestFreeClusterElement;
	}

	/**
	 * Function for increasing the capacity of the matrix. This is done by
	 * increasing the radius and copying all existing references into the new
	 * matrix.
	 */
	private void increaseCapacity() {
		this.radiusCurrent++;
		RetreatPositionClusterElement[][] newUnitMatrix = this.generateMatrix();

		// Copy the references into the new matrix:
		for (int i = 0; i < this.clusterElementMatrix.length; i++) {
			for (int j = 0; j < this.clusterElementMatrix[i].length; j++) {
				newUnitMatrix[i + 1][j + 1] = this.clusterElementMatrix[i][j];
			}
		}

		this.clusterElementMatrix = newUnitMatrix;
		this.countInvalid = this.countInvalidPositions();
	}

	/**
	 * Function for updating the information regarding the maximum number of
	 * Units the matrix can store.
	 */
	private void updateSizeMax() {
		this.sizeMax = this.clusterElementMatrix.length
				* this.clusterElementMatrix[this.clusterElementMatrix.length - 1].length;
	}

	/**
	 * Function for displaying the matrix onto the map.
	 * <ul>
	 * <li>Free space: Green</li>
	 * <li>Occupied space: Orange</li>
	 * <li>Invalid space: Red</li>
	 * </ul>
	 */
	public void display() {
		for (RetreatPositionClusterElement[] endpositionClusterElements : clusterElementMatrix) {
			for (RetreatPositionClusterElement endpositionClusterElement : endpositionClusterElements) {
				if (endpositionClusterElement.isFree()) {
					endpositionClusterElement.getPosition().display(new Color(0, 255, 0));
				} else if (!endpositionClusterElement.isValid()) {
					endpositionClusterElement.getPosition().display(new Color(255, 0, 0));
				} else {
					endpositionClusterElement.getPosition().display(new Color(255, 128, 0));
				}
			}
		}
	}

	/**
	 * Function for checking if the specified Unit is already part of the
	 * cluster.
	 * 
	 * @param retreatUnit
	 *            the Unit that will be checked for.
	 * @return true if the Unit is already part of the cluster, false if it is
	 *         not.
	 */
	public boolean containsRetreatUnit(RetreatUnit retreatUnit) {
		return this.mappedUnitsToClusterElements.containsKey(retreatUnit);
	}

	// ------------------------------ Getter / Setter

	/**
	 * Function for returning the Point that the given Unit is assigned to.
	 * 
	 * @param retreatUnit
	 *            the Unit whose assigned Point is being retrieved.
	 * @return The Point that the Unit was assigned to or null if the Unit was
	 *         not mapped to a Point.
	 */
	public Point getAssignedPosition(RetreatUnit retreatUnit) {
		Point position = null;

		if (this.mappedUnitsToClusterElements.containsKey(retreatUnit)) {
			position = this.mappedUnitsToClusterElements.get(retreatUnit).getPosition();
		}

		return position;
	}
}
