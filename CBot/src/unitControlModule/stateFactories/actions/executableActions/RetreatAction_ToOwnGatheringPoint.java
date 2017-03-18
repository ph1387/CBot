package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import core.Core;
import core.Display;
import javaGOAP.IGoapUnit;
import unitControlModule.Vector;
import unitControlModule.unitWrappers.PlayerUnit;
import unitTrackerModule.UnitTrackerModule;

// TODO: UML
/**
 * RetreatAction_ToOwnGatheringPoint.java --- An action with which a PlayerUnit
 * (!) retreats to a self created gathering point.
 * 
 * @author P H - 10.03.2017
 *
 */
public class RetreatAction_ToOwnGatheringPoint extends RetreatAction_GeneralSuperclass {

	private static final int DIST_TO_GATHERING_POINT = Core.getInstance().getTileSize();
	private static final int EXPAND_MULTIPLIER_MAX = 5;
	private static final int TILE_RADIUS_AROUND_UNITS_SEARCH = 1;
	private static final int MIN_PIXELDISTANCE_TO_UNIT = 100;
	private static final int MAX_PIXELDISTANCE_TO_UNIT = 10 * Core.getInstance().getTileSize();

	private Position generatedTempRetreatPosition = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatAction_ToOwnGatheringPoint(Object target) {
		super(target);
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {

		// TODO: DEBUG INFO
		// Executing action.
		Display.drawTileFilled(Core.getInstance().getGame(), ((PlayerUnit) goapUnit).getUnit().getTilePosition().getX(),
				((PlayerUnit) goapUnit).getUnit().getTilePosition().getY(), 1, 1, new Color(255, 255, 0));

		if (this.retreatPosition == null
				|| ((PlayerUnit) goapUnit).isNearPosition(this.retreatPosition, DIST_TO_GATHERING_POINT)) {
			RetreatAction_GeneralSuperclass.gatheringPoints.remove(this.retreatPosition);
		}

		return !RetreatAction_GeneralSuperclass.gatheringPoints.contains(this.retreatPosition);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Only override the current retreatPosition if the action trigger is
		// set and move towards it. This enables the ability of storing the
		// Positions inside a HashSet and moving other Units towards them
		// instead of constantly updating them, which would result in a Unit
		// only following them in one general direction. Once a Position is set,
		// stick with it.
		if (this.actionChangeTrigger && this.generatedTempRetreatPosition != null) {
			this.retreatPosition = this.generatedTempRetreatPosition;
			RetreatAction_GeneralSuperclass.gatheringPoints.add(this.retreatPosition);
			success &= this.retreatPosition != null && ((PlayerUnit) goapUnit).getUnit().move(this.retreatPosition);
		} else if (this.actionChangeTrigger && this.generatedTempRetreatPosition == null) {
			success = false;
		}
		
		if(this.retreatPosition != null) {
			// TODO: DEBUG INFO
			// // Position to which the Unit retreats to
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), this.retreatPosition,
					new Color(255, 255, 0));
		}
		
		return success;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		Vector vecUTP = ((PlayerUnit) goapUnit).getVecUTP();
		boolean precondtionsMet = true;
		boolean noRetreatUnitFound = false;

		Unit retreatableUnit = this
				.getUnitWithGreatestTileStrengths(this.getPlayerUnitsInIncreasingRange((PlayerUnit) goapUnit));

		if (retreatableUnit == null) {
			noRetreatUnitFound = true;
		} else {
			// Generate a Vector to found Unit that has a fixed length to the
			// Units Position.
			Vector vecToUnit = new Vector(vecUTP.x, vecUTP.y, retreatableUnit.getPosition().getX() - vecUTP.x,
					retreatableUnit.getPosition().getY() - vecUTP.y);
			vecToUnit.normalize();

			Position retreatPosition = new Position(vecUTP.x + (vecToUnit.dirX * EXPAND_MULTIPLIER_MAX),
					vecUTP.y + (vecToUnit.dirY * EXPAND_MULTIPLIER_MAX));
			this.generatedTempRetreatPosition = retreatPosition;
		}

		// Retreat to the Vector Position.
		if (noRetreatUnitFound) {
			Position targetVecPosition = new Position(vecUTP.x + vecUTP.dirX, vecUTP.y + vecUTP.dirY);

			if (this.isInsideMap(targetVecPosition)) {
				this.retreatPosition = targetVecPosition;
			} else {
				precondtionsMet = false;
			}
		}

		return precondtionsMet;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Base cost has to be increased since the action should only be taken
		// in consideration, if no gathering point from another PlayerUnit is
		// found inside the cone (is another Action).
		return 100;
	}

	// TODO: UML
	/**
	 * Function for testing if a Position is inside the map.
	 * 
	 * @param p
	 *            the Position that is going to be checked.
	 * @return true or false depending if the Position is inside the map or not.
	 */
	private boolean isInsideMap(Position p) {
		Game game = Core.getInstance().getGame();

		return (p.getX() < (game.mapWidth() * Core.getInstance().getTileSize()) || p.getX() >= 0
				|| p.getY() < (game.mapHeight() * Core.getInstance().getTileSize()) || p.getY() >= 0);
	}
	
	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		float returnValue = 0.f;
		
		try {
			returnValue = ((PlayerUnit) goapUnit).getUnit().getDistance(this.generatedTempRetreatPosition);
		} catch (Exception e) {
			returnValue = Float.MAX_VALUE;
		}
		
		return returnValue;
	}
	
	@Override
	protected void reset() {
		
	}

	// TODO: UML
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

	// TODO: UML
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
					Integer value = UnitTrackerModule.getInstance().getPlayerGroundAttackTilePositions().get(
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
