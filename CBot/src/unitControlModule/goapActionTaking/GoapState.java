package unitControlModule.goapActionTaking;

public class GoapState {
	/**
	 * GoapState.java --- States which the GoapActions use to build a graph
	 * @author P H - 28.01.2017
	 */
	
	public Integer importance = 0;
	public String effect;
	public Object value;
	
	public GoapState(Integer importance, String effect, Object value) {
		if(importance == null || importance < 0) {
			importance = 0;
		}
		
		this.importance = importance;
		this.effect = effect;
		this.value = value;
	}
}
