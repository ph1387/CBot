package unitControlModule.goapActionTaking;

public class GoapState {
	
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
