package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

//TODO: UML ADD
/**
 * LoadIntoActionTerran_Bunker.java --- A loading action with which a Unit can
 * "load itself" into a Terran_Bunker Unit. This can be used by i.e.
 * Terran_Marines or Terran_Firebats.
 * 
 * @author P H - 13.05.2018
 *
 */
public class LoadIntoActionTerran_Bunker extends LoadIntoAction {

	private static final int BUNKER_MAX_TILE_DISTANCE = 15;
	private static final int BUNKER_MAX_DISTANCE = Core.getInstance().getTileSize() * BUNKER_MAX_TILE_DISTANCE;

	/**
	 * @param target
	 *            type: Null
	 */
	public LoadIntoActionTerran_Bunker(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isLoadedIntoBunker", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isExecutionPossible(IGoapUnit goapUnit) {
		return this.loadingUnit != null || this.defineLoadingUnit(goapUnit) != null;
	}

	@Override
	protected Unit defineLoadingUnit(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		HashSet<Unit> bunkers = playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.get(UnitType.Terran_Bunker);
		HashSet<Unit> bunkersInRadius = this.extractPossibleBunkers(playerUnit, bunkers);
		Unit loadingUnit = playerUnit.getClosestUnit(bunkersInRadius);

		return loadingUnit;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting all Terran_Bunkers that the executing Unit can be
	 * loaded into from a HashSet of existing Units.
	 * 
	 * @param playerUnit
	 *            the executing PlayerUnit instance.
	 * @param bunkers
	 *            a HashSet of existing Terran_Bunker Units that are going to be
	 *            checked.
	 * @return a HashSet of bunker instances that the executing Unit can be
	 *         loaded into.
	 */
	private HashSet<Unit> extractPossibleBunkers(PlayerUnit playerUnit, HashSet<Unit> bunkers) {
		HashSet<Unit> viableBunkers = new HashSet<>();

		if (bunkers != null) {
			for (Unit bunker : bunkers) {
				if (this.canBunkerBeUsed(playerUnit, bunker)) {
					viableBunkers.add(bunker);
				}
			}
		}

		return viableBunkers;
	}

	// TODO: UML ADD
	/**
	 * Function for checking if a Terran_Bunker Unit instance can be used for
	 * loading the executing Unit into it. Various aspects are taken into
	 * account i.e. the remaining space or the distance towards the provided
	 * executing Unit.
	 * 
	 * @param playerUnit
	 *            the executing PlayerUnit instance.
	 * @param bunker
	 *            the (Terran_Bunker) Unit instance that is going to be checked.
	 * @return true if the given bunker instance can be used by the provided
	 *         PlayerUnit, otherwise false.
	 */
	private boolean canBunkerBeUsed(PlayerUnit playerUnit, Unit bunker) {
		boolean isBunker = bunker.getType() == UnitType.Terran_Bunker;
		boolean spaceFree = bunker.getSpaceRemaining() > 0;
		boolean finished = !bunker.isBeingConstructed();
		boolean inRange = playerUnit.getUnit().getDistance(bunker) <= BUNKER_MAX_DISTANCE;

		return isBunker && spaceFree && finished && inRange;
	}

}
