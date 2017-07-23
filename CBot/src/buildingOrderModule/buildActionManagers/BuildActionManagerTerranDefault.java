package buildingOrderModule.buildActionManagers;

import java.util.HashMap;
import java.util.HashSet;

import buildingOrderModule.CommandSender;
import buildingOrderModule.stateFactories.StateFactory;
import buildingOrderModule.stateFactories.StateFactoryTerranBasic;
import bwapi.TechType;
import bwapi.UpgradeType;
import informationStorage.InformationStorage;

/**
 * BuildActionManagerTerranDefault.java --- Class for controlling the building,
 * research etc. behavior of a Terran type bot.
 * 
 * @author P H - 28.04.2017
 *
 */
public class BuildActionManagerTerranDefault extends BuildActionManager {

	public BuildActionManagerTerranDefault(CommandSender sender, InformationStorage informationStorage) {
		super(sender, informationStorage);
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerranBasic();
	}
	
	@Override
	protected HashSet<TechType> defineDesiredTechnologies() {
		HashSet<TechType> desiredTechnologies = new HashSet<>();
		
		desiredTechnologies.add(TechType.Healing);
		desiredTechnologies.add(TechType.Stim_Packs);
		desiredTechnologies.add(TechType.Tank_Siege_Mode);
		
		return desiredTechnologies;
	}

	@Override
	protected HashMap<UpgradeType, Integer> defineDesiredUpgradeTypes() {
		HashMap<UpgradeType, Integer> desiredUpgradeTypes = new HashMap<>();
		
		desiredUpgradeTypes.put(UpgradeType.Terran_Infantry_Weapons, UpgradeType.Terran_Infantry_Weapons.maxRepeats());
		desiredUpgradeTypes.put(UpgradeType.Terran_Infantry_Armor, UpgradeType.Terran_Infantry_Armor.maxRepeats());
		
		return desiredUpgradeTypes;
	}
	
}
