package unitControlModule.goapActionTaking;

/**
 * GoapState.java --- States which the GoapActions use to build a graph
 * 
 * @author P H - 28.01.2017
 */
public class GoapState {

	public Integer importance = 0;
	public String effect;
	public Object value;

	/**
	 * @param importance
	 *            the importance of the state being reached Only necessary if
	 *            the state is used to define a worldState. Has no effect in
	 *            Actions being taken.
	 * @param effect
	 *            the effect the state has.
	 * @param value
	 *            the value of the effect. Since "Object" is being used this is
	 *            NOT type safe!
	 */
	public GoapState(Integer importance, String effect, Object value) {
		if (importance == null || importance < 0) {
			importance = 0;
		}

		this.importance = importance;
		this.effect = effect;
		this.value = value;
	}
}
