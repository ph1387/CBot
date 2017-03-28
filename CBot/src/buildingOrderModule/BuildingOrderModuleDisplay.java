package buildingOrderModule;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.commands.BuildCommand;
import buildingOrderModule.commands.Command;
import core.Core;

/**
 * BuildingOrderModuleDisplay.java --- Used for displaying information regarding
 * the building Queue on the screen.
 * 
 * @author P H - 25.03.2017
 *
 */
class BuildingOrderModuleDisplay {

	private static final int POSITION_LEFT = Core.getInstance().getOffsetLeft() * 25;
	private static final int POSITION_TOP = Core.getInstance().getLineheight();
	private static final int MAX_DISPLAY_AMOUNT = 10;

	public static void showCurrentBuildingCommandSender(BuildingCommandManager sender) {
		int stateCounter = sender.getStateCounter();
		List<String> unitsToBuildList = new ArrayList<String>();

		// Transform the commandlist into a displayable string list
		for (Command element : sender.getCommandList()) {
			if (element instanceof BuildCommand) {
				unitsToBuildList.add(((BuildCommand) element).getAssignedUnit().toString());
			}
		}

		// Display all elements ingame
		for (int i = 0; i < unitsToBuildList.size() && i < MAX_DISPLAY_AMOUNT; i++) {
			// Elements already sent are marked with a line after them
			try {
				if (i == 0) {
					Core.getInstance().getGame().drawTextScreen(POSITION_LEFT,
							POSITION_TOP + Core.getInstance().getLineheight() * i,
							unitsToBuildList.get(i + stateCounter) + " <--");
				} else {
					Core.getInstance().getGame().drawTextScreen(POSITION_LEFT,
							POSITION_TOP + Core.getInstance().getLineheight() * i,
							unitsToBuildList.get(i + stateCounter));
				}
			} catch (Exception e) {

			}
		}
	}
}
