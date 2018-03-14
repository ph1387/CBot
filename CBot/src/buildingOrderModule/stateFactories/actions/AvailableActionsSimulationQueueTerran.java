package buildingOrderModule.stateFactories.actions;

import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_ControlTower;
import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_MachineShop;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Academy;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Armory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_CommandCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_EngineeringBay;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_MissileTurret;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Refinery;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_ScienceFacilitiy;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionTerran_Starport;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_CloakingField;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_SiegeMode;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_SpiderMines;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_StimPacks;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Goliath;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Medic;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SCV;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_ScienceVessel;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Vulture;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Wraith;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_CharonBoosters;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_InfantryArmor;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_InfantryWeapons;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_IonThrusters;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_U_238_Shells;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_VehiclePlating;
import buildingOrderModule.stateFactories.actions.executableActions.UpgradeActionTerran_VehicleWeapons;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueStartingTerranBionic;
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
		this.add(new ActionQueueStartingTerranBionic(1));

		this.add(new ConstructActionTerran_CommandCenter(1));
		this.add(new ConstructActionTerran_Refinery(1));
		this.add(new ConstructActionTerran_Barracks(1));
		this.add(new ConstructActionTerran_Factory(1));
		this.add(new ConstructActionTerran_Academy(1));
		this.add(new ConstructActionTerran_Starport(1));
		this.add(new ConstructActionTerran_EngineeringBay(1));
		this.add(new ConstructActionTerran_ScienceFacilitiy(1));
		this.add(new ConstructActionTerran_Armory(1));
		this.add(new ConstructActionTerran_MissileTurret(1));

		this.add(new TrainUnitActionTerran_SCV(1));
		this.add(new TrainUnitActionTerran_Marine(1));
		this.add(new TrainUnitActionTerran_SiegeTank(1));
		this.add(new TrainUnitActionTerran_Vulture(1));
		this.add(new TrainUnitActionTerran_Medic(1));
		this.add(new TrainUnitActionTerran_Wraith(1));
		this.add(new TrainUnitActionTerran_Goliath(1));
		this.add(new TrainUnitActionTerran_ScienceVessel(1));

		this.add(new BuildAddonTerran_MachineShop(1));
		this.add(new BuildAddonTerran_ControlTower(1));

		this.add(new ResearchActionTerran_SiegeMode(1));
		this.add(new ResearchActionTerran_StimPacks(1));
		this.add(new ResearchActionTerran_SpiderMines(1));
		this.add(new ResearchActionTerran_CloakingField(1));

		this.add(new UpgradeActionTerran_U_238_Shells(1));
		this.add(new UpgradeActionTerran_InfantryArmor(1));
		this.add(new UpgradeActionTerran_InfantryWeapons(1));
		this.add(new UpgradeActionTerran_VehiclePlating(1));
		this.add(new UpgradeActionTerran_VehicleWeapons(1));
		this.add(new UpgradeActionTerran_IonThrusters(1));
		this.add(new UpgradeActionTerran_CharonBoosters(1));
	}
}
