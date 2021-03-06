package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import bwapi.Position;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import informationStorage.DistantRegion;
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

	/**
	 * DistantBaseLocation.java --- A wrapper Class used for encapsulating a
	 * single BaseLocation. It stores the targeted BaseLocation as well as the
	 * total distance towards it.
	 * 
	 * @author P H - 10.03.2018
	 *
	 */
	private class DistantBaseLocation implements Comparable<DistantBaseLocation> {

		public double distance = -1.;
		public BaseLocation baseLocation;

		public DistantBaseLocation(double distance, BaseLocation baseLocation) {
			this.distance = distance;
			this.baseLocation = baseLocation;
		}

		// -------------------- Functions

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DistantBaseLocation) {
				return baseLocation.equals(((DistantBaseLocation) obj).baseLocation);
			} else {
				return super.equals(obj);
			}
		}

		@Override
		public int compareTo(DistantBaseLocation baseLocation) {
			// BaseLocations with a shorter total distances are moved towards
			// the front.
			return Double.compare(this.distance, baseLocation.distance);
		}

	}

	/**
	 * ScoutBaseLocationActionWrapper.java --- Wrapper Class used for smartly
	 * moving between ChokePoints.
	 * 
	 * @author P H - 18.03.2018
	 *
	 */
	private class ScoutBaseLocationActionWrapper implements SmartlyMovingActionWrapper {

		private Position targetPosition;

		public ScoutBaseLocationActionWrapper(Position targetPosition) {
			this.targetPosition = targetPosition;
		}

		@Override
		public boolean performInternalAction(IGoapUnit goapUnit, Object target) {
			return ((PlayerUnit) goapUnit).getUnit().move(this.targetPosition);
		}

		@Override
		public Position convertTarget(Object target) {
			return this.targetPosition;
		}

	}

	// The time after a BaseLocation may be searched again.
	public static final int BASELOCATIONS_TIME_PASSED = 300;

	private SmartlyMovingActionWrapper actionWrapper;
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
		boolean isNearTargetPosition = targetPositionChosen
				&& ((PlayerUnit) goapUnit).isNearTilePosition(((Position) this.targetPosition).toTilePosition(), null);
		boolean enemyKnown = ((PlayerUnit) goapUnit).currentState == PlayerUnit.UnitStates.ENEMY_KNOWN;

		return targetPositionChosen && (isNearTargetPosition || enemyKnown);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Smartly moving part of the action itself:
		try {
			if (this.targetPosition == null) {
				this.targetPosition = this.findClosestReachableBasePosition(goapUnit);
				this.actionWrapper = new ScoutBaseLocationActionWrapper(this.targetPosition);
			}

			success = this.performSmartlyMovingToRegion(goapUnit, this.actionWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}

	/**
	 * Function for finding the closest BaseLocation, that must be visited by
	 * the Unit in order to find enemy buildings.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * 
	 * @return the closest, reachable base Position.
	 */
	private Position findClosestReachableBasePosition(IGoapUnit goapUnit) {
		TreeSet<DistantBaseLocation> distantBaseLocations = this.generateBaseLocationDistances(goapUnit);
		Position closestPosition = null;

		// The start locations must be preferred in order for them to be checked
		// first.
		closestPosition = this.findClosestReachableBasePosition(distantBaseLocations, false);

		if (closestPosition == null) {
			closestPosition = this.findClosestReachableBasePosition(distantBaseLocations, true);
		}

		return closestPosition;
	}

	/**
	 * Function for finding the closest, reachable BaseLocation with a
	 * timeStamp:
	 * <p>
	 * currentTime - timeStamp >= timePassed
	 * 
	 * @param distantBaseLocations
	 *            the BaseLocations that are being checked.
	 * @param includeNonStartingLocations
	 *            set true if the function should include the non
	 *            StartingLocations in the search. If set to false only the
	 *            BaseLocations which can be started at are used.
	 * 
	 * @return the closest, reachable base Position with a matching timeStamp.
	 */
	private Position findClosestReachableBasePosition(TreeSet<DistantBaseLocation> distantBaseLocations,
			boolean includeNonStartingLocations) {
		PlayerUnit playerUnit = (PlayerUnit) this.currentlyExecutingUnit;
		HashMap<BaseLocation, Integer> baselocationsSearched = playerUnit.getInformationStorage()
				.getBaselocationsSearched();
		Iterator<DistantBaseLocation> treeIterator = distantBaseLocations.iterator();
		Position closestReachableBasePosition = null;

		while (closestReachableBasePosition == null && treeIterator.hasNext()) {
			BaseLocation baseLocation = treeIterator.next().baseLocation;

			if (baselocationsSearched.get(baseLocation) != null
					&& playerUnit.getUnit().hasPath(baseLocation.getRegion().getCenter())) {
				if (baseLocation.isStartLocation() || includeNonStartingLocations) {
					boolean enoughTimePassed;

					// Must be differentiated between because the scout will
					// otherwise only stand at the starting BaseLocation until
					// the time is reached (Then the actual scouting will
					// begin).
					if (Core.getInstance().getGame().elapsedTime() < BASELOCATIONS_TIME_PASSED) {
						enoughTimePassed = baselocationsSearched.get(baseLocation).equals(0);
					} else {
						int timeDifference = Core.getInstance().getGame().elapsedTime()
								- baselocationsSearched.get(baseLocation);
						enoughTimePassed = timeDifference >= BASELOCATIONS_TIME_PASSED;
					}

					if (enoughTimePassed) {
						closestReachableBasePosition = baseLocation.getRegion().getCenter();
					}
				}
			}
		}

		return closestReachableBasePosition;
	}

	/**
	 * Function for generating the TreeSet with which the most suitable
	 * BaseLocation to search is being calculated.
	 * 
	 * @param goapUnit
	 *            the executing Unit whose current Region is used to determine
	 *            the starting Region of the algorithm.
	 * @return a TreeSet containing all searchable BaseLocations on the map
	 *         wrapped inside DistantBaseLocation instances in order to compare
	 *         their total distances to the provided Unit.
	 */
	private TreeSet<DistantBaseLocation> generateBaseLocationDistances(IGoapUnit goapUnit) {
		Region startRegion = BWTA.getRegion(((PlayerUnit) goapUnit).getUnit().getPosition());
		HashSet<DistantRegion> distantRegions = ((PlayerUnit) goapUnit).getInformationStorage().getMapInfo()
				.getPrecomputedRegionDistances().get(startRegion);
		TreeSet<DistantBaseLocation> baseLocationDistances = new TreeSet<>();

		for (DistantRegion distantRegion : distantRegions) {
			Position currentCenter = distantRegion.getRegion().getCenter();

			for (BaseLocation baseLocation : distantRegion.getRegion().getBaseLocations()) {
				double distanceFromRegionCenter = currentCenter.getDistance(baseLocation.getPosition());

				baseLocationDistances.add(
						new DistantBaseLocation(distantRegion.getDistance() + distanceFromRegionCenter, baseLocation));
			}
		}

		return baseLocationDistances;
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
		this.actionWrapper = null;
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
