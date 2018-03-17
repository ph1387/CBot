package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TilePosition;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * DestroyUnitAction.java --- An attacking action with which the unit can
 * perform an attack move to the specified target TilePosition.
 * 
 * @author P H - 07.02.2017
 *
 */
public class AttackMoveAction extends AttackActionGeneralSuperclass {

	private int maxGroupSize = 5;
	private int maxLeaderTileDistance = 5;

	/**
	 * @param target
	 *            type: TilePosition
	 */
	public AttackMoveAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isSpecificDone(IGoapUnit goapUnit) {
		// Either the Unit is near the target or an enemy is in weapon range and
		// therefore can be attacked.
		return ((PlayerUnit) goapUnit).isNearTilePosition((TilePosition) this.target, null)
				|| !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().isEmpty();
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = false;

		try {
			success = this.performBasedOnCurrentPosition((PlayerUnit) goapUnit);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}

	// TODO: UML ADD
	/**
	 * Function for performing the action smartly, whereas smartly means moving
	 * from ChokePoint to ChokePoint towards the target. This is due to the
	 * Units sometimes getting stuck in Regions with either blocking minerals or
	 * neutral structures. Therefore moving between ChokePoints is advised since
	 * the used access order (= breadth-search) includes these. ChokePoints are
	 * traversed until either the target Region matches the current one of the
	 * Unit or is one of the directly accessible ones of the current Region.
	 * 
	 * @param playerUnit
	 *            the executing Unit.
	 * @return true if the action was performed successfully, which means either
	 *         moving between ChokePoints or executing the actual action.
	 * @throws Exception
	 *             a Exception is thrown (Usually NullPointerException) when a
	 *             Region can not be found via BWTA and therefore no region
	 *             access order is available.
	 */
	private boolean performBasedOnCurrentPosition(PlayerUnit playerUnit) throws Exception {
		Region currentRegion = BWTA.getRegion(playerUnit.getUnit().getPosition());
		Region targetRegion = BWTA.getRegion((TilePosition) this.target);
		HashMap<Region, HashSet<Region>> regionAccessOrder = playerUnit.getInformationStorage().getMapInfo()
				.getPrecomputedRegionAcccessOrders().get(currentRegion);
		boolean success = false;

		// Target Region either is the current one or only one step / Region
		// away.
		if (targetRegion.equals(currentRegion) || regionAccessOrder.get(currentRegion).contains(targetRegion)) {
			success = playerUnit.getUnit().attack(((TilePosition) this.target).toPosition());
		}
		// Target Region is farther away.
		else {
			Chokepoint chokePointToMoveTo = this.findNextChokePointTowardsTarget(
					playerUnit.getInformationStorage().getMapInfo(), playerUnit.getUnit().getPosition(),
					((TilePosition) this.target).toPosition());

			success = playerUnit.getUnit().move(chokePointToMoveTo.getCenter());
		}

		return success;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((TilePosition) this.target).toPosition());
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().canAttack(((TilePosition) this.target).toPosition());
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return true;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		boolean success = false;

		try {
			success = this.performBasedOnCurrentPosition((PlayerUnit) groupMember);
		} catch (Exception e) {
			e.printStackTrace();
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
