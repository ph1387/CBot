package buildingModule;

import bwapi.Game;
import core.Core;

class BuildingModuleDisplay {
	// Display the job of a worker
	public static void showWorkerJob(Game game, WorkerUnit worker) {
		int posX = worker.getUnit().getX();
		int posY = worker.getUnit().getY();
		ConstructionJob constructionJob = worker.getConstructionJob();

		game.drawTextMap(posX, posY, worker.getJob().toString());

		// If the unit has a assigned construction job show the building type
		if (constructionJob != null) {
			game.drawTextMap(posX, posY + Core.getInstance().getLineheight(),
					constructionJob.getBuilding().toString() + " : " + constructionJob.getTilePosition());
		}
	}
}
