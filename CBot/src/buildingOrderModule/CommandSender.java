package buildingOrderModule;

import bwapi.UnitType;

public interface CommandSender {

	public void buildUnit(UnitType unitType);
	
	public void buildBuilding(UnitType unitType);
}
