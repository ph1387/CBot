package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import javaGOAP.IGoapUnit;
import unitControlModule.Vector;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_ToOwnGatheringPoint.java --- An action with which a PlayerUnit
 * (!) retreats to a self created gathering point.
 * 
 * @author P H - 10.03.2017
 *
 */
public class RetreatActionToOwnGatheringPoint extends RetreatActionGeneralSuperclass {
	private static final int EXPAND_MULTIPLIER_MAX = 5;
	private static final int TILE_RADIUS_AROUND_UNITS_SEARCH = 1;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionToOwnGatheringPoint(Object target) {
		super(target);
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		Vector vecUTP = ((PlayerUnit) goapUnit).getVecUTP();
		boolean precondtionsMet = true;

		// Position missing -> action not performed yet
		if (this.retreatPosition == null) {
			Unit retreatableUnit = this
					.getUnitWithGreatestTileStrengths(this.getPlayerUnitsInIncreasingRange((PlayerUnit) goapUnit));

			if (retreatableUnit == null) {
				// Walk a minimal distance in the target direction
				precondtionsMet = this.retreatToVectorEndPosition(vecUTP);
			} else {
				this.retreatToUnitPosition(vecUTP, retreatableUnit);
			}
		}
		// Position known -> action performed once
		else {
			precondtionsMet = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
		}
		return precondtionsMet;
	}

	/**
	 * Function with which the temporary retreat Position gets set to the
	 * Vectors end Position.
	 * 
	 * @param vecUTP
	 *            the Vector to the target Position.
	 * @return true or false, depending if the Position is inside the map or
	 *         not.
	 */
	private boolean retreatToVectorEndPosition(Vector vecUTP) {
		Position targetVecPosition = new Position(vecUTP.x + (int) (vecUTP.dirX), vecUTP.y + (int) (vecUTP.dirY));
		boolean returnValue = true;

		if (this.isInsideMap(targetVecPosition)) {
			this.generatedTempRetreatPosition = targetVecPosition;
		} else {
			returnValue = false;
		}

		return returnValue;
	}

	/**
	 * Function with which the temporary retreat Position gets set towards a
	 * Unit. The Vector has a specific length and the direction towards the
	 * Unit.
	 * 
	 * @param vecUTP
	 *            used to determine the starting Position of the Vector towards
	 *            the Unit.
	 * @param retreatableUnit
	 *            the Unit in which direction the Units retreat path will lead.
	 */
	private void retreatToUnitPosition(Vector vecUTP, Unit retreatableUnit) {
		Vector vecToUnit = new Vector(vecUTP.x, vecUTP.y, retreatableUnit.getPosition().getX() - vecUTP.x,
				retreatableUnit.getPosition().getY() - vecUTP.y);
		vecToUnit.normalize();

		Position retreatPosition = new Position(vecToUnit.x + (int) (vecToUnit.dirX * MIN_PIXELDISTANCE_TO_UNIT),
				vecToUnit.y + (int) (vecToUnit.dirY * MIN_PIXELDISTANCE_TO_UNIT));
		this.generatedTempRetreatPosition = retreatPosition;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Base cost has to be increased since the action should only be taken
		// in consideration, if no gathering point from another PlayerUnit is
		// found inside the cone (is another Action).
		return 100;
	}

	/**
	 * Function for retrieving all Units in an increasing range around the given
	 * PlayerUnit. The range at which the Units are searched for increases
	 * stepwise until Units are found or the preset maximum is reached.
	 * 
	 * @param goapUnit
	 *            the PlayerUnit that the search is based around.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit.
	 */
	private HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit goapUnit) {
		HashSet<Unit> unitsTooClose = new HashSet<Unit>();
		HashSet<Unit> unitsInRange = new HashSet<Unit>();
		int iterationCounter = 1;

		// Increase range until a Unit is found or the threshold is reached.
		while (unitsInRange.isEmpty() && iterationCounter <= EXPAND_MULTIPLIER_MAX) {
			HashSet<Unit> foundUnits = goapUnit
					.getAllPlayerUnitsInRange((int) (iterationCounter * MAX_PIXELDISTANCE_TO_UNIT));
			HashSet<Unit> unitsToBeRemoved = new HashSet<Unit>();

			// Test all found Units, a Unit has to have a minimum distance to
			// the PlayerUnit.
			for (Unit unit : foundUnits) {
				if (!unitsTooClose.contains(unit)
						&& goapUnit.getUnit().getDistance(unit.getPosition()) < MIN_PIXELDISTANCE_TO_UNIT) {
					unitsToBeRemoved.add(unit);
					unitsTooClose.add(unit);
				}
			}

			for (Unit unit : unitsToBeRemoved) {
				foundUnits.remove(unit);
			}

			unitsInRange.addAll(foundUnits);
			iterationCounter++;
		}
		return unitsInRange;
	}

	/**
	 * Function for retrieving the Unit with the greatest sum of strengths
	 * around the units TilePosition.
	 * 
	 * @param units
	 *            a HashSet containing all units which are going to be cycled
	 *            through.
	 * @return the Unit with the greatest sum of strengths at its TilePosition.
	 */
	private Unit getUnitWithGreatestTileStrengths(HashSet<Unit> units) {
		Unit bestUnit = null;
		int bestUnitStrengthTotal = 0;

		// Iterate over the Units and over their TilePositions in a specific
		// radius.
		for (Unit unit : units) {
			int currentStrengths = 0;

			for (int i = -TILE_RADIUS_AROUND_UNITS_SEARCH; i <= TILE_RADIUS_AROUND_UNITS_SEARCH; i++) {
				for (int j = -TILE_RADIUS_AROUND_UNITS_SEARCH; j <= TILE_RADIUS_AROUND_UNITS_SEARCH; j++) {

					// TODO: Possible Change: AirStrength Implementation
					Integer value = PlayerUnit.getPlayerGroundAttackTilePositions().get(
							new TilePosition(unit.getTilePosition().getX() + i, unit.getTilePosition().getY() + j));

					if (value != null) {
						currentStrengths += value;
					}
				}
			}

			if (bestUnit == null || currentStrengths > bestUnitStrengthTotal) {
				bestUnit = unit;
				bestUnitStrengthTotal = currentStrengths;
			}
		}
		return bestUnit;
	}
}
