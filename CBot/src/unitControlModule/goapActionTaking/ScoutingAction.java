package unitControlModule.goapActionTaking;

// TODO: REMOVE
public class ScoutingAction extends GoapAction {

	public ScoutingAction(Object target) {
		super(target);
		
		this.addEffect(1, "enemyMissing", false);
		this.addPrecondition(1, "moving", true);
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
		return 2;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return true;
	}

}
