package core;

import java.util.HashSet;
import java.util.List;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;

// TODO: UML MOVE FUNCTIONS
/**
 * TilePositionContenderGenerator.java --- Class for generating the default
 * contended construction spots on the map, on which no worker can construct a
 * building.
 * 
 * @author P H - 21.04.2017
 *
 */
public class TilePositionContenderFactory extends TilePositionFactory {

	private int contendedTileRangeBorder = 1;
	private int contendedTileRangeMinerals = 3;
	private int contendedTileRangeGeysers = 3;

	// TODO: UML REMOVE
//	private InformationStorage informationStorage;

	// TODO: UML PARAMS
	public TilePositionContenderFactory() {
		
	}

	// -------------------- Functions

	/**
	 * Function for generating the Default contended TilePositions in the
	 * beginning of the game. These include a x wide path at the map border, y
	 * tiles around all mineral spots and a z wide ring around all gas geysers.
	 * 
	 * @return a HashSet containing all default contended TilePositions.
	 */
	public HashSet<TilePosition> generateDefaultContendedTilePositions() {
		HashSet<TilePosition> defaultContendedTilePositions = new HashSet<TilePosition>();
		List<Unit> startingMinerals = BWTA.getStartLocation(Core.getInstance().getPlayer()).getMinerals();
		List<Unit> startingGeysers = BWTA.getStartLocation(Core.getInstance().getPlayer()).getGeysers();

		// Get all important contended default spots on the map.
		this.contendTilePositionsAroundMinerals(defaultContendedTilePositions, startingMinerals);
		this.contendTilePositionsAroundGeysers(defaultContendedTilePositions, startingGeysers);
		this.contendTilePositionsAtMapEdges(defaultContendedTilePositions);

		// TODO: DEBUG INFO
		System.out.println("Total default contended TilePositions: " + defaultContendedTilePositions.size());

		return defaultContendedTilePositions;
	}

	// TODO: UML VISIBILITIY
	/**
	 * Function for contending all TilePositions in a specific area around each
	 * mineral spot on the map.
	 * 
	 * @param designatedHashSet
	 *            the HashSet in which the TilePositions are going to be stored.
	 * @param excludedUnits
	 *            the Units that are not going to be looked at during the
	 *            iterations.
	 */
	private void contendTilePositionsAroundMinerals(HashSet<TilePosition> designatedHashSet, List<Unit> excludedUnits) {
		// TODO: DEBUG INFO
		System.out.println("Default contended TilePositions excluded mineral spots:");

		for (Unit unit : Core.getInstance().getGame().getMinerals()) {
			if (!excludedUnits.contains(unit)) {
				for (int i = unit.getTilePosition().getX() - this.contendedTileRangeMinerals; i <= unit
						.getTilePosition().getX() + this.contendedTileRangeMinerals; i++) {
					for (int j = unit.getTilePosition().getY() - this.contendedTileRangeMinerals; j <= unit
							.getTilePosition().getY() + this.contendedTileRangeMinerals; j++) {
						TilePosition generatedTilePosition = new TilePosition(i, j);

						if (!designatedHashSet.contains(generatedTilePosition)
								&& Core.getInstance().getGame().getUnitsOnTile(generatedTilePosition).isEmpty()) {
							designatedHashSet.add(generatedTilePosition);
						}
					}
				}
			} else {
				// TODO: DEBUG INFO
				System.out.println("  - " + unit.getTilePosition() + " " + unit);
			}
		}
	}

	// TODO: UML VISIBILITIY
	/**
	 * Function for contending all TilePositions in a specific area around each
	 * geyser on the map.
	 * 
	 * @param designatedHashSet
	 *            the HashSet in which the TilePositions are going to be stored.
	 * @param excludedUnits
	 *            the Units that are not going to be looked at during the
	 *            iterations.
	 */
	private void contendTilePositionsAroundGeysers(HashSet<TilePosition> designatedHashSet, List<Unit> excludedUnits) {
		// TODO: DEBUG INFO
		System.out.println("Default contended TilePositions excluded geysers:");

		for (Unit unit : Core.getInstance().getGame().getGeysers()) {
			if (!excludedUnits.contains(unit)) {
				for (int i = unit.getTilePosition().getX() - this.contendedTileRangeGeysers; i <= unit.getTilePosition()
						.getX() + this.contendedTileRangeGeysers; i++) {
					for (int j = unit.getTilePosition().getY() - this.contendedTileRangeGeysers; j <= unit
							.getTilePosition().getY() + this.contendedTileRangeGeysers; j++) {
						TilePosition generatedTilePosition = new TilePosition(i, j);

						if (!designatedHashSet.contains(generatedTilePosition)
								&& Core.getInstance().getGame().getUnitsOnTile(generatedTilePosition).isEmpty()) {
							designatedHashSet.add(generatedTilePosition);
						}
					}
				}
			} else {
				// TODO: DEBUG INFO
				System.out.println("  - " + unit.getTilePosition() + " " + unit);
			}
		}
	}

	// TODO: UML VISIBILITIY
	/**
	 * Function for contending all TilePositions in a specific area at the map
	 * edge.
	 * 
	 * @param designatedHashSet
	 *            the HashSet in which the TilePositions are going to be stored.
	 */
	private void contendTilePositionsAtMapEdges(HashSet<TilePosition> designatedHashSet) {
		Game game = Core.getInstance().getGame();

		// Width and height are both reduced by 1 since the TilePositions
		// leading to the ends are needed.
		// -> top and bottom
		for (int i = 0; i < this.contendedTileRangeBorder; i++) {
			for (int j = 0; j < game.mapWidth(); j++) {
				TilePosition topTilePosition = new TilePosition(j, i);
				TilePosition bottomTilePosition = new TilePosition(j, game.mapHeight() - 1 - i);

				if (!designatedHashSet.contains(topTilePosition) && game.getUnitsOnTile(topTilePosition).isEmpty()) {
					designatedHashSet.add(topTilePosition);
				}
				if (!designatedHashSet.contains(bottomTilePosition)
						&& game.getUnitsOnTile(bottomTilePosition).isEmpty()) {
					designatedHashSet.add(bottomTilePosition);
				}
			}
		}
		// -> left and right
		for (int i = 0; i < this.contendedTileRangeBorder; i++) {
			for (int j = 0; j < game.mapHeight(); j++) {
				TilePosition leftTilePosition = new TilePosition(i, j);
				TilePosition rightTilePosition = new TilePosition(game.mapWidth() - 1 - i, j);

				if (!designatedHashSet.contains(leftTilePosition) && game.getUnitsOnTile(leftTilePosition).isEmpty()) {
					designatedHashSet.add(leftTilePosition);
				}
				if (!designatedHashSet.contains(rightTilePosition)
						&& game.getUnitsOnTile(rightTilePosition).isEmpty()) {
					designatedHashSet.add(rightTilePosition);
				}
			}
		}
	}
}
