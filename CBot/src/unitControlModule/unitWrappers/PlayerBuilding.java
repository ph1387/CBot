package unitControlModule.unitWrappers;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import informationStorage.InformationStorage;

/**
 * PlayerBuilding.java --- Wrapper for a Player building. Uses a FSM to
 * determine its actions.
 * 
 * @author P H - 06.04.2017
 *
 */
public class PlayerBuilding {

	protected Unit unit;
	
	protected UnitType trainedUnit;
	protected UnitType constructedAddon;
	protected UpgradeType builtUpgrade;
	protected TechType researchedTech;
	
	protected boolean commandExecutable = true;

	private enum State {
		IDLE, TRAINING, CONSTRUCTING, UPGRADING, RESEARCHING
	}

	private State state = State.IDLE;
	
	private InformationStorage informationStorage;

	public PlayerBuilding(Unit unit, InformationStorage informationStorage) {
		this.unit = unit;
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	/**
	 * Update-function for the building.
	 */
	public void update() {
		// Initiate the different kinds of actions the building can take
		if (this.state == State.TRAINING && !this.unit.isTraining() && this.trainedUnit != null && this.commandExecutable) {
			this.unit.train(this.trainedUnit);
			this.commandExecutable = false;
		}
		// TODO: Test all following functionalities
		else if (this.state == State.CONSTRUCTING && !this.unit.isConstructing() && this.constructedAddon != null && this.commandExecutable) {
			this.unit.buildAddon(this.constructedAddon);
			this.commandExecutable = false;
		} else if (this.state == State.UPGRADING && !this.unit.isUpgrading() && this.builtUpgrade != null && this.commandExecutable) {
			this.unit.upgrade(this.builtUpgrade);
			this.commandExecutable = false;
		} else if (this.state == State.RESEARCHING && !this.unit.isResearching() && this.researchedTech != null && this.commandExecutable) {
			this.unit.research(this.researchedTech);
			this.commandExecutable = false;
		}
		// Reset all information
		else if (this.unit.isIdle()) {
			this.state = State.IDLE;

			this.trainedUnit = null;
			this.constructedAddon = null;
			this.builtUpgrade = null;
			this.researchedTech = null;
			
			this.commandExecutable = true;
		}

		// Try assigning work to the building.
		if (this.state == State.IDLE) {
			this.switchState();
		}
	}

	/**
	 * Function for switching the State of the building.
	 */
	private boolean switchState() {
		boolean resultMissing = true;
		UnitType unitToTrain = this.informationStorage.getTrainingQueue().peek();
		UnitType addonToBuild = this.informationStorage.getAddonQueue().peek();
		UpgradeType upgradeToBuild = this.informationStorage.getUpgradeQueue().peek();
		TechType techToResearch = this.informationStorage.getResearchQueue().peek();

		// Train an Unit
		if (resultMissing && unitToTrain != null && this.unit.canTrain(unitToTrain)
				&& this.informationStorage.getResourceReserver().canAffordConstruction(unitToTrain)) {
			this.state = State.TRAINING;
			this.trainedUnit = this.informationStorage.getTrainingQueue().poll();
			resultMissing = false;
		}

		// Construct an addon
		if (resultMissing && addonToBuild != null && this.unit.canBuildAddon(addonToBuild)
				&& this.informationStorage.getResourceReserver().canAffordConstruction(addonToBuild)) {
			this.state = State.CONSTRUCTING;
			this.constructedAddon = this.informationStorage.getAddonQueue().poll();
			resultMissing = false;
		}

		// Build an upgrade
		if (resultMissing && upgradeToBuild != null && this.unit.canUpgrade(upgradeToBuild)
				&& this.informationStorage.getResourceReserver().canAffordConstruction(upgradeToBuild)) {
			this.state = State.UPGRADING;
			this.builtUpgrade = this.informationStorage.getUpgradeQueue().poll();
			resultMissing = false;
		}

		// Research a technology
		if (resultMissing && techToResearch != null && this.unit.canResearch(techToResearch)
				&& this.informationStorage.getResourceReserver().canAffordConstruction(techToResearch)) {
			this.state = State.RESEARCHING;
			this.researchedTech = this.informationStorage.getResearchQueue().poll();
			resultMissing = false;
		}

		return resultMissing;
	}

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return unit;
	}
	
	public UnitType getTrainedUnit() {
		return trainedUnit;
	}

	public UnitType getConstructedAddon() {
		return constructedAddon;
	}

	public UpgradeType getBuiltUpgrade() {
		return builtUpgrade;
	}

	public TechType getResearchedTech() {
		return researchedTech;
	}
}