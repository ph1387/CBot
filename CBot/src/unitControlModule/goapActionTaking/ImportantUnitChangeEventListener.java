package unitControlModule.goapActionTaking;

interface ImportantUnitChangeEventListener {
	public void onImportantUnitGoalChange(GoapState newGoalState);
	
	public void onImportantUnitStackResetChange();
}
