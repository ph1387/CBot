package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Position;
import bwapi.UnitType;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_Medic;

// TODO: UML ADD
/**
 * TerranMedic_MoveBackToBase.java --- An Action with which a Terran_Medic can
 * retreat to the nearest base. This is needed when no healable / followable
 * Unit on the map exists. Using this action the Unit is prevented from simply
 * standing around and not reacting at all or blocking other Units. <br>
 * <b>Note:</b><br>
 * As soon as a Unit, that the executing one can support, is found on the map,
 * this action stops being executable.
 * 
 * @author P H - 21.10.2017
 *
 */
public class TerranMedic_MoveBackToBase extends BaseAction {

	// The distance at which the Unit stops trying to move towards the Base and
	// the isDone function returns true. This is needed since the Unit would
	// otherwise try to move towards a possible invalid / unreachable Position
	// and therefore getting stuck.
	private int minPixelDistanceToTarget = 128;

	/**
	 * @param target
	 *            type: Position
	 */
	public TerranMedic_MoveBackToBase(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isNearBase", true));
		this.addPrecondition(new GoapState(0, "isNearBase", false));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().move((Position) this.target);
	}

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && !this.followableUnitExists(goapUnit);
	}

	// TODO: UML ADD
	/**
	 * Function for testing if a healable / followable Unit exists on the map
	 * that the Terran_Medic can react to. This function excludes the
	 * UnitType.Terran_Medic since the action would otherwise not be performed
	 * when more than one single medic is on the map. Therefore only the other
	 * supportable UnitTypes are considered.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return true if a followable / healable Unit besides another Terran_Medic
	 *         exists on the map.
	 */
	private boolean followableUnitExists(IGoapUnit goapUnit) {
		boolean followableUnitExists = false;

		// A followable / healable Unit exists that the Unit can react to with
		// another action.
		for (UnitType unitType : PlayerUnitTerran_Medic.getHealableUnitTypes()) {
			// The UnitType must NOT be Terran_Medic since the action then would
			// only be executable when a single medic is on the map.
			if (unitType != UnitType.Terran_Medic) {
				if (((PlayerUnit) goapUnit).getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
						.getOrDefault(unitType, 0) > 0) {
					followableUnitExists = true;

					break;
				}
			}
		}

		return followableUnitExists;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		Integer closestCenterDistance = ((PlayerUnit) goapUnit).generateClosestCenterDistance();
		boolean isNearTarget = closestCenterDistance != null && this.minPixelDistanceToTarget >= closestCenterDistance;
		boolean followableUnitExists = this.followableUnitExists(goapUnit);

		// Either a healable / followable Unit exists or the Unit can / should
		// not move towards the target anymore.
		return this.target == null || isNearTarget || followableUnitExists;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((Position) this.target));
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

	// ------------------------------ Getter / Setter

}
