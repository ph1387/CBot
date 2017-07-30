package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Vector;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
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

	// Has to be smaller than MIN_PIXELDISTANCE_TO_UNIT!
	// -> isDone() condition! v
	private static final int DIST_TO_GATHERING_POINT = Core.getInstance().getTileSize();
	protected static final int TILE_RADIUS_NEAR = 1;

	protected static HashSet<Position> gatheringPoints = new HashSet<Position>();

	protected Position generatedTempRetreatPosition = null;
	protected Position retreatPosition = null;

	// Vector related stuff
	protected static final int ALPHA_MAX = 90;
	protected double maxDistance = 10 * Core.getInstance().getTileSize();
	// vecEU -> Vector(enemyUnit, playerUnit)
	// vecUTP -> Vector(playerUnit, targetPosition)
	protected Vector vecEU, vecUTP;

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
		// Either no target is present or the retreat Position was set and the
		// Unit is near it => Remove the Position from the Collection of retreat
		// Positions.
		if (this.target == null || (this.retreatPosition != null
				&& ((PlayerUnit) goapUnit).isNearPosition(this.retreatPosition, DIST_TO_GATHERING_POINT))) {
			RetreatActionGeneralSuperclass.gatheringPoints.remove(this.retreatPosition);
		}

		// A retreat Position was set and is now no longer inside the Collection
		// of gathering Points => Got removed.
		return this.retreatPosition != null
				&& !RetreatActionGeneralSuperclass.gatheringPoints.contains(this.retreatPosition);
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
			Position targetEndPosition = new Position(vecUTP.getX() + (int) (vecUTP.getDirX()),
					vecUTP.getY() + (int) (vecUTP.getDirY()));
			Core.getInstance().getGame().drawLineMap(unit.getPosition(), targetEndPosition, new Color(255, 255, 255));
		}

		// Only override the current retreatPosition if the action trigger is
		// set and move towards it. This enables the ability of storing the
		// Positions inside a HashSet and moving other Units towards them
		// instead of constantly updating them, which would result in a Unit
		// only following them in one general direction. Once a Position is set,
		// stick with it.
		if (this.actionChangeTrigger && this.generatedTempRetreatPosition != null) {
			// The first ever found Position has to be added as temporary
			// retreat Position. This ensures, that isDone() returns false and
			// the action gets actually executed. The actual retreatPosition
			// gets set when performAction() gets called.
			this.retreatPosition = this.generatedTempRetreatPosition;
			RetreatActionGeneralSuperclass.gatheringPoints.add(this.generatedTempRetreatPosition);

			success &= this.retreatPosition != null && ((PlayerUnit) goapUnit).getUnit().move(this.retreatPosition);
		} else if (this.actionChangeTrigger && this.generatedTempRetreatPosition == null) {
			success = false;
		}

		// TODO: DEBUG INFO
		// Position to which the Unit retreats to
		if (this.retreatPosition != null) {
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(),
					this.retreatPosition, new Color(255, 255, 0));
			Core.getInstance().getGame().drawCircleMap(this.retreatPosition.getPoint(), 5, new Color(0, 255, 0), true);
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
				&& ((PlayerUnit) goapUnit).getClosestEnemyUnitInConfidenceRange() != null;
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
				((PlayerUnit) goapUnit).getClosestEnemyUnitInConfidenceRange());
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
	}
}
