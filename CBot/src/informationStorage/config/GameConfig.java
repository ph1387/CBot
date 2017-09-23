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
	private boolean generateDefaultContendedTilePositions = false;
	private boolean generateDefaultContendedPolygons = false;
	private boolean generateRegionAccessOrder = false;

	// BuildingOrderModule:
	private boolean enableBuildingOrderModuleUpdates = false;

	// PlayerUnit:
	private boolean allowModifiedConfidenceGeneration = false;

	// ----- Display / Debug functionalities:

	// UnitTrackerModule:
	private boolean displayPlayerAirStrength = false;
	private boolean displayPlayerGroundStrength = false;
	private boolean displayPlayerHealthStrength = false;
	private boolean displayPlayerSupportStrength = false;
	private boolean displayEnemyAirStrength = false;
	private boolean displayEnemyGroundStrength = false;
	private boolean displayEnemyHealthStrength = false;
	private boolean displayEnemySupportStrength = false;

	// Display:
	private boolean displayMapBoundaries = false;
	private boolean displayReservedSpacePolygons = false;
	private boolean displayMapContendedTilePositions = false;

	// UnitControlModule:
	private boolean displayQueueInformation = true;
	private boolean displayUnitConfidence = true;

	// -------------------- Functions

	@Override
	public boolean enableDisplayPlayerAirStrength() {
		return this.displayPlayerAirStrength;
	}

	@Override
	public boolean enableDisplayPlayerGroundStrength() {
		return this.displayPlayerGroundStrength;
	}

	@Override
	public boolean enableDisplayPlayerHealthStrength() {
		return this.displayPlayerHealthStrength;
	}

	@Override
	public boolean enableDisplayPlayerSupportStrength() {
		return this.displayPlayerSupportStrength;
	}

	@Override
	public boolean enableDisplayEnemyAirStrength() {
		return this.displayEnemyAirStrength;
	}

	@Override
	public boolean enableDisplayEnemyGroundStrength() {
		return this.displayEnemyGroundStrength;
	}

	@Override
	public boolean enableDisplayEnemyHealthStrength() {
		return this.displayEnemyHealthStrength;
	}

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
