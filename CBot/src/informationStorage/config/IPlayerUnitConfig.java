package informationStorage.config;

import unitControlModule.unitWrappers.PlayerUnit;

//TODO: UML ADD
/**
 * IPlayerUnitConfig.java --- Configuration Interface for the
 * {@link PlayerUnit}. Class.
 * 
 * @author P H - 24.08.2017
 *
 */
public interface IPlayerUnitConfig {

	/**
	 *
	 * @return true for enabling the modification of the {@link PlayerUnit}
	 *         confidences, false for disabling it.
	 */
	public boolean enableModifiedConfidenceGeneration();

}
