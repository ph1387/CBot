package unitControlModule.scoutCommandManager.commands;

import bwapi.TilePosition;
import bwapi.Unit;
import display.Display;

public class ScoutCommandMove extends ScoutCommand implements Command, CommandGoal {

	private int minTileDistanceToTarget = 5;
	private TilePosition currentTilePosition;
	public TilePosition targetTilePosition;

	public ScoutCommandMove(Unit unit) {
		super(unit);

		this.currentTilePosition = unit.getTilePosition();
	}

	public ScoutCommandMove(Unit unit, TilePosition targetTilePosition) {
		this(unit);

		this.targetTilePosition = targetTilePosition;
	}

	// -------------------- Functions

	@Override
	public void execute() {
		this.updateTilePosition();
		
		if (this.targetTilePosition != null) {
			try {
				unit.move(this.targetTilePosition.toPosition());
			} catch (Exception e) {
				System.out.println(
						"---UNIT: " + this.unit.getType() + "can't reach position " + this.targetTilePosition + "---");
			}
		}
	}

	@Override
	public boolean commandGoalReached() {
		// If the scout is near the target location, return true
		if (this.currentTilePosition.getDistance(this.targetTilePosition) < this.minTileDistanceToTarget) {
			return true;
		} else {
			return false;
		}
	}
	
	private void updateTilePosition() {
		this.currentTilePosition = this.unit.getTilePosition();
	}

	// ------------------------------ Getter / Setter

	public TilePosition getCurrentLocation() {
		return this.currentTilePosition;
	}
}
