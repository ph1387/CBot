package unitControlModule.stateFactories.actions.executableActions.steering;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Vector;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SteeringOperationStartingLocation.java --- SteeringOperation which targets
 * the Player's starting location.
 * 
 * @author P H - 28.06.2017
 *
 */
public class SteeringOperationStartingLocation extends BaseSteeringOperation {

	public SteeringOperationStartingLocation(IGoapUnit goapUnit) {
		super(goapUnit);
	}

	// -------------------- Functions

	@Override
	public void applySteeringForce(Vector targetVector, Double intensity) {
		TilePosition playerStartingLocation = Core.getInstance().getPlayer().getStartLocation();

		if (playerStartingLocation != null) {
			Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Vector vecToBaseLocation = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
					playerStartingLocation.toPosition().getX() - unit.getPosition().getX(),
					playerStartingLocation.toPosition().getY() - unit.getPosition().getY());

			// Apply the influence to the targeted Vector.
			if (vecToBaseLocation.length() > 0.) {
				vecToBaseLocation.normalize();
				targetVector.setDirX(targetVector.getDirX() + vecToBaseLocation.getDirX() * intensity);
				targetVector.setDirY(targetVector.getDirY() + vecToBaseLocation.getDirY() * intensity);
			}
		}
	}
}
