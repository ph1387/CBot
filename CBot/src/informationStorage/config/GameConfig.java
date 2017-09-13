package informationStorage.config;

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
	// TODO: UML ADD
	private boolean generateDefaultContendedPolygons = true;
	private boolean generateRegionAccessOrder = true;

	// BuildingOrderModule:
	private boolean enableBuildingOrderModuleUpdates = true;

	// PlayerUnit:
	private boolean allowModifiedConfidenceGeneration = true;

	// ----- Display / Debug functionalities:

	// UnitTrackerModule:
	// TODO: UML REMOVE
	// private boolean displayPlayerStrength = true;
	// TODO: UML REMOVE
	// private boolean displayEnemyStrength = true;
	// TODO: UML ADD
	private boolean displayPlayerAirStrength = false;
	// TODO: UML ADD
	private boolean displayPlayerGroundStrength = false;
	// TODO: UML ADD
	private boolean displayPlayerHealthStrength = false;
	// TODO: UML ADD
	private boolean displayPlayerSupportStrength = false;
	// TODO: UML ADD
	private boolean displayEnemyAirStrength = false;
	// TODO: UML ADD
	private boolean displayEnemyGroundStrength = false;
	// TODO: UML ADD
	private boolean displayEnemyHealthStrength = false;
	// TODO: UML ADD
	private boolean displayEnemySupportStrength = false;

	// Display:
	private boolean displayMapBoundaries = false;
	private boolean displayReservedSpacePolygons = false;
	private boolean displayMapContendedTilePositions = false;

	// UnitControlModule:
	private boolean displayQueueInformation = true;
	private boolean displayUnitConfidence = true;

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	public boolean enableDisplayPlayerAirStrength() {
		return this.displayPlayerAirStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayPlayerGroundStrength() {
		return this.displayPlayerGroundStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayPlayerHealthStrength() {
		return this.displayPlayerHealthStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayPlayerSupportStrength() {
		return this.displayPlayerSupportStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayEnemyAirStrength() {
		return this.displayEnemyAirStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayEnemyGroundStrength() {
		return this.displayEnemyGroundStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayEnemyHealthStrength() {
		return this.displayEnemyHealthStrength;
	}

	// TODO: UML ADD
	@Override
	public boolean enableDisplayEnemySupportStrength() {
		return this.displayEnemySupportStrength;
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

	// TODO: UML ADD
	@Override
	public boolean enableGenerateDefaultContendedPolygons() {
		return this.generateDefaultContendedPolygons;
	}

	@Override
	public boolean enableGenerateRegionAccessOrder() {
		return this.generateRegionAccessOrder;
	}

	@Override
	public boolean enableDisplayMapBoundaries() {
		return this.displayMapBoundaries;
	}

	@Override
	public boolean enableDisplayReservedSpacePolygons() {
		return this.displayReservedSpacePolygons;
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
