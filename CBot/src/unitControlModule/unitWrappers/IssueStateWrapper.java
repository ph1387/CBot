package unitControlModule.unitWrappers;

import core.Core;

/**
 * IssueStateWrapper.java --- A wrapper Class for an {@link IssueState}
 * containing functions for signaling an error ({@link #signalIssue()}). Calling
 * this function results in the instance deciding if the issue should be reacted
 * to or not depending on an set period of time that must pass in between two
 * issues.
 * 
 * @author P H - 18.10.2017
 *
 */
public class IssueStateWrapper {

	/**
	 * IssueState.java --- A Enum representing an issue combined with an
	 * additional time stamp.
	 * 
	 * @author P H - 18.10.2017
	 *
	 */
	private enum IssueState {
		NONE, DETECTED;

		private int timeStampFrames = 0;

		public void setTimeStampFrames(int timeStamp) {
			this.timeStampFrames = timeStamp;
		}

		public int getTimeStampFrames() {
			return timeStampFrames;
		}

	};

	private IssueState currentIssueState = IssueState.NONE;
	// The time that must pass before the state resets to NONE.
	private int maxIssueResolveFrames = 240;
	// The time that must pass before the state can be switched from NONE to
	// DETECTED again (after switching to NONE).
	private int issueResolveResetFrames = 240;

	public IssueStateWrapper() {

	}

	// -------------------- Functions

	/**
	 * Function for signaling that an issue exists. This can be an Unit idling
	 * or one being stuck. This function switches the states of an
	 * {@link IssueState} around based on fixed periods of time that must pass
	 * in between multiple calls.
	 * 
	 * @return true if the caller should react to the issue, otherwise false
	 *         (Not enough time has passed).
	 */
	public boolean signalIssue() {
		int frameCount = Core.getInstance().getGame().getFrameCount();
		int framesPassed = frameCount - this.currentIssueState.getTimeStampFrames();
		boolean shouldReact = false;

		if (this.currentIssueState == IssueState.NONE && framesPassed >= this.issueResolveResetFrames) {
			this.currentIssueState.setTimeStampFrames(frameCount);
			this.currentIssueState = IssueState.DETECTED;

			shouldReact = true;
		} else if (this.currentIssueState == IssueState.DETECTED && framesPassed >= this.maxIssueResolveFrames) {
			this.currentIssueState.setTimeStampFrames(frameCount);
			this.currentIssueState = IssueState.NONE;
		}

		return shouldReact;
	}

	// ------------------------------ Getter / Setter

}
