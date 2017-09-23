package buildingOrderModule.stateFactories.actions;

import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_MachineShop;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_EngineeringBay;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_ScienceFacilitiy;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Starport;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionRefinery;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Academy;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Armory;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_SiegeMode;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_StimPacks;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Goliath;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Medic;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_ScienceVessel;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Vulture;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Wraith;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_InfantryArmor;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_InfantryWeapons;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_U_238_Shells;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_VehiclePlating;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_VehicleWeapons;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueStartingTerranRaxFE;

/**
 * AvailableActionsSimulationQueueTerran.java --- Available actions for the
 * Terran race.
 * 
 * @author P H - 30.04.2017
 *
 */
public class AvailableActionsSimulationQueueTerran extends AvailableActionsSimulationQueue {

	public AvailableActionsSimulationQueueTerran() {
		this.add(new ActionQueueStartingTerranRaxFE(1));

		this.add(new ConstructActionCenter(1));
		this.add(new ConstructActionRefinery(1));
		this.add(new ConstructActionTerran_Barracks(1));
		this.add(new ConstructActionTerran_Factory(1));
		this.add(new ConstructActionTerran_Academy(1));
		this.add(new ConstructActionTerran_Starport(1));
		this.add(new ConstructActionTerran_EngineeringBay(1));
		this.add(new ConstructActionTerran_ScienceFacilitiy(1));
		this.add(new ConstructActionTerran_Armory(1));

		this.add(new TrainUnitActionWorker(1));
		this.add(new TrainUnitActionTerran_Marine(1));
		this.add(new TrainUnitActionTerran_SiegeTank(1));
		this.add(new TrainUnitActionTerran_Vulture(1));
		this.add(new TrainUnitActionTerran_Medic(1));
		this.add(new TrainUnitActionTerran_Wraith(1));
		this.add(new TrainUnitActionTerran_Goliath(1));
		this.add(new TrainUnitActionTerran_ScienceVessel(1));

		this.add(new BuildAddonTerran_MachineShop(1));

		this.add(new ResearchActionTerran_SiegeMode(1));
		this.add(new ResearchActionTerran_StimPacks(1));

		this.add(new UpgradeActionTerran_U_238_Shells(1));
		this.add(new UpgradeActionTerran_InfantryArmor(1));
		this.add(new UpgradeActionTerran_InfantryWeapons(1));
		this.add(new UpgradeActionTerran_VehiclePlating(1));
		this.add(new UpgradeActionTerran_VehicleWeapons(1));
	}
}
