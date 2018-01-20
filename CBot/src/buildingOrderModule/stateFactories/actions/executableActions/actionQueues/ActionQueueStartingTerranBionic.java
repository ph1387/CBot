package buildingOrderModule.stateFactories.actions.executableActions.actionQueues;

import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionRefinery;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Academy;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_EngineeringBay;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;
import bwapi.Race;
import core.Core;
import javaGOAP.IGoapUnit;

/**
 * ActionQueueStartingTerranBionic.java --- Action Queue for a Terran opening:
 * 
 * <p>
 * <q>The Bio Terran vs. Ultralisk/Zergling end game is characterized by the
 * Marine/Medic ball supported by Siege Tanks and Science Vessels against
 * Ultralisks and Zerglings supported by Defilers. This article spans the entire
 * match up of TvZ provided that it arrives at this endgame. This contrasts the
 * use of any Terran Mech Build Orders (For example the Fantasy Build.)</q> <br>
 * <ul>
 * <li>http://wiki.teamliquid.net/starcraft/Bionic_Terran_vs._Zerg_Guide
 * </ul>
 * </p>
 * 
 * @author P H - 18.11.2017
 *
 */
public class ActionQueueStartingTerranBionic extends ActionQueueStarting {

	/**
	 * @param target
	 *            type: Irrelevant, because the whole Queue will be cycled
	 *            through.
	 */
	public ActionQueueStartingTerranBionic(Object target) {
		super(target);

		this.actionQueue.add(new TrainUnitActionWorker(6));
		this.actionQueue.add(new ConstructActionTerran_Barracks(1));
		this.actionQueue.add(new TrainUnitActionWorker(2));
		this.actionQueue.add(new ConstructActionTerran_Barracks(1));
		this.actionQueue.add(new TrainUnitActionWorker(2));

		this.actionQueue.add(new TrainUnitActionTerran_Marine(3));

		this.actionQueue.add(new ConstructActionRefinery(1));
		this.actionQueue.add(new TrainUnitActionWorker(3));
		this.actionQueue.add(new TrainUnitActionTerran_Marine(2));
		this.actionQueue.add(new ConstructActionTerran_Academy(1));
		this.actionQueue.add(new TrainUnitActionWorker(1));

		this.actionQueue.add(new TrainUnitActionTerran_Marine(2));
		this.actionQueue.add(new TrainUnitActionWorker(2));

		this.actionQueue.add(new TrainUnitActionTerran_Marine(2));
		this.actionQueue.add(new TrainUnitActionWorker(2));
		this.actionQueue.add(new ConstructActionTerran_EngineeringBay(1));

		this.actionQueue.add(new TrainUnitActionTerran_Marine(2));
		this.actionQueue.add(new TrainUnitActionWorker(2));

		// At 34 workers:
		// this.actionQueue.add(new ConstructActionCenter(1));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return super.checkProceduralSpecificPrecondition(goapUnit)
				&& Core.getInstance().getGame().enemy().getRace() == Race.Zerg;
	}

}
