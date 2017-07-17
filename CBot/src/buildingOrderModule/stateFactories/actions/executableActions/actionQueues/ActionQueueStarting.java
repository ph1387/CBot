package buildingOrderModule.stateFactories.actions.executableActions.actionQueues;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * ActionQueueStarting.java --- Superclass for all ActionQueues that can be
 * executed in the beginning of a match / are starting build orders or openings.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionQueueStarting extends ActionQueueDefault {

	/**
	 * @param target
	 *            type: Irrelevant, because the whole Queue will be cycled
	 *            through.
	 */
	public ActionQueueStarting(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "buildingsNeeded", false));
		this.addEffect(new GoapState(0, "unitsNeeded", false));
		this.addEffect(new GoapState(0, "startingBuildOrderNeeded", false));
		this.addPrecondition(new GoapState(0, "buildingsNeeded", true));
		this.addPrecondition(new GoapState(0, "unitsNeeded", true));
		this.addPrecondition(new GoapState(0, "startingBuildOrderNeeded", true));
	}

	// -------------------- Functions

}
