package buildingOrderModule.scoringDirector.gameState;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

/**
 * GameState.java --- Class for representing a state in the game with an
 * associated score attached to it. Each state has a score attached to it that
 * resembles the importance of it. The higher the score the better.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class GameState {

	// States that can be used by other actions for generating their individual
	// score:
	public static final GameState Expansion_Focused = new GameStateFocused_Expansion();
	public static final GameState Technology_Focused = new GameStateFocused_Technology();
	public static final GameState Upgrade_Focused = new GameStateFocused_Upgrade();

	public static final GameState Refinery_Units = new GameStateFocused_Refinery();
	public static final GameState Building_Units = new GameStateUnits_Building();
	public static final GameState Worker_Units = new GameStateUnits_Worker();
	public static final GameState Combat_Units = new GameStateUnits_Combat();

	public static final GameState Cheap_Units = new GameStateUnits_Cheap();
	public static final GameState Expensive_Units = new GameStateUnits_Expensive();
	public static final GameState Mineral_Units = new GameStateUnits_Mineral();
	public static final GameState Gas_Units = new GameStateUnits_Gas();

	public static final GameState Flying_Units = new GameStateUnits_Flying();
	public static final GameState Bio_Units = new GameStateUnits_Bio();
	public static final GameState Machine_Units = new GameStateUnits_Machines();
	public static final GameState Support_Units = new GameStateUnits_Support();
	public static final GameState Healer_Units = new GameStateUnits_Healer();

	// The GameStates that rely on free training facilities: (Positive meaning!
	// => Higher score + more dividers!)
	public static final GameState FreeTrainingFacility_Center = new GameStateUnits_TrainingFacilitiesFree(
			Core.getInstance().getPlayer().getRace().getCenter());
	public static final GameState FreeTrainingFacility_Terran_Barracks = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Barracks);
	public static final GameState FreeTrainingFacility_Terran_CommandCenter = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Command_Center);
	public static final GameState FreeTrainingFacility_Terran_Factory = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Factory);
	public static final GameState FreeTrainingFacility_Terran_Starport = new GameStateUnits_TrainingFacilitiesFree(
			UnitType.Terran_Starport);

	// The GameStates that rely on idling training facilities: (Negative
	// meaning! => More dividers are added!)
	public static final GameState IdleTrainingFacility_Center = new GameStateUnits_TrainingFacilitiesIdle(
			Core.getInstance().getPlayer().getRace().getCenter());
	public static final GameState IdleTrainingFacility_Terran_Barracks = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Barracks);
	public static final GameState IdleTrainingFacility_Terran_CommandCenter = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Command_Center);
	public static final GameState IdleTrainingFacility_Terran_Factory = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Factory);
	public static final GameState IdleTrainingFacility_Terran_Starport = new GameStateUnits_TrainingFacilitiesIdle(
			UnitType.Terran_Starport);

	// GameStates focused on researching technologies in a specific field. I.e.
	// technologies for machine Units.
	public static final GameState ResearchBioUnits = new GameStateResearch_BioUnits();
	public static final GameState ResearchMachineUnits = new GameStateResearch_MachineUnits();
	public static final GameState ResearchFlyingUnits = new GameStateResearch_FlyingUnits();

	// GameStates focused on upgrading properties in a specific field. I.e.
	// upgrades for machine Units.
	public static final GameState UpgradeBioUnits = new GameStateUpgrade_BioUnits();
	public static final GameState UpgradeMachineUnits = new GameStateUpgrade_MachineUnits();
	public static final GameState UpgradeFlyingUnits = new GameStateUpgrade_FlyingUnits();

	// Specific GameStates for different Terran Units, Upgrades, Technologies
	// and buildings.
	// Units:
	public static final GameState SpecificUnit_Terran_Goliath = new GameStateSpecific_Unit(UnitType.Terran_Goliath);
	public static final GameState SpecificUnit_Terran_Marine = new GameStateSpecific_Unit(UnitType.Terran_Marine);
	public static final GameState SpecificUnit_Terran_Medic = new GameStateSpecific_Unit(UnitType.Terran_Medic);
	public static final GameState SpecificUnit_Terran_Science_Vessel = new GameStateSpecific_Unit(
			UnitType.Terran_Science_Vessel);
	public static final GameState SpecificUnit_Terran_Siege_Tank_Tank_Mode = new GameStateSpecific_Unit(
			UnitType.Terran_Siege_Tank_Tank_Mode);
	public static final GameState SpecificUnit_Terran_Vulture = new GameStateSpecific_Unit(UnitType.Terran_Vulture);
	public static final GameState SpecificUnit_Terran_Wraith = new GameStateSpecific_Unit(UnitType.Terran_Wraith);
	// Upgrades:
	public static final GameState SpecificUpgrade_Terran_Infantry_Armor = new GameStateSpecific_Upgrade(
			UpgradeType.Terran_Infantry_Armor);
	public static final GameState SpecificUpgrade_Terran_Infantry_Weapons = new GameStateSpecific_Upgrade(
			UpgradeType.Terran_Infantry_Weapons);
	public static final GameState SpecificUpgrade_Terran_Vehicle_Plating = new GameStateSpecific_Upgrade(
			UpgradeType.Terran_Vehicle_Plating);
	public static final GameState SpecificUpgrade_Terran_Vehicle_Weapons = new GameStateSpecific_Upgrade(
			UpgradeType.Terran_Vehicle_Weapons);
	public static final GameState SpecificUpgrade_Ion_Thrusters = new GameStateSpecific_Upgrade(
			UpgradeType.Ion_Thrusters);
	public static final GameState SpecificUpgrade_Charon_Boosters = new GameStateSpecific_Upgrade(
			UpgradeType.Charon_Boosters);
	public static final GameState SpecificUpgrade_U_238_Shells = new GameStateSpecific_Upgrade(
			UpgradeType.U_238_Shells);
	// Technologies:
	public static final GameState SpecificTech_Stim_Packs = new GameStateSpecific_Tech(TechType.Stim_Packs);
	public static final GameState SpecificTech_Tank_Siege_Mode = new GameStateSpecific_Tech(TechType.Tank_Siege_Mode);
	public static final GameState SpecificTech_Spider_Mines = new GameStateSpecific_Tech(TechType.Spider_Mines);
	public static final GameState SpecificTech_Cloaking_Field = new GameStateSpecific_Tech(TechType.Cloaking_Field);
	// Buildings:
	// TODO: UML RENAME SpecificImprovementFacility_Terran_Academy
	public static final GameState SpecificBuilding_Terran_Academy = new GameStateSpecific_Building(
			UnitType.Terran_Academy);
	// TODO: UML RENAME SpecificImprovementFacility_Terran_Engineering_Bay
	public static final GameState SpecificBuilding_Terran_Engineering_Bay = new GameStateSpecific_Building(
			UnitType.Terran_Engineering_Bay);
	// TODO: UML RENAME SpecificImprovementFacility_Terran_Science_Facility
	public static final GameState SpecifiBuilding_Terran_Science_Facility = new GameStateSpecific_Building(
			UnitType.Terran_Science_Facility);
	// TODO: UML RENAME SpecificImprovementFacility_Terran_Armory
	public static final GameState SpecificBuilding_Terran_Armory = new GameStateSpecific_Building(
			UnitType.Terran_Armory);

	// TODO: UML ADD
	/**
	 * Function for resetting all GameStates.
	 */
	public static void resetAll() {
		Expansion_Focused.reset();
		Technology_Focused.reset();
		Upgrade_Focused.reset();

		Refinery_Units.reset();
		Building_Units.reset();
		Worker_Units.reset();
		Combat_Units.reset();

		Cheap_Units.reset();
		Expensive_Units.reset();
		Mineral_Units.reset();
		Gas_Units.reset();

		Flying_Units.reset();
		Bio_Units.reset();
		Machine_Units.reset();
		Support_Units.reset();
		Healer_Units.reset();

		FreeTrainingFacility_Center.reset();
		FreeTrainingFacility_Terran_Barracks.reset();
		FreeTrainingFacility_Terran_CommandCenter.reset();
		FreeTrainingFacility_Terran_Factory.reset();
		FreeTrainingFacility_Terran_Starport.reset();

		IdleTrainingFacility_Center.reset();
		IdleTrainingFacility_Terran_Barracks.reset();
		IdleTrainingFacility_Terran_CommandCenter.reset();
		IdleTrainingFacility_Terran_Factory.reset();
		IdleTrainingFacility_Terran_Starport.reset();

		ResearchBioUnits.reset();
		ResearchMachineUnits.reset();
		ResearchFlyingUnits.reset();

		UpgradeBioUnits.reset();
		UpgradeMachineUnits.reset();
		UpgradeFlyingUnits.reset();

		SpecificUnit_Terran_Goliath.reset();
		SpecificUnit_Terran_Marine.reset();
		SpecificUnit_Terran_Medic.reset();
		SpecificUnit_Terran_Science_Vessel.reset();
		SpecificUnit_Terran_Siege_Tank_Tank_Mode.reset();
		SpecificUnit_Terran_Vulture.reset();
		SpecificUnit_Terran_Wraith.reset();

		SpecificUpgrade_Terran_Infantry_Armor.reset();
		SpecificUpgrade_Terran_Infantry_Weapons.reset();
		SpecificUpgrade_Terran_Vehicle_Plating.reset();
		SpecificUpgrade_Terran_Vehicle_Weapons.reset();
		SpecificUpgrade_Ion_Thrusters.reset();
		SpecificUpgrade_Charon_Boosters.reset();
		SpecificUpgrade_U_238_Shells.reset();

		SpecificTech_Stim_Packs.reset();
		SpecificTech_Tank_Siege_Mode.reset();
		SpecificTech_Spider_Mines.reset();
		SpecificTech_Cloaking_Field.reset();
		SpecificBuilding_Terran_Academy.reset();
		SpecificBuilding_Terran_Engineering_Bay.reset();
		SpecifiBuilding_Terran_Science_Facility.reset();
		SpecificBuilding_Terran_Armory.reset();
	}

	// The current score this state holds.
	private double currentScore = 0.;
	// The number of times the state is going to be divided.
	private int currentDivider = 1;
	// The frame time stamp of the last score update.
	protected int lastUpdateTimeStampScore = 0;
	// The frames that have passed since the last score update.
	protected int updateFramesPassedScore = 0;
	// The frame time stamp of the last divider update.
	protected int lastUpdateTimeStampDivider = 0;
	// The frames that have passed since the last divider update.
	protected int updateFramesPassedDivider = 0;

	public GameState() {

	}

	// -------------------- Functions

	// TODO: UML ADD
	/**
	 * Function for resetting all properties of the GameState instance.
	 */
	public void reset() {
		this.currentScore = 0.;
		this.currentDivider = 1;
		this.lastUpdateTimeStampScore = 0;
		this.updateFramesPassedScore = 0;
		this.lastUpdateTimeStampDivider = 0;
		this.updateFramesPassedDivider = 0;
	}

	/**
	 * Function for updating the current score of the GameState.
	 *
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 */
	public void updateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		int frameCount = Core.getInstance().getGame().getFrameCount();

		this.updateFramesPassedScore = frameCount - this.lastUpdateTimeStampScore;
		this.currentScore = this.generateScore(scoringDirector, manager);
		this.lastUpdateTimeStampScore = frameCount;
	}

	/**
	 * Function for updating the current divider of the GameState.
	 *
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 */
	public void updateDivider(ScoringDirector scoringDirector, BuildActionManager manager) {
		int frameCount = Core.getInstance().getGame().getFrameCount();

		this.updateFramesPassedDivider = frameCount - this.lastUpdateTimeStampDivider;
		this.currentDivider = this.generateDivider(scoringDirector, manager);
		this.lastUpdateTimeStampDivider = frameCount;
	}

	/**
	 * Function for generating a new score for the GameState that represents the
	 * state of the game in the are that the GameState is responsible for. This
	 * score (For simplicity) should be between 0 and 1.
	 * 
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 * @return a score based on the provided information and the area the
	 *         GameState is performing in.
	 */
	protected abstract double generateScore(ScoringDirector scoringDirector, BuildActionManager manager);

	/**
	 * Function for generating a new divider for the GameState that represents
	 * the state of the game in the are that the GameState is responsible for.
	 * The higher the return value, the stronger the diving impact is.
	 * 
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 * @return a divider based on the provided information and the area the
	 *         GameState is performing in.
	 */
	protected abstract int generateDivider(ScoringDirector scoringDirector, BuildActionManager manager);

	// ------------------------------ Getter / Setter

	public double getCurrentScore() {
		return currentScore;
	}

	public int getCurrentDivider() {
		return currentDivider;
	}

}
