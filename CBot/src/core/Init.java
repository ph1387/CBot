package core;

import bwapi.Game;
import bwapi.Mirror;
import bwapi.Pair;
import bwapiMath.Polygon;
import bwta.BWTA;
import bwta.Region;
import informationStorage.InformationStorage;

/**
 * Init.java --- Class used for the initialization of the most important
 * instances.
 * 
 * @author P H - 18.03.2017
 *
 */
public class Init {
	private static final int UNIT_FLAG = 1;
	private static final int GAME_SPEED = 300; // TODO: 20, 0, etc.
	// TODO: UML
	private static final int MAX_POLYGON_EDGE_LENGTH = 50;

	/**
	 * Function for initializing all important Functions in the beginning.
	 * 
	 * @param mirror
	 *            the mirror of the game.
	 * @return true or false depending if the action as successful or not.
	 */
	public static boolean init(Mirror mirror, InformationStorage informationStorage) {
		boolean successful = true;

		try {
			Game game = mirror.getGame();

			Core.getInstance().setMirror(mirror);

			// Use BWTA to analyze map
			BWTA.readMap();
			BWTA.analyze();
			
			// TODO: UML ENABLE
/*
			// Add all default contended TilePositions.
			informationStorage.getMapInfo().getTilePositionContenders()
					.addAll(new TilePositionContenderFactory(CBot.getInstance().getInformationStorage())
							.generateDefaultContendedTilePositions());
*/
			// Add all BWTA-Polygons to the collection of Polygons in the
			// InformationStorage.
			convertBWTAPolygons(informationStorage);

			// Change game settings
			game.enableFlag(UNIT_FLAG);
			game.setLocalSpeed(GAME_SPEED);
		} catch (Exception e) {
			System.out.println("---INIT FAILED---");
			e.printStackTrace();
			successful = false;
		}
		return successful;
	}

	/**
	 * Function for converting the BWTA-Polygon map boundaries into standard
	 * Polygons that can be used for pathfinding etc.
	 * 
	 * @param informationStorage
	 *            the location the newly generated Polygons are stored in.
	 */
	private static void convertBWTAPolygons(InformationStorage informationStorage) {
		for (Region region : BWTA.getRegions()) {
			Polygon regionPolygon = new Polygon(region.getPolygon());
			
			regionPolygon.splitLongEdges(MAX_POLYGON_EDGE_LENGTH);
			informationStorage.getMapInfo().getMapBoundaries().add(new Pair<bwta.Region, Polygon>(region, regionPolygon));
		}
	}
}
