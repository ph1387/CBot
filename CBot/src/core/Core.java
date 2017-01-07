package core;

import java.util.ArrayList;
import java.util.List;

import bwapi.*;

public class Core {
	private static Core instance;
	
	private Mirror mirror;
	private Game game;
	private Player player;

	private Core() {
		
	}
	
	// -------------------- Functions
	
	// Singleton function
	public static Core getInstance() {
		if(instance == null) {
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
}	
