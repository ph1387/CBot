package unitControlModule.actions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import core.Core;
import core.Display;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatFromNearestUnitAction.java --- An action with which the Unit moves
 * away from another enemy Unit.
 * 
 * @author P H - 12.02.2017
 *
 */
public class RetreatFromNearestUnitAction extends BaseAction {

	// TODO: REMOVE TEST
	private static int ITERATION_MAX = 10;

	private Unit retreatTarget = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatFromNearestUnitAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "retreatFromUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(GoapUnit goapUnit) {
//		return this.retreatTarget == null || (this.retreatTarget != null && ((PlayerUnit) goapUnit).isNear(this.retreatTarget.getTilePosition(), 2));
		
		// TODO: REMOVE TEST
		return this.target == null || !((PlayerUnit) goapUnit).getAllEnemyUnitsInRange((PlayerUnit.CONFIDENCE_TILE_RADIUS + 1) * Display.TILESIZE).contains(this.target);
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
//		int posX = this.retreatTarget.getPosition().getX();
//		int posY = this.retreatTarget.getPosition().getY();
		
		// TODO: REMOVE TEST
		int pPosX = ((PlayerUnit) goapUnit).getUnit().getPosition().getX();
		int pPosY = ((PlayerUnit) goapUnit).getUnit().getPosition().getY();
		int ePosX = ((Unit) this.target).getPosition().getX();
		int ePosY = ((Unit) this.target).getPosition().getY();
		
		// TODO: REMOVE TEST ADDON
		Display.drawTileFilled(Core.getInstance().getGame(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getX(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getY(), 1, 1, new Color(255, 0, 0));
		
		return ((PlayerUnit) goapUnit).getUnit().move(new Position(pPosX + pPosX - ePosX, pPosY + pPosY - ePosY));
	}

	@Override
	protected float generateBaseCost(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
//		HashSet<Unit> unitsAlreadyChecked = new HashSet<Unit>();
//		int iterationCounter = 1;
//		int baseSearchRange = PlayerUnit.CONFIDENCE_TILE_RADIUS * Display.TILESIZE;
//		Integer posX = null;
//		Integer posY = null;
//
//		// Not excluding the unit itself causes exceptions.
//		unitsAlreadyChecked.add(((PlayerUnit) goapUnit).getUnit());
//
//		if (this.target != null) {
//			while (this.retreatTarget == null && iterationCounter <= ITERATION_MAX) {
//				for (Unit unit : ((PlayerUnit) goapUnit).getAllPlayerUnitsInRange(baseSearchRange * iterationCounter)) {
//					if (!unitsAlreadyChecked.contains(unit)) {
//						int unitDistanceTarget = unit.getDistance(((Unit) this.target).getPosition());
//						int goapUnitDistanceTarget = ((PlayerUnit) goapUnit).getUnit()
//								.getDistance(((Unit) this.target).getPosition());
//
//						// Since the unit tries to retreat the distance between
//						// it and the target has to increase.
//						// -> The unit this one is retreating to has to be
//						// further away from the target than either the unit
//						// itself or the possible found retreat target.
//						if ((this.retreatTarget == null && unitDistanceTarget > goapUnitDistanceTarget)
//								|| (this.retreatTarget != null && unitDistanceTarget > this.retreatTarget
//										.getDistance(((Unit) this.target).getPosition()))) {
//							this.retreatTarget = unit;
//						}
//					}
//					unitsAlreadyChecked.add(unit);
//				}
//
//				iterationCounter++;
//			}
//
//			if (this.retreatTarget != null) {
//				posX = this.retreatTarget.getPosition().getX();
//				posY = this.retreatTarget.getPosition().getY();
//			}
//		}
//
//		return (this.target != null && this.retreatTarget != null
//				&& ((PlayerUnit) goapUnit).getUnit().hasPath(new Position(posX, posY)));
		
		// TODO: REMOVE TEST
		if(this.target != null) {
			int pPosX = ((PlayerUnit) goapUnit).getUnit().getPosition().getX();
			int pPosY = ((PlayerUnit) goapUnit).getUnit().getPosition().getY();
			int ePosX = ((Unit) this.target).getPosition().getX();
			int ePosY = ((Unit) this.target).getPosition().getY();
			return this.target != null && ((PlayerUnit) goapUnit).getUnit().hasPath(new Position(pPosX + pPosX - ePosX, pPosY + pPosY - ePosY));
		} else {
			return false;
		}
	}

	@Override
	protected boolean requiresInRange(GoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean isInRange(GoapUnit goapUnit) {
		return false;
	}
}
