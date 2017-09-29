package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.Position;
import bwapi.TechType;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * AbilityActionTerranVuture_SpiderMines.java --- The Spider_Mines ability of a
 * Terran_Vulture.
 * 
 * @author P H - 29.09.2017
 *
 */
public class AbilityActionTerranVuture_SpiderMines extends AbilityActionTechTargetPosition {

	private int minPixelDistanceToTarget = 128;
	private int minFrameExecutionWait = 12;
	private int executionStartTimeStamp = 0;
	private boolean performedOnce = false;
	
	/**
	 * @param target
	 *            type: Position
	 */
	public AbilityActionTerranVuture_SpiderMines(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addEffect(new GoapState(0, "retreatFromUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "isAtSpiderMineLocation", true));
		this.addPrecondition(new GoapState(0, "canSpiderMineBePlaced", true));
		this.addPrecondition(new GoapState(0, "shouldSpiderMinesBePlaced", true));
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Spider_Mines;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return this.target != null && ((PlayerUnit) goapUnit).getUnit().getSpiderMineCount() > 0;
	}
	
	// TODO: UML ADD
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;
		
		if(this.actionChangeTrigger) {
			success &= super.performSpecificAction(goapUnit);
			
			this.executionStartTimeStamp = Core.getInstance().getGame().getFrameCount();
			this.performedOnce = true;
		}
		return success;
	}
	
	// TODO: UML ADD
	@Override
	protected void resetSpecific() {
		super.resetSpecific();
		
		this.performedOnce = false;
	}
	
	// TODO: UML ADD
	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		// Wait a certain time before the action is considered done. This is due to the mines taking time to plant.
		boolean timePassed = Core.getInstance().getGame().getFrameCount() - this.executionStartTimeStamp >= this.minFrameExecutionWait;
		
		return this.target == null || (this.performedOnce && timePassed);
	}
	
	// TODO: UML ADD
	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNearPosition((Position) this.target, this.minPixelDistanceToTarget);
	}

	// TODO: UML ADD
	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return true;
	}
}
