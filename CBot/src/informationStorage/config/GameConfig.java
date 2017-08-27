package informationStorage.config;

// TODO: UML ADD
/**
 * GameConfig.java --- Centralized configuration Class for a simple switching
 * between different configuration / debug / display modes.
 * 
 * @author P H - 24.08.2017
 *
 */
public class GameConfig implements IBuildingOrderModuleConfig, IDisplayConfig, IInitConfig, IPlayerUnitConfig,
		IUnitControlModuleConfig, IUnitTrackerModuleConfig {

	// ----- General Bot functionalities:
	// Disable following properties on custom maps!

	// Init:
	private boolean generateDefaultContendedTilePositions = true;
	private boolean generateReversedRegionAccessOrder = true;

	// BuildingOrderModule:
	private boolean enableBuildingOrderModuleUpdates = true;

	// PlayerUnit:
	private boolean allowModifiedConfidenceGeneration = true;

	// ----- Display / Debug functionalities:

	// UnitTrackerModule:
	private boolean displayPlayerStrength = false;
	private boolean displayEnemyStrength = false;

	// Display:
	private boolean displayMapBoundaries = false;
	private boolean displayMapContendedTilePositions = false;

	// UnitControlModule:
	private boolean displayQueueInformation = false;
	private boolean displayUnitConfidence = true;

	// -------------------- Functions

	@Override
	public boolean enableDisplayPlayerStrength() {
		return this.displayPlayerStrength;
	}

	@Override
	public boolean enableDisplayEnemyStrength() {
		return this.displayEnemyStrength;
	}

	@Override
	public boolean enableDisplayQueueInformation() {
		return this.displayQueueInformation;
	}

	@Override
	public boolean enableDisplayUnitConfidence() {
		return this.displayUnitConfidence;
	}

	@Override
	public boolean enableModifiedConfidenceGeneration() {
		return this.allowModifiedConfidenceGeneration;
	}

	@Override
	public boolean enableGenerateDefaultContendedTilePositions() {
		return this.generateDefaultContendedTilePositions;
	}

	@Override
	public boolean enableGenerateReversedRegionAccessOrder() {
		return this.generateReversedRegionAccessOrder;
	}

	@Override
	public boolean enableDisplayMapBoundaries() {
		return this.displayMapBoundaries;
	}

	@Override
	public boolean enableDisplayMapContendedTilePositions() {
		return this.displayMapContendedTilePositions;
	}

	@Override
	public boolean enableBuildingOrderModuleUpdates() {
		return this.enableBuildingOrderModuleUpdates;
	}

	// ------------------------------ Getter / Setter

}
