package informationStorage.config;

import buildingOrderModule.BuildingOrderModule;
import unitControlModule.UnitControlModule;

/**
 * IUnitControlModuleConfig.java --- Configuration Interface for the
 * {@link UnitControlModule} Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IUnitControlModuleConfig {

	/**
	 *
	 * @return true for enabling the display of the forwarded / received and
	 *         therefore currently active building and upgrade Queues from the
	 *         {@link BuildingOrderModule} as well as the reserved resources,
	 *         false for disabling it.
	 */
	public boolean enableDisplayQueueInformation();

	/**
	 *
	 * @return true for enabling the display of the PlayerUnit confidences,
	 *         false for disabling it.
	 */
	public boolean enableDisplayUnitConfidence();

}
