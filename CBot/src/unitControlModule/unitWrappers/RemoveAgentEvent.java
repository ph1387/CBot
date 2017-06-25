package unitControlModule.unitWrappers;

/**
 * RemoveAgentEvent.java --- Event used for removing an Agent from the
 * collection of updatable Agents associated with the given PlayerUnit.
 * 
 * @author P H - 24.06.2017
 *
 */
public interface RemoveAgentEvent {

	public void removeAgent(PlayerUnit sender);
}
