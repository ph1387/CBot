package buildingOrderModule;

import bwapi.UnitType;

public interface DistributeBuildingOrdersEventListener {
	public void onDistributeBuildingOrders(UnitType building);
	
	public void onDistributeUnitBuildingOrders(UnitType unit);
}
