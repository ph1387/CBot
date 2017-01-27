package unitControlModule.goapActionTaking;
//TODO: REMOVE
class MoveAction extends GoapAction {

	public MoveAction(Object target) {
		super(target);
		
		this.addEffect(1, "moving", true);
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
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return true;
	}
	
}
