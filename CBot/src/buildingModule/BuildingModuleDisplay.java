package buildingModule;

import bwapi.Game;
import display.Display;

class BuildingModuleDisplay {
	// Display the job of a worker
	public static void showWorkerJob(Game game, WorkerUnit worker) {
		int posX = worker.getUnit().getX();
		int posY = worker.getUnit().getY();
		ConstructionJob constructionJob = worker.getConstructionJob();

		game.drawTextMap(posX, posY, worker.getJob().toString());

		// If the unit has a assigned construction job show the building type
		if (constructionJob != null) {
			game.drawTextMap(posX, posY + Display.LINEHEIGHT,
					constructionJob.getBuilding().toString() + " : " + constructionJob.getTilePosition());
		}
	}
}
