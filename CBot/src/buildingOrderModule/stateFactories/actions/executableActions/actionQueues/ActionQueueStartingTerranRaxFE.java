package buildingOrderModule.stateFactories.actions.executableActions.actionQueues;

import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_MachineShop;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionRefinery;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;

/**
 * ActionQueueStartingTerranRaxFE.java --- Action Queue for a Terran opening:
 * <p>
 * <q>The 1 Barracks Fast Expand is a build which aims to create an economic
 * advantage for the Terran, without being as risky as 14 CC. It negates much of
 * the effectiveness of the 12 Nexus build. The build can also be done in
 * response to a Protoss gas steal.</q> <br>
 * <ul>
 * <li>http://wiki.teamliquid.net/starcraft/1_Rax_FE_(vs._Protoss)
 * </ul>
 * </p>
 * 
 * @author P H - 30.04.2017
 *
 */
public class ActionQueueStartingTerranRaxFE extends ActionQueueStarting {

	/**
	 * @param target
	 *            type: Irrelevant, because the whole Queue will be cycled
	 *            through.
	 */
	public ActionQueueStartingTerranRaxFE(Object target) {
		super(target);

		this.actionQueue.add(new TrainUnitActionWorker(6));
		this.actionQueue.add(new ConstructActionTerran_Barracks(1));
		this.actionQueue.add(new TrainUnitActionWorker(4));

		// Custom addition against rushes.
		this.actionQueue.add(new TrainUnitActionTerran_Marine(2));

		this.actionQueue.add(new ConstructActionCenter(1));
		this.actionQueue.add(new TrainUnitActionWorker(1));
		this.actionQueue.add(new ConstructActionRefinery(1));
		this.actionQueue.add(new TrainUnitActionTerran_Marine(2));
		this.actionQueue.add(new TrainUnitActionWorker(5));
		this.actionQueue.add(new ConstructActionTerran_Factory(1));
		this.actionQueue.add(new BuildAddonTerran_MachineShop(1));

		// Standard version
		// this.actionQueue.add(new TrainUnitActionWorker(4));
		// this.actionQueue.add(new ConstrucActionTerran_Factory(1));
		// this.actionQueue.add(new TrainUnitActionWorker(5));
		// this.actionQueue.add(new ConstructActionRefinery(1));
		// this.actionQueue.add(new TrainUnitActionWorker(1));
		// this.actionQueue.add(new ResearchActionTerran_SiegeMode(1));
		// this.actionQueue.add(new TrainUnitActionTerran_SiegeTank(1));
	}

	// -------------------- Functions

}
