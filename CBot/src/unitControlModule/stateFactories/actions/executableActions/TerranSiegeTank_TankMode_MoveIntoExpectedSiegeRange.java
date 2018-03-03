package unitControlModule.stateFactories.actions.executableActions;

import java.util.List;

import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Line;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Region;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

/**
 * TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange.java --- Action for a
 * {@link PlayerUnitTerran_SiegeTank} to move into bombard / siege range of an
 * expected enemy Unit, therefore preparing itself for an incoming attack.
 * Preparing is better than simply reacting to enemies since morphing the tank
 * from Tank_Mode into Siege_Mode takes time. For this Action to be performable
 * the Unit has to be completely out of range of the enemy Unit (-> No Unit in
 * siege range / below siege range), therefore limiting it's use cases.
 * 
 * @author P H - 27.10.2017
 *
 */
public class TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange extends BaseAction {

	// A temporary generated Position the Unit can move to.
	private Position generatedPosition;
	private int minPixelDistanceToGeneratedPosition = 32;

	// The extra range that is going to be added towards the Units default siege
	// range and that a generated Position must include.
	// This is necessary since moving directly to a Position without extra range
	// would cause the enemy Unit to be directly on the edge of the tank's siege
	// range without giving it time to set up / morph into Siege_Mode properly.
	private int extraRange = 128;

	// The offset that is used to retrieve a second Position from the path
	// emerging from the enemy Unit and leading towards the Player's
	// starting location. This second Position is used to generate a Vector
	// which is then rotated 90°. All Positions on the line between the
	// borders of the map that the Vector generates can be used as Positions
	// for the Unit to move to. The larger the offset, the more accurate the
	// Positions are. On the other hand: Numbers that are too large can
	// produce a faulty direction!
	private int desiredIndexOffset = 8;

	// The length of the Vectors emerging from the start-Position that marks the
	// possible Position to move to. The length does not matter as long as it is
	// large enough for the Vectors to intersect wíth the current Region's
	// boundaries.
	private final int vectorLength = 320000;
	// The number of degrees the Vectors emerging from the start-Position that
	// marks the possible Position to move to are turned left / right.
	private int rotateDegrees = 90;

	/**
	 * @param target
	 *            type: Unit
	 */
	public TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "inExpectedSiegeRange", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
		this.addPrecondition(new GoapState(0, "isSieged", false));

		// Must NOT (!) already be in siege range. This prevents Units that are
		// already near enemy Units from using this action!
		this.addPrecondition(new GoapState(0, "inSiegeRange", false));
		this.addPrecondition(new GoapState(0, "belowSiegeRange", false));
		this.addPrecondition(new GoapState(0, "isExpectingEnemy", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = false;

		this.generatedPosition = this.generatePosition();

		// Only if one gets found, move towards it. This prevents access
		// violation errors.
		if (this.generatedPosition != null) {
			success = ((PlayerUnit) goapUnit).getUnit().move(this.generatedPosition);
		}
		return success;
	}

	/**
	 * Function for generating a Position used for the Action to move to in
	 * order to prepare for an expected enemy Unit.
	 * 
	 * @return a Position the Unit can move to in order to be prepared for an
	 *         expected, incoming enemy Unit.
	 */
	private Position generatePosition() {
		// The BaseLocation of the Player is chosen due to all Units retreating
		// to this Position.
		List<TilePosition> path = BWTA.getShortestPath(((Unit) this.target).getTilePosition(),
				Core.getInstance().getPlayer().getStartLocation());
		Integer possiblePositionIndex = this.generatePossiblePositionIndex(path);

		return this.tryGeneratingPossibleMovePosition(path, possiblePositionIndex);
	}

	/**
	 * Function for finding a suitable index at which the Terran_Siege_Tank
	 * could move to in order to be at a certain distance to the enemy Unit as
	 * well as on the path that it would have to take to move towards the
	 * Player's starting location.
	 * 
	 * @param path
	 *            the List of TilePositions the enemy Unit has to follow in
	 *            order to get to the Player's starting location (Emerging from
	 *            the enemy Unit).
	 * @return the index of a TilePosition of the provided List (Emerging from
	 *         the enemy Unit) which the Unit could move to.
	 */
	private Integer generatePossiblePositionIndex(List<TilePosition> path) {
		Integer possiblePositionIndex = null;

		// Chose a TilePosition on the path that can be used as a waiting
		// Position.
		for (int i = 0; i < path.size() && possiblePositionIndex == null; i++) {
			if (path.get(i).toPosition()
					.getDistance(((Unit) this.target).getPosition()) >= PlayerUnitTerran_SiegeTank.getMaxSiegeRange()
							+ this.extraRange) {
				possiblePositionIndex = i;
			}
		}
		return possiblePositionIndex;
	}

	/**
	 * Function for generating a Position that the executing Unit can move to in
	 * order to be prepared for an enemy attack. This function utilizes various
	 * Vector operations to ensure that the generated Position is indeed the
	 * shortest, free TilePosition the Unit can move to.
	 * 
	 * @param path
	 *            the List of TilePositions the enemy Unit has to follow in
	 *            order to get to the Player's starting location (Emerging from
	 *            the enemy Unit).
	 * @param possiblePositionIndex
	 *            the index of a TilePosition of the provided List (Emerging
	 *            from the enemy Unit) which the Unit could move to.
	 * @return a Position the Unit can move to without interfering with any
	 *         other Units that might be blocking a generated TilePosition.
	 */
	private Position tryGeneratingPossibleMovePosition(List<TilePosition> path, Integer possiblePositionIndex) {
		Position possibleMovePosition = null;

		// Try finding a possible Position to move to. Either use a generated
		// path or a default Position.
		try {
			Vector pathVector = this.generatePathVector(path, possiblePositionIndex);
			// Pair.first: left
			// Pair.second: right
			Pair<Position, Position> boundaryIntersections = this.generateBoundaryIntersections(pathVector);

			// Find the closest TilePosition on the path of the Vectors towards
			// the intersections.
			TilePosition startTilePosition = (new Position(pathVector.getX(), pathVector.getY())).toTilePosition();
			List<TilePosition> pathToIntersectionLeft = BWTA.getShortestPath(startTilePosition,
					boundaryIntersections.first.toTilePosition());
			List<TilePosition> pathToIntersectionRight = BWTA.getShortestPath(startTilePosition,
					boundaryIntersections.second.toTilePosition());
			TilePosition freeTilePositionLeft = this.extractFreeTilePosition(pathToIntersectionLeft);
			TilePosition freeTilePositionRight = this.extractFreeTilePosition(pathToIntersectionRight);
			TilePosition closestTilePosition = this.findCloserTilePosition(freeTilePositionLeft, freeTilePositionRight);

			possibleMovePosition = closestTilePosition.toPosition();
		} catch (Exception e) {
			e.printStackTrace();

			possibleMovePosition = path.get(possiblePositionIndex).toPosition();
		}

		return possibleMovePosition;
	}

	/**
	 * Function for generating a Vector from a TilePosition on a provided Path
	 * to a TilePosition with a larger index. This is used to provide a general
	 * direction Vector that can be used for calculating the estimated direction
	 * the enemy Unit will have to move in order to reach the Player's starting
	 * location. The provided index is the start of the generated Vector while a
	 * desired offset defines the number of TilePositions that are skipped
	 * before generating the Vector's end-Position.
	 * 
	 * @param path
	 *            the List of TilePositions the enemy Unit has to follow in
	 *            order to get to the Player's starting location (Emerging from
	 *            the enemy Unit).
	 * @param possiblePositionIndex
	 *            the index of a TilePosition of the provided List (Emerging
	 *            from the enemy Unit) which the Unit could move to.
	 * @return a Vector that describes the general direction the enemy Unit has
	 *         to move in order to reach the Player's starting location using a
	 *         path and a starting index (+ offset).
	 * @throws Exception
	 *             throws a basic Exception when the offset that is applied to
	 *             the TilePosition on the provided path is smaller than 1. If
	 *             this occurs no Vector can be generated since the
	 *             start-Position would be equal to the end-Position (=>
	 *             Point!).
	 */
	private Vector generatePathVector(List<TilePosition> path, int possiblePositionIndex) throws Exception {
		Vector vector = null;
		int currentIndexOffset = this.desiredIndexOffset;

		// Try to apply the desired offset to the found path index. If the
		// offset can not be applied, try decreasing it until a fitting one is
		// found (Minimum is 1 due to the Vector not being allowed to be of 0
		// length).
		while (vector == null) {
			try {
				// Note: Path extends from the target to the StartLocation!
				TilePosition tilePositionStart = path.get(possiblePositionIndex);
				TilePosition tilePositionEnd = path.get(possiblePositionIndex + currentIndexOffset);
				int tileSize = Core.getInstance().getTileSize();

				// The Vector is generated from the center of the TilePositions,
				// not form the left-upper edges.
				Position vecStart = new Position(tilePositionStart.toPosition().getX() + (tileSize / 2),
						tilePositionStart.toPosition().getY() + (tileSize / 2));
				Position vecEnd = new Position(tilePositionEnd.toPosition().getX() + (tileSize / 2),
						tilePositionEnd.toPosition().getY() + (tileSize / 2));
				vector = new Vector(vecStart, vecEnd);
			} catch (Exception e) {
				if (currentIndexOffset > 1) {
					currentIndexOffset--;
				} else {
					throw new Exception(
							"TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange: generatePosition(): Offset index <= 1");
				}
			}
		}

		return vector;
	}

	/**
	 * Function for creating two Vectors to intersect the map's / current
	 * Region's boundaries. The length can be set to any value as long as the
	 * boundaries of the start Position are intersected. Both Vectors are
	 * rotated left and right by a fixed amount.
	 * 
	 * @param pathVector
	 *            a Vector that describes the general direction the enemy Unit
	 *            has to move in order to reach the Player's starting location.
	 * @return the Pair of Positions that the generated Vectors, which got
	 *         rotated left and right by X-degrees, are intersecting the map's /
	 *         current Region's boundaries.
	 */
	private Pair<Position, Position> generateBoundaryIntersections(Vector pathVector) {
		Vector moveVectorLeft = pathVector.clone();
		Vector moveVectorRight = pathVector.clone();
		moveVectorLeft.setToLength(this.vectorLength);
		moveVectorRight.setToLength(this.vectorLength);
		moveVectorLeft.rotateLeftDEG(this.rotateDegrees);
		moveVectorRight.rotateRightDEG(this.rotateDegrees);

		// Find the intersections with the current Region's boundaries.
		Pair<Region, Polygon> boundaries = BaseAction.findBoundariesPositionIsIn(pathVector.toPosition());
		List<Pair<Line, Point>> intersectionsLeft = boundaries.second.findIntersections(new Line(moveVectorLeft));
		List<Pair<Line, Point>> intersectionsRight = boundaries.second.findIntersections(new Line(moveVectorRight));
		Position intersectionLeft = intersectionsLeft.get(0).second.toPosition();
		Position intersectionRight = intersectionsRight.get(0).second.toPosition();
		Vector vecToIntersectionLeft = new Vector(pathVector.toPosition(), intersectionLeft);
		Vector vecToIntersectionRight = new Vector(pathVector.toPosition(), intersectionRight);

		Position vecLeftEndPosition = new Position(
				vecToIntersectionLeft.getX() + (int) (vecToIntersectionLeft.getDirX()),
				vecToIntersectionLeft.getY() + (int) (vecToIntersectionLeft.getDirY()));
		Position vecRightEndPosition = new Position(
				vecToIntersectionRight.getX() + (int) (vecToIntersectionRight.getDirX()),
				vecToIntersectionRight.getY() + (int) (vecToIntersectionRight.getDirY()));

		// Pair.first: left
		// Pair.second: right
		return new Pair<>(vecLeftEndPosition, vecRightEndPosition);
	}

	/**
	 * Function for extracting the first free / non occupied TilePosition from a
	 * List of TilePositions.
	 * 
	 * @param pathToIntersection
	 *            the List of TilePosition that is going to be looked at.
	 * @return the first occurrence of a TilePosition for which the test
	 *         "getUnitsOnTile()" returns true.
	 */
	private TilePosition extractFreeTilePosition(List<TilePosition> pathToIntersection) {
		TilePosition freeTilePosition = null;
		Unit unit = ((PlayerUnit) this.currentlyExecutingUnit).getUnit();

		for (int i = 0; i < pathToIntersection.size() && freeTilePosition == null; i++) {
			List<Unit> unitsOnTile = Core.getInstance().getGame().getUnitsOnTile(pathToIntersection.get(i));
			boolean onlyExecutingUnitOnTile = unitsOnTile.size() == 1 && unitsOnTile.get(0) == unit;
			boolean noUnitOnTile = unitsOnTile.isEmpty();

			if (onlyExecutingUnitOnTile || noUnitOnTile) {
				freeTilePosition = pathToIntersection.get(i);
			}
		}
		return freeTilePosition;
	}

	/**
	 * Function for finding the closest TilePosition of a pair of two provided
	 * TilePositions to the currently executing Unit.
	 * 
	 * @param freeTilePositionLeft
	 *            the first TilePosition that is going to be checked.
	 * @param freeTilePositionRight
	 *            the second TilePosition that is going to be checked.
	 * @return the TilePosition of the provided ones that is closest to the
	 *         executing Unit.
	 */
	private TilePosition findCloserTilePosition(TilePosition freeTilePositionLeft, TilePosition freeTilePositionRight) {
		// Get the closest one of the possible TilePositions to move to.
		Unit unit = ((PlayerUnit) this.currentlyExecutingUnit).getUnit();
		TilePosition closerTilePosition = null;

		// Decide based on the distance towards the Unit itself.
		if (freeTilePositionLeft == null || freeTilePositionRight == null) {
			if (freeTilePositionLeft == null && freeTilePositionRight != null) {
				closerTilePosition = freeTilePositionRight;
			} else if (freeTilePositionLeft != null && freeTilePositionRight == null) {
				closerTilePosition = freeTilePositionLeft;
			}
		} else {
			if (unit.getDistance(freeTilePositionLeft.toPosition()) < unit
					.getDistance(freeTilePositionRight.toPosition())) {
				closerTilePosition = freeTilePositionLeft;
			} else {
				closerTilePosition = freeTilePositionRight;
			}
		}
		return closerTilePosition;
	}

	@Override
	protected void resetSpecific() {
		this.generatedPosition = null;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		// The target must not be a building since the tank would be unable to
		// reach a building being outside it's siege range since the building
		// can not move towards the tank itself.
		return this.target != null && !playerUnit.getUnit().isSieged() && playerUnit.getUnit().canMove()
				&& !((Unit) this.target).getType().isBuilding();
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1.f;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean isDone = true;

		if (this.target != null) {
			PlayerUnitTerran_SiegeTank siegeTank = (PlayerUnitTerran_SiegeTank) goapUnit;
			boolean positionReached = this.generatedPosition != null
					&& siegeTank.isNearPosition(this.generatedPosition, this.minPixelDistanceToGeneratedPosition);
			boolean enemyInRange = siegeTank.isInSiegeRange((Unit) this.target)
					|| siegeTank.isBelowSiegeRange((Unit) this.target);
			boolean noLongerExpectingEnemy = !siegeTank.isExpectingEnemy();

			isDone = positionReached || enemyInRange || noLongerExpectingEnemy;
		}
		return isDone;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}

	@Override
	public int defineMaxGroupSize() {
		return 0;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}

}
