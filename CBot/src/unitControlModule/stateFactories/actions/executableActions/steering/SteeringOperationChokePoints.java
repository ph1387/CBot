package unitControlModule.stateFactories.actions.executableActions.steering;

import java.util.HashMap;
import java.util.List;

import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import core.BWTAWrapper;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SteeringOperationChokePoints.java --- SteeringOperation which targets
 * ChokePoints on the map.
 * 
 * @author P H - 28.06.2017
 *
 */
public class SteeringOperationChokePoints extends BaseSteeringOperation {

	// Index that is used while steering towards a ChokePoint. The index implies
	// the path TilePosition count to the ChokePoint's center that must be met
	// for using the path with the element at the provided index towards the
	// ChokePoint rather than the general Vector towards the center.
	private static final int CHOKE_POINT_PATH_INDEX = 4;

	// The Region and Polygon the Unit is currently in.
	private Pair<Region, Polygon> polygonPairUnitIsIn;

	public SteeringOperationChokePoints(IGoapUnit goapUnit, Pair<Region, Polygon> polygonPairUnitIsIn) {
		super(goapUnit);

		this.polygonPairUnitIsIn = polygonPairUnitIsIn;
	}

	public SteeringOperationChokePoints(IGoapUnit goapUnit) {
		this(goapUnit, null);
	}

	// -------------------- Functions

	@Override
	public void applySteeringForce(Vector targetVector, Double intensity) {
		try {
			PlayerUnit playerUnit = (PlayerUnit) this.goapUnit;
			HashMap<Region, Region> reversedRegionAccessOrder = playerUnit.getInformationStorage().getMapInfo()
					.getReversedRegionAccessOrder();
			// Wrapper used since the Unit's Position could be outside of a
			// Region.
			Region currentRegion = BWTAWrapper.getRegion(playerUnit.getUnit().getPosition());
			Region regionToFallBackTo = reversedRegionAccessOrder.get(currentRegion);

			// Only change the Vectors direction if the Unit is not currently
			// inside the Player's starting region since this would cause the
			// Unit to uncontrollably circle around the closest ChokePoint.
			if (regionToFallBackTo != null) {
				Chokepoint closestChoke = this.findChokePointToRetreatTo(this.goapUnit, regionToFallBackTo);

				if (closestChoke != null) {
					Unit unit = ((PlayerUnit) goapUnit).getUnit();

					// Get the shortest Path from the Unit to the ChokePoint.
					List<TilePosition> shortestPath = BWTA.getShortestPath(unit.getTilePosition(),
							closestChoke.getCenter().toTilePosition());
					Vector vecUnitToChokePoint = null;

					// Use the first TilePosition as direction for moving the
					// Unit to the ChokePoint. This is necessary since
					// generating a Vector directly towards the ChokePoint would
					// cause the Unit to move uncontrollably in some cases where
					// it is "trapped" in between two sides of the Polygon and
					// therefore moves left, right, left, right, ...
					if (shortestPath.size() > CHOKE_POINT_PATH_INDEX) {
						Position firstStep = shortestPath.get(CHOKE_POINT_PATH_INDEX).toPosition();
						vecUnitToChokePoint = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
								firstStep.getX() - unit.getPosition().getX(),
								firstStep.getY() - unit.getPosition().getY());
					}
					// Generate a Vector leading directly towards the
					// ChokePoint.
					else {
						vecUnitToChokePoint = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
								closestChoke.getCenter().getX() - unit.getPosition().getX(),
								closestChoke.getCenter().getY() - unit.getPosition().getY());
					}

					// Apply the influence to the targeted Vector.
					if (vecUnitToChokePoint.length() > 0.) {
						vecUnitToChokePoint.normalize();
						targetVector.setDirX(targetVector.getDirX() + vecUnitToChokePoint.getDirX() * intensity);
						targetVector.setDirY(targetVector.getDirY() + vecUnitToChokePoint.getDirY() * intensity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for determining the ChokePoint which the Unit must travel to to
	 * get closer towards the Player's starting location.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to retreat.
	 * @param regionToFallBackTo
	 *            the next Region the Unit has to travel to in order to move
	 *            towards the Player's starting location.
	 * @return the ChokePoint from the List of given ChokePoints that leads
	 *         towards the Player's starting location.
	 */
	private Chokepoint findChokePointToRetreatTo(IGoapUnit goapUnit, Region regionToFallBackTo) {
		Chokepoint retreatChokePoint = null;
		Position centerPlayerStartingRegion = BWTA
				.getRegion(BWTA.getStartLocation(Core.getInstance().getPlayer()).getTilePosition()).getCenter();

		for (Chokepoint chokePoint : this.polygonPairUnitIsIn.first.getChokepoints()) {
			if (chokePoint.getRegions().first.equals(regionToFallBackTo)
					|| chokePoint.getRegions().second.equals(regionToFallBackTo)) {
				// The Region might be connected to the other Region by two
				// separate ChokePoints. The closest one towards the Player's
				// starting location is being chosen.
				if (retreatChokePoint == null || retreatChokePoint.getDistance(centerPlayerStartingRegion) > chokePoint
						.getDistance(centerPlayerStartingRegion)) {
					retreatChokePoint = chokePoint;
				}
			}
		}

		return retreatChokePoint;
	}

	// ------------------------------ Getter / Setter

	public void setPolygonPairUnitIsIn(Pair<Region, Polygon> polygonPairUnitIsIn) {
		this.polygonPairUnitIsIn = polygonPairUnitIsIn;
	}
}
