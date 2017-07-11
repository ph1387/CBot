package buildingOrderModule.simulator;

import java.util.LinkedList;
import java.util.Queue;

// TODO: UML ADD
/**
 * ActionSequenceFactory.java --- A factory that stores and creates instances of
 * {@link ActionSequence}s. This is necessary since vast amounts of these
 * containers are necessary for performing a simulation and therefore creating
 * the objects of the class is CPU intensive. Therefore this class provides a
 * way of using the same instances multiple times.
 * 
 * @author P H - 06.07.2017
 *
 */
public class ActionSequenceFactory {

	private Queue<ActionSequence> availableSequences = new LinkedList<ActionSequence>();

	public ActionSequenceFactory(int initialSequenceCount) {
		for (int i = 0; i < initialSequenceCount; i++) {
			this.availableSequences.add(this.createNewSequence());
		}
	}

	public ActionSequenceFactory() {

	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	private ActionSequence createNewSequence() {
		return new ActionSequence();
	}

	/**
	 * Function for receiving an available ActionSequence instance.
	 * 
	 * @return an available ActionSequence instance.
	 */
	public ActionSequence receiveSequence() {
		// If there are no available Nodes inside the Queue create new ones.
		if (this.availableSequences.peek() == null) {
			this.availableSequences.add(this.createNewSequence());
		}

		return this.availableSequences.poll();
	}

	/**
	 * Function for returning a ActionSequence instance making it available for
	 * other receivers again.
	 * 
	 * @param sequence
	 *            the ActionSequence that can be used by other classes.
	 */
	public void markNodeAsAvailable(ActionSequence sequence) {
		sequence.setGasCost(0);
		sequence.setMineralCost(0);
		sequence.getUnitsFree().clear();
		sequence.getOccupiedUnitTimes().clear();
		sequence.getActionTypeSequence().clear();

		this.availableSequences.add(sequence);
	}
}
