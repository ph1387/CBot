package core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bwapi.Color;
import bwapi.Game;
import bwapi.Unit;
import bwapi.UnitType;

public class Display {
	public static final int TILESIZE = 32;
	public static final int LINEHEIGHT = 10;
	public static final int OFFSET_LEFT = 10;
	
	// Displays the unit tile ingame
	public static void showUnitTile(Game game, Unit unit, Color color) {
		int posX = unit.getTilePosition().getX();
		int posY = unit.getTilePosition().getY();
		int tileSizeX = 1;
		int tileSizeY = 1;

		// Show building sizes accordingly
		if (unit.getType().isBuilding()) {
			tileSizeX = unit.getType().tileWidth();
			tileSizeY = unit.getType().tileHeight();
		}

		drawTile(game, posX, posY, tileSizeX, tileSizeY, color);
	}

	// Display a box around (a) tile/-s
	public static void drawTile(Game game, int posX, int posY, int tileSizeX, int tileSizeY, Color color) {
		game.drawBoxMap(posX * TILESIZE, posY * TILESIZE, (posX + tileSizeX) * TILESIZE, (posY + tileSizeY) * TILESIZE, color);
	}
	
	// Display a filled tile on the map
	public static void drawTileFilled(Game game, int posX, int posY, int tileSizeX, int tileSizeY, Color color) {
		game.drawBoxMap(posX * TILESIZE, posY * TILESIZE, (posX + tileSizeX) * TILESIZE, (posY + tileSizeY) * TILESIZE, color, true);
	}

	// Display the target position of the unit
	public static void showUnitTarget(Game game, Unit unit, Color color) {
		game.drawLineMap(unit.getPosition(), unit.getTargetPosition(), color);
	}
	
	public static void showUnits(Game game, List<Unit> units) {
		List<String> outputList = new ArrayList<String>();
		List<UnitType> alreadyCountedTypes = new ArrayList<UnitType>();
		
		// Count each unit and display the number on the left side of it
		for (Unit unit : units) {
			// Do not count the buildings and avoid adding the same type more than one time
			if(!unit.getType().isBuilding() && !alreadyCountedTypes.contains(unit.getType())) {
				int unitCounter = 0;
				
				// Iterate through all units and
				for(Unit referenceUnit : units) {
					if(referenceUnit.getType() == unit.getType()) {
						unitCounter++;
					}
				}
				
				// Speficy the output for a symmetric list
				String output = "";
				if(unitCounter < 10) {
					output += "  ";
				} else if(unitCounter < 100) {
					output += " ";
				}
				output += unitCounter + " - " + unit.getType();
				
				alreadyCountedTypes.add(unit.getType());
				outputList.add(output);
			}
		}
		
		showList(game, outputList, OFFSET_LEFT, LINEHEIGHT * 5);
	}

	// Display a list of strings
	public static void showList(Game game, List<String> list, int offsetX, int offsetY) {
		for (int i = 1; i <= list.size(); i++) {
			game.drawTextScreen(offsetX, offsetY + (LINEHEIGHT * i), list.get(i - 1));
		}
	}

	// Wrapper for showing game information
	public static void showGameInformation(Game game) {
		showTime(game, OFFSET_LEFT, LINEHEIGHT);
		showAPM(game, OFFSET_LEFT, LINEHEIGHT * 2);
		showFPS(game, OFFSET_LEFT, LINEHEIGHT * 3);
	}

	// Display the current time
	public static void showTime(Game game, int offsetX, int offsetY) {
		String minutesString = (int) Math.floor(game.elapsedTime() / 60) + "";
		String secondsString = "";
		int seconds = (int) game.elapsedTime() % 60;

		// Leading 0 regarding seconds
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = "" + seconds;
		}

		String text = "Elapsed Time: " + minutesString + ":" + secondsString + " - " + game.elapsedTime();

		game.drawTextScreen(offsetX, offsetY, text);
	}

	// Display the APM counter
	public static void showAPM(Game game, int offsetX, int offsetY) {
		String text = "APM: " + game.getAPM();
		game.drawTextScreen(offsetX, offsetY, text);
	}

	// Display FPS
	public static void showFPS(Game game, int offsetX, int offsetY) {
		String text = "FPS: " + game.getFPS();
		game.drawTextScreen(offsetX, offsetY, text);
	}
}
