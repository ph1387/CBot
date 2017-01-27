package unitControlModule.goapActionTaking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
//TODO: REMOVE
public class OwnUnitWrapper extends GoapUnit {

	public OwnUnitWrapper() {
		// Goals
		GoapState goal1 = new GoapState(1, "inDanger", false);
		GoapState goal2 = new GoapState(2, "attacking", true);

		ArrayList<GoapState> goals = new ArrayList<GoapState>();
		goals.add(goal1);
		goals.add(goal2);

		this.setGoalState(goals);

		// World States
		GoapState world1 = new GoapState(1, "enemyMissing", true);
		GoapState world2 = new GoapState(1, "attacking", false);
		GoapState world3 = new GoapState(1, "bleeding", true);
		GoapState world4 = new GoapState(1, "moving", false);
		GoapState world5 = new GoapState(1, "inDanger", false);

		HashSet<GoapState> worldStates = new HashSet<GoapState>();

		worldStates.add(world1);
		worldStates.add(world2);
		worldStates.add(world3);
		worldStates.add(world4);
		worldStates.add(world5);

		this.setWorldState(worldStates);

		// Actions
		Object target = new Object();
		AttackAction action1 = new AttackAction(target);
		MoveAction action2 = new MoveAction(target);
		HealingAction action3 = new HealingAction(target);
		ScoutingAction action4 = new ScoutingAction(target);

		HashSet<GoapAction> availableActions = new HashSet<>();
		availableActions.add(action1);
		availableActions.add(action2);
		availableActions.add(action3);
		availableActions.add(action4);

		this.setAvailableActions(availableActions);
	}

	@Override
	protected void goapPlanFound(GoapState goal, Queue<GoapAction> actions) {

	}

	@Override
	protected void goapPlanFailed(GoapState goal) {

	}

	@Override
	protected void goapPlanFinished() {

	}

	@Override
	protected void update() {

	}
}
