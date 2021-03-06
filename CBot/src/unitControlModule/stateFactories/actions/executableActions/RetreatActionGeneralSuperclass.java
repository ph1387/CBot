package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.Region;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringFactory;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_GeneralSuperclass.java --- Superclass for RetreatActions. <br>
 * <b>Notice:</b> <br>
 * The temporary retreat-Position has to be set by subclasses since this
 * determines if this Action can actually be taken or not.
 * 
 * @author P H - 10.03.2017
 *
 */
public abstract class RetreatActionGeneralSuperclass extends BaseAction {

	/**
	 * Private Class used for validating the Positions generated by the retreat
	 * group cluster.
	 */
	private class PositionValidator implements RetreatPositionValidator {

		// The own generated retreat Position of the leader. Used for verifying
		// that the other Positions are in the same Region.
		private Position retreatPosition;

		public PositionValidator(Position retreatPosition) {
			this.retreatPosition = retreatPosition;
		}

		@Override
		public boolean validatePosition(Point position, boolean isLeaderPosition) {
			boolean result = isLeaderPosition;

			if (!isLeaderPosition) {
				Position checkedPosition = new Position(position.getX(), position.getY());
				Pair<Region, Polygon> boundaries = BaseAction.findBoundariesPositionIsIn(checkedPosition,
						isLeaderPosition);
				boolean positionBlocked = SteeringFactory.isEndPositionBlockedByNeutralOrBuilding(checkedPosition);

				result = boundaries != null
						&& boundaries
								.equals(BaseAction.findBoundariesPositionIsIn(this.retreatPosition, isLeaderPosition))
						&& !positionBlocked;
			}

			return result;
		}

	}

	// The distance at which isDone() returns true.
	protected int minDistanceToGatheringPoint = 2 * Core.getInstance().getTileSize();

	// Generated Positions by this specific Unit (Temporary and final).
	protected Position generatedTempRetreatPosition = null;
	protected Position retreatPosition = null;

	// Vector related stuff.
	protected static final int ALPHA_MAX = 90;
	// vecEU -> Vector(enemyUnit, playerUnit)
	// vecUTP -> Vector(playerUnit, targetPosition)
	protected Vector vecEU, vecUTP;

	// Group related stuff.
	private RetreatPositionCluster retreatPositionCluster;
	private int distanceBetweenUnits = 48;
	private int matrixTurnAngleXDEG = 0;
	private int matrixTurnAngleYDEG = 90;
	private int maxGroupSize = 15;
	private int maxLeaderTileDistance = 5;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionGeneralSuperclass(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "retreatFromUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean isNearTargetPosition = false;

		// Either no target is present or the retreat Position was set and the
		// Unit is near it.
		if (this.target == null || (this.retreatPosition != null
				&& ((PlayerUnit) goapUnit).isNearPosition(this.retreatPosition, this.minDistanceToGatheringPoint))) {
			isNearTargetPosition = true;
		}
		return this.retreatPosition != null && isNearTargetPosition;
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Generate a temporary retreat Position when no real retreat Position
		// is defined. This means that either this is the first iteration or the
		// last iteration returned null as temporary retreat Position. Therefore
		// another one can be created.
		if (this.retreatPosition == null) {
			this.updateVectors(goapUnit);

			this.generatedTempRetreatPosition = this.generateTempRetreatPosition(goapUnit);

			// TODO: DEBUG INFO
			// Targeted retreat-Position (Vector Unit -> TargetPosition)
			bwapi.Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Position targetEndPosition = new Position(this.vecUTP.getX() + (int) (this.vecUTP.getDirX()),
					this.vecUTP.getY() + (int) (this.vecUTP.getDirY()));
			(new Vector(unit.getPosition(), targetEndPosition)).display(new Color(255, 255, 255));
		}

		// Only override the current retreatPosition if the action trigger is
		// set and move towards it.
		if (this.actionChangeTrigger && this.generatedTempRetreatPosition != null) {
			// The first ever found Position has to be added as temporary
			// retreat Position.
			this.retreatPosition = this.generatedTempRetreatPosition;

			// Generate the PositionValidator for the applied retreat Position
			// and the associated retreat cluster.
			PositionValidator validator = new PositionValidator(this.retreatPosition);
			this.retreatPositionCluster = new RetreatPositionCluster((RetreatUnit) this.currentlyExecutingUnit,
					new Point(this.retreatPosition), this.distanceBetweenUnits, this.matrixTurnAngleXDEG,
					this.matrixTurnAngleYDEG, validator);
		} else if (this.actionChangeTrigger && this.generatedTempRetreatPosition == null) {
			success = false;
		}

		// TODO: DEBUG INFO
		// Position to which the Unit retreats to
		if (this.retreatPosition != null) {
			(new Vector(((PlayerUnit) goapUnit).getUnit().getPosition(), this.retreatPosition))
					.display(new Color(255, 255, 0));
			(new Point(this.retreatPosition)).display(new Color(0, 255, 0));
		}

		// Prevent errors by checking the retreat Position first.
		if (this.retreatPosition != null) {
			success &= this.retreatPosition != null && ((PlayerUnit) goapUnit).getUnit().move(this.retreatPosition);
		}

		return this.retreatPosition != null && success;
	}

	/**
	 * Function used for generating a Position to which the Unit can retreat to.
	 * Is called when a new temporary retreat Position is needed.
	 * 
	 * @param goapUnit
	 *            the Unit that will perform the Action.
	 * @return a Position to which the performing Unit can retreat to.
	 */
	protected abstract Position generateTempRetreatPosition(IGoapUnit goapUnit);

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0.f;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && this.checkProceduralSpecificPrecondition(goapUnit)
				&& ((PlayerUnit) goapUnit).getAttackingEnemyUnitToReactTo() != null;
	}

	/**
	 * Each Subclass implements its own specific precondition test.
	 * 
	 * @param goapUnit
	 *            the Unit the test is performed on.
	 * @return true or false depending if the test was successful or not.
	 */
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	/**
	 * Function for updating the main Vectors this Class provides.
	 * 
	 * @param goapUnit
	 *            the Unit that is currently trying to retreat.
	 */
	private void updateVectors(IGoapUnit goapUnit) {
		this.vecEU = this.generateVectorFromEnemyToUnit(goapUnit,
				((PlayerUnit) goapUnit).getAttackingEnemyUnitToReactTo());
		this.vecUTP = this.generateVectorUnitToRetreatPosition(this.vecEU, goapUnit);
	}

	/**
	 * Function for generating a Vector from an (enemy) Unit to this current
	 * Unit.
	 * 
	 * @param goapUnit
	 *            the Unit at which the Vector will point.
	 * @param enemyUnit
	 *            the Unit the Vector will emerge from.
	 * @return a Vector pointing from an enemy Unit to the provided Unit.
	 */
	protected Vector generateVectorFromEnemyToUnit(IGoapUnit goapUnit, Unit enemyUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		// uPos -> Unit Position, ePos -> Enemy Position
		int uPosX = playerUnit.getUnit().getPosition().getX();
		int uPosY = playerUnit.getUnit().getPosition().getY();
		int ePosX = enemyUnit.getX();
		int ePosY = enemyUnit.getY();

		return new Vector(ePosX, ePosY, uPosX - ePosX, uPosY - ePosY);
	}

	/**
	 * Function for generating a Vector emerging from the executing Unit with
	 * the same direction Vector as a provided incoming Vector.
	 * 
	 * @param incomingVector
	 *            the Vector whose directions are projected onto the executing
	 *            Unit's Position.
	 * @param goapUnit
	 *            the currently executing Unit.
	 * @return a Vector emerging from the executing Unit's Position with the
	 *         directions of a provided Vector.
	 */
	protected Vector generateVectorUnitToRetreatPosition(Vector incomingVector, IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		// uPos -> Unit Position
		int uPosX = playerUnit.getUnit().getPosition().getX();
		int uPosY = playerUnit.getUnit().getPosition().getY();

		// Generate a Vector starting from the goapUnit itself with the
		// direction Vector from the enemy Unit to the goapUnit applied.
		return new Vector(uPosX, uPosY, incomingVector.getDirX(), incomingVector.getDirY());
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected void resetSpecific() {
		this.retreatPosition = null;
		this.generatedTempRetreatPosition = null;
		this.retreatPositionCluster = null;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return true;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		boolean success = false;

		// Add the Unit to the cluster if it was missing before.
		if (!this.retreatPositionCluster.containsRetreatUnit((RetreatUnit) groupMember)) {
			this.retreatPositionCluster.addUnit((RetreatUnit) groupMember);
		}

		// Reset each member separately if they arrived at the target location.
		if (this.isDone(groupMember)) {
			((PlayerUnit) groupMember).manuallyResetActions();
			success = true;
		} else {
			// Move the Unit to it's assigned location.
			Point position = this.retreatPositionCluster.getAssignedPosition((RetreatUnit) groupMember);
			success = ((PlayerUnit) groupMember).getUnit().move(new Position(position.getX(), position.getY()));

			// TODO: DEBUG INFO POSITION
			this.retreatPositionCluster.display();
		}

		return success;
	}

	@Override
	public int defineMaxGroupSize() {
		return this.maxGroupSize;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return this.maxLeaderTileDistance;
	}

}
