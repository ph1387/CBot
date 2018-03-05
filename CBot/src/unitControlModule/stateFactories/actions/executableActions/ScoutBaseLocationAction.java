package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * ScoutBaseLocationsAction.java --- A scouting action for searching a base
 * location.
 * 
 * @author P H - 29.01.2017
 *
 */
public class ScoutBaseLocationAction extends BaseAction {

	// TODO: UML CHANGE 60
	// The time after a BaseLocation may be searched again.
	public static final int BASELOCATIONS_TIME_PASSED = 300;

	protected static Integer RANGE_TO_TARGET = null;

	private Position targetPosition = null;

	/**
	 * @param target
	 *            type: Null
	 */
	public ScoutBaseLocationAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", false));
		this.addPrecondition(new GoapState(0, "isScout", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean targetPositionChosen = this.targetPosition != null;
		boolean isNearTargetPosition = targetPositionChosen && ((PlayerUnit) goapUnit)
				.isNearTilePosition(((Position) this.targetPosition).toTilePosition(), RANGE_TO_TARGET);
		boolean enemyKnown = ((PlayerUnit) goapUnit).currentState == PlayerUnit.UnitStates.ENEMY_KNOWN;

		return targetPositionChosen && (isNearTargetPosition || enemyKnown);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;
		boolean executeMove = false;

		if (this.targetPosition == null) {
			this.targetPosition = this.findClosestReachableBasePosition();
			executeMove = true;
		}

		if (this.targetPosition != null && executeMove) {
			success &= ((PlayerUnit) goapUnit).getUnit().move(this.targetPosition);
		} else if (this.targetPosition == null) {
			success = false;
		}

		return success;
	}

	/**
	 * If no enemy buildings are are being known of the Bot has to search for
	 * them.
	 * 
	 * @return the closest, reachable base Position.
	 */
	protected Position findClosestReachableBasePosition() {
		Position closestPosition = null;

		// The start locations must be preferred.
		closestPosition = this.findCLosestReachableBaseLocation(BWTA.getStartLocations());

		if (closestPosition == null) {
			closestPosition = this.findCLosestReachableBaseLocation(BWTA.getBaseLocations());
		}

		return closestPosition;
	}

	/**
	 * Function for finding the closest, reachable BaseLocation with a
	 * timeStamp:
	 * <p>
	 * currentTime - timeStamp >= timePassed
	 * 
	 * @param baseLocations
	 *            the BaseLocations that are being checked.
	 * 
	 * @return the closest, reachable base Position with a matching timeStamp.
	 */
	private Position findCLosestReachableBaseLocation(List<BaseLocation> baseLocations) {
		PlayerUnit playerUnit = (PlayerUnit) this.currentlyExecutingUnit;
		HashMap<BaseLocation, Integer> baselocationsSearched = playerUnit.getInformationStorage()
				.getBaselocationsSearched();
		Position closestReachableBasePosition = null;
		int closestReachableBasePositionPathSize = -1;

		for (BaseLocation location : baseLocations) {
			Region baseRegion = ((BaseLocation) location).getRegion();

			// TODO: Needed Change: Check for blocking minerals
			if (baselocationsSearched.get(location) != null && playerUnit.getUnit().hasPath(baseRegion.getCenter())) {
				boolean enoughTimePassed, isInitialBaseLocation, isBetterBaseLocation;
				List<TilePosition> path = BWTA.getShortestPath(
						((PlayerUnit) this.currentlyExecutingUnit).getUnit().getTilePosition(),
						baseRegion.getCenter().toTilePosition());

				// Must be differentiated between because the scout will
				// otherwise only stand at the starting BaseLocation until the
				// time is reached (Then the actual scouting will begin).
				if (Core.getInstance().getGame().elapsedTime() < BASELOCATIONS_TIME_PASSED) {
					enoughTimePassed = baselocationsSearched.get(location).equals(0);
				} else {
					int timeDifference = Core.getInstance().getGame().elapsedTime()
							- baselocationsSearched.get(location);
					enoughTimePassed = timeDifference >= BASELOCATIONS_TIME_PASSED;
				}

				isInitialBaseLocation = closestReachableBasePosition == null && enoughTimePassed;
				isBetterBaseLocation = !isInitialBaseLocation && enoughTimePassed
						&& path.size() < closestReachableBasePositionPathSize;

				if (isInitialBaseLocation || isBetterBaseLocation) {
					closestReachableBasePosition = baseRegion.getCenter();
					closestReachableBasePositionPathSize = path.size();
				}
			}
		}
		return closestReachableBasePosition;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 10.f;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return true;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected void resetSpecific() {
		this.target = new Object();
		this.targetPosition = null;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		// All scouting actions are executed by a single Unit. It makes no
		// difference if one or more Units move towards the same location. It
		// can only be discovered once!
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
