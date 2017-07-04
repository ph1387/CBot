package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Pair;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.Region;
import javaGOAP.IGoapUnit;

/**
 * SteeringVectorGenerator.java --- Interface for all Classes that use some sort
 * of SteeringOperating and therefore are required to generate a Vector which
 * represents all current influences the Unit is experiencing at the moment with
 * different multipliers.
 * 
 * @author P H - 04.07.2017
 *
 */
public interface SteeringVectorGenerator {

	/**
	 * Function for generating a generalized / "normalized" retreat Vector which
	 * consists of all influences the implementing class takes in consideration.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @param regionPolygonPairUnitIsIn
	 *            the Pair of Region and Polygon the Unit is currently in.
	 * @return a Vector that consists of all influences the Unit is currently
	 *         experiencing with different kinds of multipliers.
	 */
	public Vector generateGeneralizedRetreatVector(IGoapUnit goapUnit, Pair<Region, Polygon> regionPolygonPairUnitIsIn);
}
