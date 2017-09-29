package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * UnitWorldStateAbilityUsingUnitsTerran_Vulture.java --- WorldState for a
 * Terran_Vulture using its corresponding abilities.
 * 
 * @author P H - 29.09.2017
 *
 */
public class UnitWorldStateAbilityUsingUnitsTerran_Vulture extends UnitWorldStateAbilityUsingUnits {

	public UnitWorldStateAbilityUsingUnitsTerran_Vulture() {
		this.add(new GoapState(0, "isAtSpiderMineLocation", false));
		this.add(new GoapState(0, "canSpiderMineBePlaced", false));
		this.add(new GoapState(0, "shouldSpiderMinesBePlaced", false));
	}

}
