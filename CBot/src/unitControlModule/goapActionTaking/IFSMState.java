package unitControlModule.goapActionTaking;

interface IFSMState {
	// Returning false results in the removing of the implementers instance on
	// the stack of the FSM. True signalizes that the running actions are valid
	// and not finished / obsolete.
	public boolean runGoapAction(GoapUnit goapUnit);
}
