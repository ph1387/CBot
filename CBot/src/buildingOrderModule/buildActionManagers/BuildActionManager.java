package buildingOrderModule.buildActionManagers;

import java.util.Queue;

import buildingOrderModule.CommandSender;
import buildingOrderModule.stateFactories.StateFactory;
import buildingOrderModule.stateFactories.updater.Updater;
import informationStorage.InformationStorage;
import javaGOAP.GoapAction;
import javaGOAP.GoapUnit;

/**
 * BuildActionManager.java --- Superclass for all action managers.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class BuildActionManager extends GoapUnit {

	private CommandSender sender;
	private InformationStorage informationStorage;

	// Factories and Objects needed for an accurate representation of the
	// managers capabilities.
	private StateFactory stateFactory;
	private Updater worldStateUpdater;
	private Updater goalStateUpdater;
	private Updater actionUpdater;

	public BuildActionManager(CommandSender sender, InformationStorage informationStorage) {
		this.sender = sender;
		this.informationStorage = informationStorage;

		this.stateFactory = this.createFactory();
		this.worldStateUpdater = this.stateFactory.getMatchingWorldStateUpdater(this);
		this.goalStateUpdater = this.stateFactory.getMatchingGoalStateUpdater(this);
		this.actionUpdater = this.stateFactory.getMatchingActionUpdater(this);

		this.setWorldState(this.stateFactory.generateWorldState());
		this.setGoalState(this.stateFactory.generateGoalState());
		this.setAvailableActions(this.stateFactory.generateAvailableActions());
	}

	// -------------------- Functions

	@Override
	public void update() {
		try {
			this.worldStateUpdater.update(this);
			this.goalStateUpdater.update(this);
			this.actionUpdater.update(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected abstract StateFactory createFactory();

	@Override
	public void goapPlanFailed(Queue<GoapAction> actionQueue) {

	}

	@Override
	public void goapPlanFinished() {

	}

	@Override
	public void goapPlanFound(Queue<GoapAction> actionQueue) {

	}

	@Override
	public boolean moveTo(Object target) {
		// Can not move, but if this is mistakenly called, it should throw an
		// Exception and continue.
		try {
			throw new Exception("Move to function of the Building Unit called!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

	// ------------------------------ Getter / Setter

	public CommandSender getSender() {
		return this.sender;
	}
	
	public InformationStorage getInformationStorage() {
		return informationStorage;
	}
}
