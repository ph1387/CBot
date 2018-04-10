package core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.Region;

// TODO: UML ADD
/**
 * BWTAWrapper.java --- Wrapper-Class for some BWTA functions. Used since some
 * of them can return null under certain circumstances. <br>
 * Should be used when the Tile- / Position of a target could be <b>outside</b>
 * of a specific Region's bounds. Targets for which this is not the case (I.e.
 * mineral patches or baselocations) can use the "normal", non-force-find
 * version of this function since the latter only ensures finding a Region.
 * 
 * @author P H - 01.04.2018
 *
 */
public class BWTAWrapper {

	private static final int DEFAULT_STEP_SIZE = Core.getInstance().getTileSize() / 2;

	// -------------------- Functions

	/**
	 * Convenience function.
	 * 
	 * @see BWTAWrapper#getRegion(TilePosition, int)
	 * @param tilePosition
	 *            the TilePosition whose Region is being looked for.
	 * @return the Region of the provided TilePosition instance.
	 */
	public static Region getRegion(TilePosition tilePosition) {
		return getRegion(tilePosition.toPosition(), DEFAULT_STEP_SIZE);
	}

	/**
	 * Convenience function.
	 * 
	 * @see BWTAWrapper#getRegion(TilePosition, int)
	 * @param tilePosition
	 *            the TilePosition whose Region is being looked for.
	 * @param stepSize
	 *            the distance which is being used to determine the Region if
	 *            the used BWTA function returns null and the TilePosition is
	 *            not inside a Region.
	 * @return the Region of the provided TilePosition instance.
	 */
	public static Region getRegion(TilePosition tilePosition, int stepSize) {
		return getRegion(tilePosition.toPosition(), stepSize);
	}

	/**
	 * Convenience function.
	 * 
	 * @see BWTAWrapper#getRegion(TilePosition, int)
	 * @param position
	 *            the Position whose Region is being looked for.
	 * @return the Region of the provided Position instance.
	 */
	public static Region getRegion(Position position) {
		return getRegion(position, DEFAULT_STEP_SIZE);
	}

	/**
	 * Function for finding the Region of a provided Position.
	 * 
	 * @param position
	 *            the Position whose Region is being looked for.
	 * @param stepSize
	 *            the distance which is being used to determine the Region if
	 *            the used BWTA function returns null and the Position is not
	 *            inside a Region.
	 * @return the Region of the provided Position instance.
	 */
	public static Region getRegion(Position position, int stepSize) {
		Region region = BWTA.getRegion(position);

		// Provided Position is NOT inside a Region (I.e. due to TilePosition
		// using the top-left side or a Unit's Position being directly on top of
		// a Region's bounds).
		if (region == null) {
			int maxTries = 1000;

			try {
				region = forceFindRegion(position, maxTries, stepSize);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return region;
	}

	/**
	 * Function for initializing a force search for a Position's Region
	 * instance. This is due Positions sometimes being slightly outside the
	 * BWTA's Region boundaries or directly on top of the borders. This function
	 * performs a flood-fill for Positions until either a Region is found or a
	 * threshold is reached. <br>
	 * <b>Note:</b> </br>
	 * Since the Positions are being changed it is possible for this algorithm
	 * to return a "false" Position when i.e. a Unit is standing directly on top
	 * of a ChokePoint.
	 * 
	 * @param position
	 *            starting Position instance for the flood fill.
	 * @param maxIterations
	 *            the max number of iterations until the search is stopped.
	 * @param stepSize
	 *            the offset that is applied to each Position in every
	 *            iteration. Left, top, right and bottom Positions are generated
	 *            with this value (X and Y +/- value).
	 * @return a found Region instance that a Position inside the flood-fill
	 *         algorithm was in.
	 * @throws Exception
	 *             an Exception is thrown when the maximum number of iterations
	 *             is reached.
	 */
	private static Region forceFindRegion(Position position, int maxIterations, int stepSize) throws Exception {
		HashSet<Position> checkedPositions = new HashSet<>();
		Queue<Position> positionsToCheck = new LinkedList<>();
		Region region = null;
		int counter = 0;

		// Init Queue.
		positionsToCheck.add(position);

		while (counter < maxIterations && region == null) {
			Position currentPosition = positionsToCheck.poll();
			region = BWTA.getRegion(currentPosition);

			if (region == null) {
				HashSet<Position> adjacentPositions = generatePossibleAdjacentPositions(position, checkedPositions,
						stepSize);

				positionsToCheck.addAll(adjacentPositions);
				checkedPositions.add(currentPosition);
				// Added here since another failed iteration would otherwise add
				// the same elements to the Queue.
				checkedPositions.addAll(adjacentPositions);
			}
		}

		// Emergency stop notification.
		if (counter >= maxIterations && region == null) {
			throw new Exception("Max number of iterations reached while searching for a Region!");
		}

		return region;
	}

	/**
	 * Function for generating the Positions that are the direct neighbours of a
	 * provided Position instance. This includes the Positions matching the
	 * following orientations:
	 * <ul>
	 * <li>Left</li>
	 * <li>Top</li>
	 * <li>Right</li>
	 * <li>Bottom</li>
	 * </ul>
	 * The function then checks if the generated Positions are <b>not</b> inside
	 * the provided HashSet containing the checked Positions. Only Positions
	 * matching this criteria are being returned.
	 * 
	 * @param position
	 *            the Position whose neighbours are being checked.
	 * @param checkedPositions
	 *            the HashSet of already checked Positions which a generated
	 *            Position must not be in.
	 * @param stepSize
	 *            the offset that is applied to the X and Y values when
	 *            generating the adjacent Positions.
	 * @return a HashSet containing the neighbour Positions from the provided
	 *         Position instance matching the criteria mentioned above.
	 */
	private static HashSet<Position> generatePossibleAdjacentPositions(Position position,
			HashSet<Position> checkedPositions, int stepSize) {
		HashSet<Position> positions = new HashSet<>();

		int posX = position.getX();
		int posY = position.getY();

		Position posLeft = new Position(posX - stepSize, posY);
		Position posTop = new Position(posX, posY - stepSize);
		Position posRight = new Position(posX + stepSize, posY);
		Position posBottom = new Position(posX, posY + stepSize);

		if (!checkedPositions.contains(posLeft))
			positions.add(posLeft);
		if (!checkedPositions.contains(posTop))
			positions.add(posTop);
		if (!checkedPositions.contains(posRight))
			positions.add(posRight);
		if (!checkedPositions.contains(posBottom))
			positions.add(posBottom);

		return positions;
	}

}
