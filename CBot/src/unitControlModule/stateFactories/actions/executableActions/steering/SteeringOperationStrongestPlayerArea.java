package unitControlModule.stateFactories.actions.executableActions.steering;

import bwapi.Unit;
import bwapiMath.Vector;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.RetreatActionGeneralSuperclass;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SteeringOperationStrongestPlayerArea.java --- SteeringOperation which targets
 * the area with the highest Player strength on the map based on the tracker's
 * information.
 * 
 * @author P H - 28.06.2017
 *
 */
public class SteeringOperationStrongestPlayerArea extends BaseSteeringOperation {

	// TODO: UML ADD
	private static final int MINIMUM_SEARCH_RANGE = 200;
	
	public SteeringOperationStrongestPlayerArea(IGoapUnit goapUnit) {
		super(goapUnit);
	}

	// -------------------- Functions

	@Override
	public void applySteeringForce(Vector targetVector, Double intensity) {
		try {
			Unit unitWithStrongestArea = RetreatActionGeneralSuperclass.getUnitWithGreatestTileStrengths(
					RetreatActionGeneralSuperclass.getPlayerUnitsInIncreasingRange((PlayerUnit) goapUnit, MINIMUM_SEARCH_RANGE), goapUnit);
		
			if (unitWithStrongestArea != null && unitWithStrongestArea != ((PlayerUnit) goapUnit).getUnit()) {
				Unit unit = ((PlayerUnit) goapUnit).getUnit();
				Vector vecToStrongestUnitArea = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
						unitWithStrongestArea.getPosition().getX() - unit.getPosition().getX(),
						unitWithStrongestArea.getPosition().getY() - unit.getPosition().getY());
		
				// Apply the influence to the targeted Vector.
				if (vecToStrongestUnitArea.length() > 0.) {
					vecToStrongestUnitArea.normalize();
					targetVector.setDirX(targetVector.getDirX() + vecToStrongestUnitArea.getDirX() * intensity);
					targetVector.setDirY(targetVector.getDirY() + vecToStrongestUnitArea.getDirY() * intensity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
