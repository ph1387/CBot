package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UpgradeType;

// TODO: UML ADD
/**
 * ScoringDirectorTerran_Bio.java --- A ScoringDirector whose goal is to score
 * Bio Units and the corresponding upgrades / technologies.
 * 
 * @author P H - 17.07.2017
 *
 */
public class ScoringDirectorTerran_Bio extends ScoringDirector {

	// -------------------- Functions

	@Override
	protected double defineDesiredBuildingsPercent() {
		return 0.33;
	}

	@Override
	protected double defineDesiredCombatUnitsPercent() {
		return 0.50;
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

	@Override
	protected double defineFixedScoreUnitsBio() {
		return 1.0;
	}

	@Override
	protected double defineFixedScoreUnitsFlying() {
		return 0.;
	}

	@Override
	protected double defineFixedScoreUnitsHealer() {
		return 1.0;
	}

	@Override
	protected double defineFixedScoreUnitsSupport() {
		return 1.0;
	}
	
	// ------------------------------ Getter / Setter

}
