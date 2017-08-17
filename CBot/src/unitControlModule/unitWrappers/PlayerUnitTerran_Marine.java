package unitControlModule.unitWrappers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

import bwapi.Color;
import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;
import bwapiMath.Point;
import bwapiMath.Vector;
import informationStorage.InformationStorage;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.StateFactoryTerran_Marine;

/**
 * PlayerUnit_Marine.java --- Terran_Marine Class.
 * 
 * @author P H - 26.02.2017
 *
 */
public class PlayerUnitTerran_Marine extends PlayerUnitTypeRanged {

	// The value that will be added towards the medic multiplier for each medic
	// found.
	private double additionalMedicMultiplierValue = 0.5;

	public PlayerUnitTerran_Marine(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		this.confidenceDefault = 0.5;
		this.extraConfidencePixelRangeToClosestUnits = 40;
	}

	// -------------------- Functions

	@Override
	protected StateFactory createFactory() {
		return new StateFactoryTerran_Marine();
	}

	/**
	 * Overridden since Marines use an ability called StimPack, which
	 * effectively reduces their health but significantly improves their
	 * movement. All changes made in the Superclass must be implemented here as
	 * well.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnitTypeRanged#generateConfidence()
	 */
	@Override
	protected double generateConfidence() {
		double generatedConfidence = 0.;
		Pair<Double, Double> playerEnemyStrengths = this.generatePlayerAndEnemyStrengths();
		double playerStrengthTotal = playerEnemyStrengths.first;
		double enemyStrengthTotal = playerEnemyStrengths.second;
		int lifeAddtionStimEffect = 0;

		// Custom addition to the default implementation since being stimmed
		// decreases the health but increases the possible damage output.
		if (this.unit.isStimmed()) {
			lifeAddtionStimEffect = 10;
		}

		// TODO: Possible Change: Change the way the life offset is calculated.
		// Calculate the offset of the confidence based on the current Units
		// health.
		double lifeConfidenceMultiplicator = (double) (this.unit.getHitPoints() + lifeAddtionStimEffect)
				/ (double) (this.unit.getType().maxHitPoints());

		// Generate the multiplier based on each medic in the area.
		double medicMultiplier = this.generateMedicMultiplier();

		// Has to be set for following equation
		if (enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}

		// TODO: Possible Change: AirWeapon Implementation
		// Allow kiting if the PlayerUnit is outside of the other Unit's attack
		// range. Also this allows Units to further attack and not running
		// around aimlessly when they are on low health.
		// -> PlayerUnit in range of enemy Unit + extra
		if (this.closestEnemyUnitInConfidenceRange != null
				&& this.closestEnemyUnitInConfidenceRange.getType().groundWeapon().maxRange()
						+ this.extraConfidencePixelRangeToClosestUnits >= this.getUnit()
								.getDistance(this.closestEnemyUnitInConfidenceRange)) {
			generatedConfidence = (playerStrengthTotal / enemyStrengthTotal) * lifeConfidenceMultiplicator
					* medicMultiplier * this.confidenceDefault;
		}
		// -> PlayerUnit out of range of the enemy Unit
		else {
			generatedConfidence = (playerStrengthTotal / enemyStrengthTotal) * this.confidenceDefault;
		}

		return generatedConfidence;
	}

	/**
	 * Function for generating a multiplier based on Terran_Medic Units found in
	 * an area around the Unit.
	 * 
	 * @return a multiplier for each medic in the area around the Unit.
	 */
	private double generateMedicMultiplier() {
		double medicMultiplier = 1.;

		for (Unit unit : this.getAllPlayerUnitsInConfidenceRange()) {
			if (unit.getType() == UnitType.Terran_Medic) {
				medicMultiplier += this.additionalMedicMultiplierValue;
			}
		}

		return medicMultiplier;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// TODO: UML ADD + JAVADOCS FF!
	
	
	
	// TODO: WIP REMOVE
	@Override
	public void update() {
		super.update();
		
		this.customFunc();
	}
	
	// TODO: WIP REMOVE
	// Class for testing the implementation.
	class TestUnit implements IRetreatUnit {

		Point position;
		
		public TestUnit(int x, int y) {
			this(new Point(x, y, Point.Type.POSITION));
		}
		
		public TestUnit(Point position) {
			this.position = position;
		}
		
		@Override
		public Point defineCurrentPosition() {
			return this.position;
		}
		
	}
	
	// TODO: WIP REMOVE
	//Graph and factories that are going to be used.
	private EndpointFactory factory = new EndpointFactory();
	private EndpointGraph endpointGraph = new EndpointGraph();
	
	// Storage classes.
	private HashSet<IRetreatUnit> groupMembers = new HashSet<>();
	private HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints = new HashMap<>();
	
	private boolean notDefined = true;
	private TestUnit leader;
	
	// TODO: WIP REMOVE
	private void customFunc() {
		if(this.notDefined) {
			this.defineContent();
			
			this.notDefined = false;
		}
		
		// Display the initial information of the leader.
		this.displayLeader(leader, mappedUnitsToEndpoints);
		
		// Display the initial information of each Unit.
		for (IRetreatUnit iRetreatUnit : groupMembers) {
			if(iRetreatUnit != leader) {
				this.displayMember(iRetreatUnit, leader);
			}
		}
		
		// Display all connections in the graph.
		this.displayGraph(groupMembers, endpointGraph, mappedUnitsToEndpoints);
		
	}
	
	private void defineContent() {
		// Define the leader:
		TestUnit leader = this.generateLeader(groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		this.leader = leader;
		
//		this.defineBaseTestContent();
		this.defineInsertionTestContent();
	}
	
	private void defineBaseTestContent() {
		IRetreatUnit topMember = this.addNewMember(0, -100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit leftMember = this.addNewMember(-100, 0, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit bottomMember = this.addNewMember(0, 100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit rightMember = this.addNewMember(100, 0, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		
		IRetreatUnit topLeftMember = this.addNewMember(-100, -100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit topRighttMember = this.addNewMember(100, -100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit bottomLeftMember = this.addNewMember(-100, 100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit bottomRightMember = this.addNewMember(100, 100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
	}
	
	private void defineInsertionTestContent() {
		IRetreatUnit topMember = this.addNewMember(0, -100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		IRetreatUnit leftMember = this.addNewMember(-100, 0, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		
		IRetreatUnit topLeftMember = this.addNewMember(-100, -100, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
		
		IRetreatUnit topLeftInsertionMember = this.addNewMember(-70, -70, leader, groupMembers, factory, endpointGraph, mappedUnitsToEndpoints);
	}
	
	private TestUnit generateLeader(HashSet<IRetreatUnit> groupMembers, EndpointFactory factory, EndpointGraph endpointGraph, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
		// This unit as testing unit.
		TestUnit leader = new TestUnit(new Point(this.unit.getPosition()));
		Point leaderRetreatPoint = new Point(leader.position.getX() + 100, leader.position.getY(), Point.Type.POSITION);
		
		// Add the leader to the different storage classes.
		groupMembers.add(leader);
		Endpoint leaderEndpoint = factory.addLeaderToGraph(endpointGraph, leaderRetreatPoint);
		mappedUnitsToEndpoints.put(leader, leaderEndpoint);
		
		return leader;
	}
	
	private void displayLeader(IRetreatUnit leader, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
		Vector vecLeaderRetreatPoint = new Vector(leader.defineCurrentPosition(), mappedUnitsToEndpoints.get(leader).point);
		
		// Display the leader info.
		mappedUnitsToEndpoints.get(leader).point.display(new Color(255,0,0), false);
		vecLeaderRetreatPoint.display(new Color(0,0,255));
	}
	
	private IRetreatUnit addNewMember(int offsetX, int offsetY, TestUnit leader, HashSet<IRetreatUnit> groupMembers, EndpointFactory factory, EndpointGraph endpointGraph, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
		TestUnit newMember = new TestUnit(leader.position.getX() + offsetX, leader.position.getY() + offsetY);
		Endpoint newMemberEndpoint = factory.addMemberToGraph(endpointGraph, newMember, groupMembers, mappedUnitsToEndpoints);
		groupMembers.add(newMember);
		mappedUnitsToEndpoints.put(newMember, newMemberEndpoint);
		
		return newMember;
	}
	
	private void displayMember(IRetreatUnit member, IRetreatUnit leader) {
		// Display the current Position of the member.
		// Initial:
		member.defineCurrentPosition().display(new Color(0,255,0), false);
		(new Vector(member.defineCurrentPosition(), leader.defineCurrentPosition())).display(new Color(0,255,0));
	}
	
	private void displayGraph(HashSet<IRetreatUnit> groupMembers, EndpointGraph endpointGraph, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
		for (IRetreatUnit iRetreatUnit : groupMembers) {
			for (Endpoint endpoint : endpointGraph.getNodes().get(mappedUnitsToEndpoints.get(iRetreatUnit))) {
				(new Vector(endpoint.point, mappedUnitsToEndpoints.get(iRetreatUnit).point)).display(new Color(255,255,0), false);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public interface IRetreatUnit {
		public Point defineCurrentPosition();
	}
	
	private class EndpointFactory {
		
		public Endpoint addLeaderToGraph(EndpointGraph graph, Point endPosition) {
			return this.addToGraph(graph, null, endPosition, null, null);
		}
		
		public Endpoint addMemberToGraph(EndpointGraph graph, IRetreatUnit goapUnit, HashSet<IRetreatUnit> groupMembers, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
			return this.addToGraph(graph, goapUnit, null, groupMembers, mappedUnitsToEndpoints);
		}
		
		public Endpoint addToGraph(EndpointGraph graph, IRetreatUnit goapUnit, Point endPosition, HashSet<IRetreatUnit> groupMembers, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
			Endpoint generatedEndpoint = null;
			
			try {
				// If the graph is empty add the node using the leader end-position.
				if(graph.getNodes().isEmpty() && endPosition != null) {
					Pair<Endpoint, HashSet<Endpoint>> endPositionInfo = this.generateLeaderEndpoint(graph, endPosition);
					graph.addEndpoint(endPositionInfo.first, endPositionInfo.second);
					generatedEndpoint = endPositionInfo.first;
				} 
				// Otherwise there must be nodes inside the graph with which new ones can be created.
				else if(!graph.getNodes().isEmpty()) {
					Pair<Endpoint, HashSet<Endpoint>> endPositionInfo = this.generateEndpoint(graph, groupMembers, goapUnit, mappedUnitsToEndpoints);
					graph.addEndpoint(endPositionInfo.first, endPositionInfo.second);
					generatedEndpoint = endPositionInfo.first;
				} 
				// Finally throw an Exception since either one of these cases must apply.
				else {
					throw new Exception();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return generatedEndpoint;
		}
		
		private Pair<Endpoint, HashSet<Endpoint>> generateLeaderEndpoint(EndpointGraph graph, Point endPosition) {
			return new Pair<Endpoint, HashSet<Endpoint>>(new Endpoint(graph, endPosition), new HashSet<Endpoint>());
		}
		
		private Pair<Endpoint, HashSet<Endpoint>> generateEndpoint(EndpointGraph graph, HashSet<IRetreatUnit> groupMembers, IRetreatUnit currentGroupUnit, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
			// Get the references to all Units which are possible connection partners in the graphs. This includes the connection to them not being crossed by another one in the group.
			HashSet<IRetreatUnit> possibleConnectionPartners = this.extractPossibleConnections(graph, groupMembers, currentGroupUnit, mappedUnitsToEndpoints);
			
			// Get the mapped Endpoints of the Units and add them to the collection of ones returned by this factory.
			HashSet<Endpoint> mappedConnections = this.generateMappedConnections(possibleConnectionPartners, mappedUnitsToEndpoints);
			
			// Select a random Unit as base for the Endpoint generation. A Vector from this selected Unit will be cast towards the current group Unit and then set to a length generating the initial starting Position later on.
			IRetreatUnit initalStartingLocationUnit = possibleConnectionPartners.iterator().next();
			
			return new Pair<Endpoint, HashSet<Endpoint>>(new Endpoint(graph, mappedUnitsToEndpoints, initalStartingLocationUnit, currentGroupUnit), mappedConnections);
		}
		
		private HashSet<IRetreatUnit> extractPossibleConnections(EndpointGraph graph, HashSet<IRetreatUnit> groupMembers, IRetreatUnit currentGroupUnit, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
			// Iterate through all other group members and find the ones that can be connected with the current Unit without crossing another Vector. The graph is not considered since he only represents the desired end-result.
			HashSet<IRetreatUnit> possibleConnections = new HashSet<>();
			
			
			
			
			
			// TODO: WIP OWN FUNCTION
			// The previously mapped Units to Endpoints are swapped around since recovering them this way is more efficient than searching all the values each time a member must be converted to an Endpoint. This way they must be once swapped (O(N)) and can then be retrieved more easily (O(1)).
			final HashMap<Endpoint, IRetreatUnit> mappedEndpointsToUnits = new HashMap<>();
			
			mappedUnitsToEndpoints.forEach(new BiConsumer<IRetreatUnit, Endpoint>() {

				@Override
				public void accept(IRetreatUnit iRetreatUnit, Endpoint endpoint) {
					mappedEndpointsToUnits.put(endpoint, iRetreatUnit);
				}
			});
			
			
			
			
			
			
			
			// Each member of the graph is being checked if it can be connected with the current Unit.
			for (IRetreatUnit targetMember : groupMembers) {
				// Do NOT create a Vector from the Unit to itself.
				if(targetMember != currentGroupUnit) {
					boolean isPossibleConnection = true;
					
					// The Vector to the member itself emerging from the current Unit.
					Vector vecToMember = new Vector(currentGroupUnit.defineCurrentPosition(), targetMember.defineCurrentPosition());
					
					// TODO: WIP OWN FUNCTION
					// Now test if the Vector intersects another connection of the Units.
					// NOTE:
					// Only Vector lengths between 0-1. matter since the line itself is being tested.
					for (IRetreatUnit firstTestMember : groupMembers) {
						// The Endpoints of this test member. These are then converted back to the Units for retrieving their current Position. This is necessary since only the members must be checked which are connected with the test Unit in the graph itself. any other method would result in the graph being falsely generated since Endpoints inserted in between a cluster of Nodes would not connect to any one due to all the misleading "connections" that are not really present.
						for(Endpoint secondTestEndpoint : graph.getNodes().get(mappedUnitsToEndpoints.get(firstTestMember))) {
							IRetreatUnit secondTestMember = mappedEndpointsToUnits.get(secondTestEndpoint);
						
							// A member can only be connected to a different one.
							if(firstTestMember != secondTestMember) {
								// The Vector from the first Unit to the currently chosen other Unit of the group.
								Vector vecBetweenTestMembers = new Vector(firstTestMember.defineCurrentPosition(), secondTestMember.defineCurrentPosition());
								Point intersection = vecToMember.getIntersection(vecBetweenTestMembers);
								
								if(intersection != null) {
									Double vecToMemberMultiplier = vecToMember.getNeededMultiplier(intersection);
									Double vecBetweenMembersMultiplier = vecBetweenTestMembers.getNeededMultiplier(intersection);
									
									// Both Vectors must be crossing each other with a positive multiplier. If one of them has a negative one then the intersection is behind it's starting point and therefore must not considered. This is especially important due to the later use of Math.max which in return can cause problems when comparing these values (The multiplier leading behind the starting Point will probably be ignored!). Therefore this test is necessary.  
									if(vecToMemberMultiplier >= 0. && vecBetweenMembersMultiplier >= 0) {
										// Both Vectors are tested and their multipliers compared since in some cases the intersection is the start of the Vector leading towards the member itself while the other one is pointing directly at it in a straight line. In this case the former has the multiplier of 0. while the latter might have one with the value of 2.. If the values were not compared the connection would have not been made.
										Double neededMultiplier = Math.max(vecToMemberMultiplier, vecBetweenMembersMultiplier);
										
										// TODO: WIP Possible Error: since 1.0 is the beginning of the other Vector there might be an error! Maybe change the length multipliers to 0.98-0.99?
										// If the intersection requires a compared (Math.max) multiplier between 0. and 1. the connection can not be made.
										if(neededMultiplier >= 0. && neededMultiplier < 1.) {
											isPossibleConnection = false;
											
											break;
										}
									}
								}
							}
						}
						
						// Break the loop if the connection is not possible anymore.
						// -> No need to further iterate through the collection since the connection already crossed another Vector.
						if(!isPossibleConnection) {
							break;
						}
					}
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					// TODO: WIP OWN FUNCTION
					// TODO: WIP OR
					// TODO: WIP PLACE INSIDE THE OTHER FUNCTION
					// Test if the Vector crosses any Endpoints since this would cause the generated Endpoint Vector to point at (0, 0) when combined with this particular Point.
					for (IRetreatUnit testMember : groupMembers) {
						// Only test Endpoints that are not the target!
						if(testMember != targetMember) {
							Double secondMemberMultiplier = vecToMember.getNeededMultiplier(testMember.defineCurrentPosition());
							
							// Both tests have to be < and > since the exact Endpoints must not be removed! 
							if(secondMemberMultiplier != null && secondMemberMultiplier > 0. && secondMemberMultiplier < 1.) {
								isPossibleConnection = false;

								break;
							}
						}
					}
					
					
					
					
					
					
					
					
					
					
					// If the Vector does not intersect any of the other connections from this Unit add the Unit as possible connection partner.
					if(isPossibleConnection) {
						possibleConnections.add(targetMember);
					}
				}
			}
			
			return possibleConnections;
		}
	
		private HashSet<Endpoint> generateMappedConnections(HashSet<IRetreatUnit> possibleConnectionPartners, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints) {
			HashSet<Endpoint> mappedConnections = new HashSet<>();
			
			for (IRetreatUnit IRetreatUnit : possibleConnectionPartners) {
				mappedConnections.add(mappedUnitsToEndpoints.get(IRetreatUnit));
			}
			return mappedConnections;
		}
	
	}
	
	// TODO: WIP Is undirected.
	private class EndpointGraph {
		
		// The nodes in the graph / mesh.
		HashMap<Endpoint, HashSet<Endpoint>> nodes = new HashMap<>();

		public void updateEndpoint(Endpoint endpoint) {
			endpoint.updatePosition();
		}
		
		public void updateEndpoints() {
			for (Endpoint endpoint : this.nodes.keySet()) {
				endpoint.updatePosition();
			}
		}
		
		public void addEndpoint(Endpoint endpoint, HashSet<Endpoint> connections) {
			this.nodes.put(endpoint, connections);
			
			// Take each Endpoint of the defined ones and add the given Endpoint to their connection HashSet since this is a undirected graph.
			for (Endpoint node : connections) {
				HashSet<Endpoint> existingConnections = this.nodes.get(node);
				if(existingConnections != null && !existingConnections.contains(endpoint)) {
					existingConnections.add(endpoint);
				}
			}
		}
		
		public boolean removeEndPoint(Endpoint endpoint) {
			boolean success = false;
			
			// Try to remove the key from the HashMap and all other connected HashSets.
			if(this.nodes.containsKey(endpoint)) {
				// Remove all saved references of the Endpoint's connection.
				for (Endpoint connection : this.nodes.get(endpoint)) {
					this.nodes.get(connection).remove(endpoint);
				}
				
				// Remove the Endpoint himself.
				this.nodes.remove(endpoint);
				
				
				
				
				
				
				success = true;
				
				// TODO: WIP Update all other Endpoints that are connected towards the removed one.
				// If a node from the connected ones is removed, update all connections and try to connect to new nodes in the mesh.
			}
			return success;
		}
		
		public HashMap<Endpoint, HashSet<Endpoint>> getNodes() {
			return nodes;
		}
	}
	
	private class Endpoint {
		
		// The distance that the Endpoint tries to keep towards all other connected Endpoints.
		private static final double DESIRED_SPACE_IN_BETWEEN = 64;
		
		// The graph / mesh the Endpoint is part of.
		private EndpointGraph graph;
		// The position of the Endpoint.
		private Point point;
		
		public Endpoint(EndpointGraph graph, HashMap<IRetreatUnit, Endpoint> mappedUnitsToEndpoints, IRetreatUnit baseUnit, IRetreatUnit directionUnit) {
			// Vector that will be used for generating the Point of the Endpoint. This uses the first Unit as starting location and the second one as direction. The Vector is then set to a certain length. 
			Vector vecInDirection = new Vector(baseUnit.defineCurrentPosition(), directionUnit.defineCurrentPosition());
			vecInDirection.setToLength(DESIRED_SPACE_IN_BETWEEN);
			
			// Vector from the base Unit's Endpoint to the new one for the direction Unit. The X and Y coordinates are taken from the base and combined with the directions of the direction Unit.
			Endpoint baseUnitEndpoint = mappedUnitsToEndpoints.get(baseUnit);
			Vector vecToEndposition = new Vector(baseUnitEndpoint.getPoint().getX(), baseUnitEndpoint.getPoint().getY(), vecInDirection.getDirX(), vecInDirection.getDirY());

			this.point = new Point((int)(vecToEndposition.getX() + vecToEndposition.getDirX()), (int)(vecToEndposition.getY() + vecToEndposition.getDirY()), Point.Type.POSITION);
			this.graph = graph;
		}
		
		public Endpoint(EndpointGraph graph, Point point) {
			this.graph = graph;
			this.point = point;
		}
		
		public void updatePosition() {
			// TODO: WIP Save in property -> time save!
			HashSet<Endpoint> connections = this.graph.getNodes().get(this);
			
			
			
			
			// TODO: WIP ADD
			
			
			
			
		}
		
		public Point getPoint() {
			return point;
		}
		
	}

}
