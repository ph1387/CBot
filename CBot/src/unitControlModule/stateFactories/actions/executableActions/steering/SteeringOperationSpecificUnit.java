package unitControlModule.stateFactories.actions.executableActions.steering;

import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Vector;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SteeringOperationSpecificUnit.java --- SteeringOperation which targets a
 * specific Unit on the map.
 * 
 * @author P H - 30.06.2017
 *
 */
public class SteeringOperationSpecificUnit extends BaseSteeringOperation {

	private Unit targetUnit;

	public SteeringOperationSpecificUnit(IGoapUnit goapUnit, Unit targetUnit) {
		super(goapUnit);

		this.targetUnit = targetUnit;
	}

	public SteeringOperationSpecificUnit(IGoapUnit goapUnit) {
		this(goapUnit, null);
	}

	// -------------------- Functions

	@Override
	public void applySteeringForce(Vector targetVector, Double intensity) {
		try {
			Position goapUnitPosition = ((PlayerUnit) this.goapUnit).getUnit().getPosition();
			Vector vecToTargetUnit = new Vector(goapUnitPosition.getX(), goapUnitPosition.getY(),
					this.targetUnit.getPosition().getX() - goapUnitPosition.getX(),
					this.targetUnit.getPosition().getY() - goapUnitPosition.getY());

			// Apply the influence to the targeted Vector.
			if (vecToTargetUnit.length() > 0.) {
				vecToTargetUnit.normalize();
				targetVector.setDirX(targetVector.getDirX() + vecToTargetUnit.getDirX() * intensity);
				targetVector.setDirY(targetVector.getDirY() + vecToTargetUnit.getDirY() * intensity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------------------------------ Getter / Setter

	public void setTargetUnit(Unit targetUnit) {
		this.targetUnit = targetUnit;
	}
}
