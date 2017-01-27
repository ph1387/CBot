package unitControlModule.goapActionTaking;
//TODO: REMOVE
class AttackAction extends GoapAction {

	public AttackAction(Object target) {
		super(target);
		
		this.addEffect(1, "attacking", true);
		this.addPrecondition(1, "enemyMissing", false);
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
