package informationStorage.config;

import buildingOrderModule.BuildingOrderModule;

/**
 * IBuildingOrderModuleConfig.java --- Configuration Interface for the
 * {@link BuildingOrderModule} Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IBuildingOrderModuleConfig {

	/**
	 *
	 * @return true for enabling the generation of the updates on the
	 *         {@link BuildingOrderModule}, false for disabling them.
	 */
	public boolean enableBuildingOrderModuleUpdates();

}
