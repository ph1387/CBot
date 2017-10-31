package unitControlModule.stateFactories.actions.executableActions;

import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

// TODO: UML ADD
/**
 * TerranSiegeTank_TankMode_MoveIntoExpectedSiegeRange.java --- Action for a
 * {@link PlayerUnitTerran_SiegeTank} to move into bombard / siege range of an
 * expected enemy Unit, therefore preparing itself for an incoming attack.
 * Preparing is better than simply reacting to enemies since morphing the tank
 * from Tank_Mode into Siege_Mode takes time.
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
	private int extraRange = 64;

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

	// TODO: WIP IMPROVE FUNCTIONALITY
	// TODO: UML ADD
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
		Position generatedPosition = null;

		// Chose a TilePosition on the path that can be used as a waiting
		// Position.
		for (int i = 0; i < path.size() && generatedPosition == null; i++) {
			if (path.get(i).toPosition()
					.getDistance(((Unit) this.target).getPosition()) >= PlayerUnitTerran_SiegeTank.getMaxSiegeRange()
							+ this.extraRange) {
				generatedPosition = path.get(i).toPosition();
			}
		}
		return generatedPosition;
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
