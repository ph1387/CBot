package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import bwapiMath.Vector;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_ToNearestUnit.java --- An action with which a PlayerUnit (!)
 * can retreat to another Unit which is located inside a cone in front of the
 * executing one. The Unit the PlayerUnit is retreating to is chosen each time
 * the action is executed.
 * 
 * @author P H - 03.03.2017
 *
 */
public class RetreatActionToFurthestUnitInCone extends RetreatActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionToFurthestUnitInCone(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		// Get a possible Unit to which this unit can retreat to.
		Unit possibleRetreatUnit = this.tryFindingRetreatUnitInCone((PlayerUnit) goapUnit);
		boolean success = false;

		if(this.retreatPosition != null) {
			success = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
		} else if (possibleRetreatUnit != null) {
			this.generatedTempRetreatPosition = possibleRetreatUnit.getPosition();
			success = true;
		}
		return success;
	}

	/**
	 * Function for finding a Unit inside a cone in front of the current
	 * PlayerUnit that is bound by two Vectors (one rotated left, one rotated
	 * right). The used Vectors have to extend from the same origin for this
	 * function to work!
	 * 
	 * @param goapUnit
	 *            the PlayerUnit the Units are searched from.
	 * @return the Unit located inside the calculated cone with the greatest
	 *         distance to the given PlayerUnit or null, if none is found.
	 */
	private Unit tryFindingRetreatUnitInCone(PlayerUnit goapUnit) {
		Unit retreatUnit = null;

		try {
			Vector vecUTP = goapUnit.getVecUTP();
			Vector vecRotatedL = goapUnit.getVecUTPRotatedL();
			Vector vecRotatedR = goapUnit.getVecUTPRotatedR();

			for (Unit unit : goapUnit.getAllPlayerUnitsInRange((int) (vecUTP.length()))) {
				Vector vecToUnit = new Vector(vecUTP.getX(), vecUTP.getY(), unit.getPosition().getX() - vecUTP.getX(),
						unit.getPosition().getY() - vecUTP.getY());
				int distanceGoapUnitToUnit = goapUnit.getUnit().getDistance(unit);
				
				// -> If the Unit is between the left and right rotated Vectors
				// then the sign of both cross products of the Vectors is
				// positive.
				// => (AxB * AxC >= 0 && CxB * CxA >=0) = B is between A and C =
				// inside the created cone.
				if (vecRotatedL.getCrossProduct(vecToUnit) * vecRotatedL.getCrossProduct(vecRotatedR) >= 0
						&& vecRotatedR.getCrossProduct(vecToUnit) * vecRotatedR.getCrossProduct(vecRotatedL) >= 0) {
					if ((distanceGoapUnitToUnit > MIN_PIXELDISTANCE_TO_UNIT && retreatUnit == null)
							|| (distanceGoapUnitToUnit > MIN_PIXELDISTANCE_TO_UNIT && goapUnit.getUnit()
									.getDistance(retreatUnit) < distanceGoapUnitToUnit)) {
						retreatUnit = unit;
					}
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return retreatUnit;
	}
}
