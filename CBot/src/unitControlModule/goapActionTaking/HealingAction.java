package unitControlModule.goapActionTaking;

// TODO: REMOVE
class HealingAction extends GoapAction {

	public HealingAction(Object target) {
		super(target);
		
		this.addEffect(1, "inDanger", false);
		this.addEffect(1, "bleeding", false);
		this.addPrecondition(1, "bleeding", true);
		this.addPrecondition(1, "moving", false);;
	}

	@Override
	protected void reset() {
		
	}

	@Override
	protected boolean isDone() {
		return false;
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		return false;
	}

	@Override
	protected float generateCost(GoapUnit goapUnit) {
		return 5;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return true;
	}
	
}
