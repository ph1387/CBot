# CBot
A Terran StarCraft: Brood War bot that uses the Goal Oriented Action Planning (GOAP) AI-system written in Java. 
Creator: P H, ph1387@t-online.de 

---

## Overview
CBot uses the [BWMirror library](https://github.com/vjurenka/BWMirror) as well as a [JavaGOAP library](https://github.com/p1387h/JavaGOAP) which consists of five major parts:

 1. GoapAgent - the main component of the system representing a single agent.
 2. GoapPlanner - generates the final Queue of GoapActions that must be taken in order to fulfill a GoalState.
 3. GoapState - a state which influences either the current state of the Unit or its actions. This can either be a World- or a GoalState depending on the use case of the state itself.
 4. GoapUnit - a wrapper for the Unit that is going to take actions, used by the GoapAgent.
 5. GoapAction - a single action that can be taken by a GoapUnit.
 
A GoapAgent is defined by it surrounding (WorldState) and by the goal that it is trying to achieve (GoalState). Certain actions with preconditions and effects (GoapActions) can be taken to get to a / the GoalState(s). Each action taken influences the current WorldState and therefore the possible actions taken after it. 

The bot is split into five parts:

1. Core - is the starting point of the bot. All general initializations and the connection to the game mirror are stored here
2. InformationStorage - shared information storage that can be accessed by every module
3. UnitControlModule - module for controlling Units in the game
4. UnitTrackerModule - module for tracking the Units on the map
5. BuildingOrderModule - module for generating build orders

Each Unit is wrapped inside a GoapAgent and placed inside the UnitControlModule for further actions. Therefore each Unit acts independently on its own choosing a Queue of actions from the action pool to fulfill certain goals. The WorldState, GoalState as well as the AvailableActions are all based on the UnitType of the acting Unit. A worker Unit has different goals and actions than i.e. a Terran_Siege_Tank.

The building orders are based on a simulation performed inside one of the BuildingOrderModule's action updaters (ActionQueueSimulationResults performs upon the action Queue, ActionUpdaterSimulationQueue generates the action Queue). Each build / training / research / upgrade action is given a certain score based on the current state of the game. These scores are then added together and sums are generated for different action sequences. The one with the highest score is taken and forwarded sequentially to the UnitControlModule.

## Changelog
v1.1 - 25.04.2018

- Made the scout not constantly retreat when finding the enemy
- Added the (reversed) breadth access order for each Region as well as the distances to each other
- Excluded ChokePoints blocked by mineral patches from the breadth access order
- Actions depending on distances between Regions now use the pre-calculated values
- Added smartly moving between ChokePoints in order to prevent Units getting stuck
- Added bunkers and missile turrets to the construction planner
- Fixed Crashes on certain maps due to Region conversion problems
- Improved buildlocation search algorithm

v1.0 - 20.01.2018

- First release

v0.0 - 07.01.2017

- Begin of project

## References
 - [BWMirror](https://github.com/vjurenka/BWMirror)
 - [Javadoc](https://p1387h.github.io/CBot/index.html)
 - [JavaGOAP library](https://github.com/p1387h/JavaGOAP)

## License
MIT [license](https://github.com/p1387h/CBot/blob/master/LICENSE.txt)