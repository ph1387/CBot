package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import core.Core;
import core.Display;
import unitControlModule.Vector;
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

	private Position retreatPosition = null;

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
		return this.target == null || !((PlayerUnit) goapUnit).getAllEnemyUnitsInRange((PlayerUnit.CONFIDENCE_TILE_RADIUS + 1) * Display.TILESIZE).contains(this.target);
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		
		// TODO: DEBUG INFO
		// Executing action.
		Display.drawTileFilled(Core.getInstance().getGame(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getX(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getY(), 1, 1, new Color(255, 255, 0));
		
		return ((PlayerUnit) goapUnit).getUnit().move(this.retreatPosition);
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
		if(this.target != null) {
			double maxDistance = PlayerUnit.CONFIDENCE_TILE_RADIUS * Display.TILESIZE;
			double alphaMax = 120.;
			double alphaAdd = 10.;
			double minMultiplier = 0.1;		// TODO: Possible Change: Increment it
			
			// uPos -> Unit Position, ePos -> Enemy Position
			int uPosX = ((PlayerUnit) goapUnit).getUnit().getPosition().getX();
			int uPosY = ((PlayerUnit) goapUnit).getUnit().getPosition().getY();
			int ePosX = ((Unit) this.target).getPosition().getX();
			int ePosY = ((Unit) this.target).getPosition().getY();
			
			// vecEU -> Vector(enemyUnit, playerUnit)
			Vector vecEU = new Vector(ePosX, ePosY, uPosX - ePosX, uPosY - ePosY);
			double vecMultiplier = (maxDistance - vecEU.length()) / vecEU.length();
			double alphaActual = (alphaMax * vecEU.length() / maxDistance) + alphaAdd;
			
			// vecUTP -> Vector(playerUnit, targetPosition)
			Vector vecUTP = new Vector(uPosX, uPosY, (int) (vecMultiplier * vecEU.dirX), (int) (vecMultiplier * vecEU.dirY));
			
			// Create two vectors that are left and right rotated representations of the vector(playerUnit, targetPosition) by the actual alpha value.
			// vecRotatedL -> Rotated Vector left
			// vecRotatedR -> Rotated Vector right
			Vector vecRotatedL = new Vector(vecUTP.x, vecUTP.y, vecUTP.dirX, vecUTP.dirY);
			Vector vecRotatedR = new Vector(vecUTP.x, vecUTP.y, vecUTP.dirX, vecUTP.dirY);
			vecRotatedL.rotateLeftDEG(alphaActual);
			vecRotatedR.rotateRightDEG(alphaActual);
			
			// Get a possible Unit to which this unit can retreat to. If none is found, move to the previously calculated target Position.
			Unit possibleRetreatUnit = null;
			
			try {
				for (Unit possibleUnit : ((PlayerUnit) goapUnit).getAllPlayerUnitsInRange((int) (maxDistance))) {
					// ruPos -> Retreat Unit Position 
					int ruPosX = possibleUnit.getPosition().getX();
					int ruPosY = possibleUnit.getPosition().getY();
					
					// vecETU -> vector(enemyUnit, targetUnit)
					Vector vecETU = new Vector(ePosX, ePosY, ruPosX - ePosX, ruPosY - ePosY);
					
					// Determine if the targetUnit is at a valid Position. The Unit has to be inside the cone created by the left and right rotated Vectors, which results in the checking of the needed multipliers for the intersection point. The multiplier has to be in a certain range to enable the selection of a retreat Unit.
					Vector intersecL = vecRotatedL.getIntersection(vecETU);
					Vector intersecR = vecRotatedR.getIntersection(vecETU);
					Double intersectionMultiplierL = null, intersectionMultiplierR = null;
					
					if(intersecL != null) {
						intersectionMultiplierL = vecRotatedL.getNeededMultiplier(intersecR);
					}
					if(intersecR != null) {
						intersectionMultiplierR = vecRotatedR.getNeededMultiplier(intersecR);
					}
					
					if((intersectionMultiplierL != null && intersectionMultiplierL > minMultiplier && intersectionMultiplierL < vecMultiplier) || (intersectionMultiplierR != null && intersectionMultiplierR > minMultiplier && intersectionMultiplierR < vecMultiplier)) {
						if(possibleRetreatUnit == null || ((PlayerUnit) goapUnit).getUnit().getDistance(possibleRetreatUnit) < ((PlayerUnit) goapUnit).getUnit().getDistance(possibleUnit)) {
							possibleRetreatUnit = possibleUnit;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(possibleRetreatUnit == null) {
				this.retreatPosition = new Position(vecUTP.x + vecUTP.dirX, vecUTP.y + vecUTP.dirY);
			} else {
				this.retreatPosition = possibleRetreatUnit.getPosition();
			}
			
			
			
			
			// TODO: DEBUG INFO
			// Cone of possible retreat Positions
			Position targetEndPosition = new Position(vecUTP.x + vecUTP.dirX, vecUTP.y + vecUTP.dirY);
			Position rotatedLVecEndPos = new Position(vecRotatedL.x + vecRotatedL.dirX, vecRotatedL.y + vecRotatedL.dirY);
			Position rotatedRVecEndPos = new Position(vecRotatedR.x + vecRotatedR.dirX, vecRotatedR.y + vecRotatedR.dirY);
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), targetEndPosition, new Color(255, 128, 255));
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), rotatedLVecEndPos, new Color(255, 0, 0));
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), rotatedRVecEndPos, new Color(0, 255, 0));
			Core.getInstance().getGame().drawTextMap(rotatedLVecEndPos, String.valueOf(alphaActual));
			Core.getInstance().getGame().drawTextMap(rotatedRVecEndPos, String.valueOf(alphaActual));
//			// Indication which Unit got updated
//			Display.drawTileFilled(Core.getInstance().getGame(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getX(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getY(), 1, 1, new Color(255, 0, 0));
//			// Position to which the Unit retreats to
//			Display.drawTileFilled(Core.getInstance().getGame(), this.retreatPosition.toTilePosition().getX(), this.retreatPosition.toTilePosition().getY(), 1, 1, new Color(255, 255, 0));
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), this.retreatPosition, new Color(255, 255, 0));
			
		
			
		
			return this.retreatPosition != null;
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
