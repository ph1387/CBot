package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Position;
import bwapiMath.Point;
import bwapiMath.Vector;
import bwta.Chokepoint;
import bwta.Region;
import core.BWTAWrapper;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatActionInPreviousAdjacentRegion.java --- A retreat Action with which a
 * Unit can easily move between two different Regions and retreat to the one
 * that leads towards the Player's starting location. All this is based on the
 * Unit's distance towards the ChokePoints on the map. The Action is only
 * enabled if the Unit gets close to (The right) one.
 * 
 * @author P H - 27.08.2017
 *
 */
public class RetreatActionInPreviousAdjacentRegion extends RetreatActionGeneralSuperclass {

	// The range to a ChokePoint leading towards the starting location at which
	// the Action can be performed.
	private int acceptableChokePointRange = 320;
	private Position prevRegionRetreatPosition = null;
	// The percentage of the distance towards the previous Region Position that
	// the Unit has to travel in order for the Action to be finished.
	// Percentages are used since the distances that Units travel may vary.
	private double minTravelDistancePercentange = 0.75;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionInPreviousAdjacentRegion(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected Position generateTempRetreatPosition(IGoapUnit goapUnit) {
		return this.prevRegionRetreatPosition;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		// The ranges were not checked in any previous iteration.
		if (this.prevRegionRetreatPosition == null) {
			this.prevRegionRetreatPosition = this.generatePrevRegionRetreatPosition(goapUnit);

			success = this.prevRegionRetreatPosition != null;

			// Set the distance at which the isDone function returns true and
			// the Action is therefore finished.
			if (success) {
				this.minDistanceToGatheringPoint = (int) ((1. - this.minTravelDistancePercentange)
						* ((PlayerUnit) goapUnit).getUnit().getDistance(prevRegionRetreatPosition));
			}
		}
		// the previous Region's center was found.
		else {
			success = true;

			// TODO: DEBUG INFO POSITION
			// Display the retreat Position as one big (Red) circle.
			(new Point(this.prevRegionRetreatPosition)).display(25, new Color(255, 0, 0), true);
			(new Vector(((PlayerUnit) goapUnit).getUnit().getPosition(), this.prevRegionRetreatPosition)).display();
		}

		return success;
	}

	/**
	 * Function for generating the retreat Position of the previous Region that
	 * the Unit has to move to in order to reach the starting location.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to move to another Region.
	 * @return the Position in another Region that the Unit can / must retreat
	 *         to in order to reach the starting location.
	 */
	private Position generatePrevRegionRetreatPosition(IGoapUnit goapUnit) {
		// Find the current Region and the one that the Unit must retreat to
		// in order to get to the starting location.
		// Wrapper used since the Unit could be outside of a Region.
		Region curRegion = BWTAWrapper.getRegion(((PlayerUnit) goapUnit).getUnit().getPosition());
		Region prevRegion = ((PlayerUnit) goapUnit).getInformationStorage().getMapInfo().getReversedRegionAccessOrder()
				.get(curRegion);
		Position generatedPosition = null;

		// The Unit might be already in the starting Region (No previous
		// Region!).
		if (prevRegion != null) {
			// Check the distance to each ChokePoint. If the distance is in
			// the acceptable range, the Action can be performed.
			for (Chokepoint chokepoint : prevRegion.getChokepoints()) {
				if (((PlayerUnit) goapUnit).getUnit()
						.getDistance(chokepoint.getCenter()) <= this.acceptableChokePointRange) {
					generatedPosition = prevRegion.getCenter();

					break;
				}
			}
		}

		return generatedPosition;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Arbitrary cost. Can be chosen as desired. Only condition is, that it
		// must be smaller than the Vector retreat Action's cost since it only
		// then gets used when the Unit is near a possible retreat ChokePoint.
		return 10;
	}

	@Override
	protected void resetSpecific() {
		super.resetSpecific();

		this.prevRegionRetreatPosition = null;
	}

	// ------------------------------ Getter / Setter

}
