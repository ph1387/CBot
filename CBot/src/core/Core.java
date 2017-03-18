package core;

import bwapi.*;

public class Core {
	private static Core instance;

	private Mirror mirror;
	private Game game;
	private Player player;
	
	private int tileSize = 32;
	private int lineheight = 10;
	private int offsetLeft = 10;

	private Core() {

	}

	// -------------------- Functions

	/**
	 * Singleton function.
	 * 
	 * @return instance of the class.
	 */
	public static Core getInstance() {
		if (instance == null) {
			instance = new Core();
		}
		return instance;
	}

	// ------------------------------ Getter / Setter

	public void setMirror(Mirror mirror) {
		this.mirror = mirror;
		this.game = this.mirror.getGame();
		this.player = this.game.self();
	}

	public Mirror getMirror() {
		return mirror;
	}

	public Game getGame() {
		return game;
	}

	public Player getPlayer() {
		return player;
	}

	public int getTileSize() {
		return this.tileSize;
	}
	
	public int getLineheight() {
		return lineheight;
	}

	public int getOffsetLeft() {
		return offsetLeft;
	}
}
