package unitControlModule.stateFactories.actions.executableActions.steering;

import java.util.HashSet;

import bwapi.Unit;
import bwapiMath.Vector;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SteeringOperationEnemiesInConfidenceRange.java --- SteeringOperation which
 * targets enemies that are near the Unit and inside its confidence range.
 * 
 * @author P H - 28.06.2017
 *
 */
public class SteeringOperationEnemiesInConfidenceRange extends BaseSteeringOperation {

	public SteeringOperationEnemiesInConfidenceRange(IGoapUnit goapUnit) {
		super(goapUnit);
	}

	// -------------------- Functions

	@Override
	public void applySteeringForce(Vector targetVector, Double intensity) {
		HashSet<Unit> enemiesInConfidenceRange = ((PlayerUnit) goapUnit).getAllEnemyUnitsInConfidenceRange();

		for (Unit unit : enemiesInConfidenceRange) {
			PlayerUnit playerUnit = (PlayerUnit) goapUnit;

			// uPos -> Unit Position, ePos -> Enemy Position
			int uPosX = playerUnit.getUnit().getPosition().getX();
			int uPosY = playerUnit.getUnit().getPosition().getY();
			int ePosX = unit.getPosition().getX();
			int ePosY = unit.getPosition().getY();

			// Generate a Vector starting from the goapUnit itself with the
			// direction Vector from the enemy Unit to the goapUnit applied.
			Vector retreatVectorFromUnit = new Vector(uPosX, uPosY, uPosX - ePosX, uPosY - ePosY);

			// Apply the influence to the targeted Vector.
			if (retreatVectorFromUnit.length() > 0.) {
				retreatVectorFromUnit.normalize();
				targetVector.setDirX(targetVector.getDirX() + retreatVectorFromUnit.getDirX() * intensity);
				targetVector.setDirY(targetVector.getDirY() + retreatVectorFromUnit.getDirY() * intensity);
			}
		}
	}

}
