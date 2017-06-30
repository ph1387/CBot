package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Vector;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML COMPLETLY REMOVE!
/**
 * RetreatAction_ToNearestUnit.java --- An action with which a PlayerUnit (!)
 * can retreat to another Unit which is located inside a cone in front of the
 * executing one. The Unit the PlayerUnit is retreating to is chosen each time
 * the action is executed.
 * 
 * @author P H - 03.03.2017
 *
 */
//public class RetreatActionToFurthestUnitInCone extends RetreatActionGeneralSuperclass {
//
//	// vecUTPRotatedL -> Rotated Vector left
//	// vecUTPRotatedR -> Rotated Vector right
//	Vector vecUTPRotatedL, vecUTPRotatedR;
//	protected double alphaMod = 75.;
//	protected double alphaAdd = 10.; // AlphaMod + AlphaAdd < AlphaMax
//
//	/**
//	 * @param target
//	 *            type: Unit
//	 */
//	public RetreatActionToFurthestUnitInCone(Object target) {
//		super(target);
//	}
//
//	// -------------------- Functions
//
//	@Override
//	protected float generateBaseCost(IGoapUnit goapUnit) {
//		return 0;
//	}
//
//	@Override
//	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
//		this.updateVecRotated();
//
//		// Get a possible Unit to which this unit can retreat to.
//		Unit possibleRetreatUnit = this.tryFindingRetreatUnitInCone((PlayerUnit) goapUnit);
//		boolean success = false;
//
//		if (this.retreatPosition != null) {
//			success = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
//		} else if (possibleRetreatUnit != null) {
//			this.generatedTempRetreatPosition = possibleRetreatUnit.getPosition();
//			success = true;
//		}
//
//		// TODO: DEBUG INFO
//		// Cone in front of the Unit
//		bwapi.Unit unit = ((PlayerUnit) goapUnit).getUnit();
//		Position rotatedLVecEndPos = new Position(this.vecUTPRotatedL.getX() + (int) (this.vecUTPRotatedL.getDirX()),
//				this.vecUTPRotatedL.getY() + (int) (this.vecUTPRotatedL.getDirY()));
//		Position rotatedRVecEndPos = new Position(this.vecUTPRotatedR.getX() + (int) (this.vecUTPRotatedR.getDirX()),
//				this.vecUTPRotatedR.getY() + (int) (this.vecUTPRotatedR.getDirY()));
//		Core.getInstance().getGame().drawLineMap(unit.getPosition(), rotatedLVecEndPos, new Color(255, 0, 0));
//		Core.getInstance().getGame().drawLineMap(unit.getPosition(), rotatedRVecEndPos, new Color(0, 255, 0));
//		// Core.getInstance().getGame().drawTextMap(rotatedLVecEndPos,
//		// String.valueOf(alphaActual));
//		// Core.getInstance().getGame().drawTextMap(rotatedRVecEndPos,
//		// String.valueOf(alphaActual));
//
//		return success;
//	}
//
//	/**
//	 * Used for updating all Vectors which are the rotated equivalent to the
//	 * Vector targeting the possible retreat position.
//	 */
//	private void updateVecRotated() {
//		double alphaActual = (this.alphaMod * (this.vecEU.length() / this.maxDistance)) + this.alphaAdd;
//
//		// Create two vectors that are left and right rotated
//		// representations of the vector(playerUnit, targetPosition) by the
//		// actual alpha value.
//		// vecRotatedL -> Rotated Vector left
//		// vecRotatedR -> Rotated Vector right
//		Vector rotatedL = new Vector(this.vecUTP.getX(), this.vecUTP.getY(), this.vecUTP.getDirX(), this.vecUTP.getDirY());
//		Vector rotatedR = new Vector(this.vecUTP.getX(), this.vecUTP.getY(), this.vecUTP.getDirX(), this.vecUTP.getDirY());
//		rotatedL.rotateLeftDEG(alphaActual);
//		rotatedR.rotateRightDEG(alphaActual);
//
//		this.vecUTPRotatedL = rotatedL;
//		this.vecUTPRotatedR = rotatedR;
//	}
//
//	/**
//	 * Function for finding a Unit inside a cone in front of the current
//	 * PlayerUnit that is bound by two Vectors (one rotated left, one rotated
//	 * right). The used Vectors have to extend from the same origin for this
//	 * function to work!
//	 * 
//	 * @param goapUnit
//	 *            the PlayerUnit the Units are searched from.
//	 * @return the Unit located inside the calculated cone with the greatest
//	 *         distance to the given PlayerUnit or null, if none is found.
//	 */
//	private Unit tryFindingRetreatUnitInCone(PlayerUnit goapUnit) {
//		Unit retreatUnit = null;
//
//		try {
//			for (Unit unit : goapUnit.getAllPlayerUnitsInRange((int) (this.vecUTP.length()))) {
//				Vector vecToUnit = new Vector(this.vecUTP.getX(), this.vecUTP.getY(),
//						unit.getPosition().getX() - this.vecUTP.getX(), unit.getPosition().getY() - this.vecUTP.getY());
//				int distanceGoapUnitToUnit = goapUnit.getUnit().getDistance(unit);
//
//				// -> If the Unit is between the left and right rotated Vectors
//				// then the sign of both cross products of the Vectors is
//				// positive.
//				// => (AxB * AxC >= 0 && CxB * CxA >=0) = B is between A and C =
//				// inside the created cone.
//				if (this.vecUTPRotatedL.getCrossProduct(vecToUnit)
//						* this.vecUTPRotatedL.getCrossProduct(this.vecUTPRotatedR) >= 0
//						&& this.vecUTPRotatedR.getCrossProduct(vecToUnit)
//								* this.vecUTPRotatedR.getCrossProduct(this.vecUTPRotatedL) >= 0) {
//					if ((distanceGoapUnitToUnit > MIN_PIXELDISTANCE_TO_UNIT && retreatUnit == null)
//							|| (distanceGoapUnitToUnit > MIN_PIXELDISTANCE_TO_UNIT
//									&& goapUnit.getUnit().getDistance(retreatUnit) < distanceGoapUnitToUnit)) {
//						retreatUnit = unit;
//					}
//				}
//			}
//		} catch (Exception e) {
//			// e.printStackTrace();
//		}
//		return retreatUnit;
//	}
//}
