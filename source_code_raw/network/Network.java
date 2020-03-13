package network;

import java.util.ArrayList;
import java.util.Map;

public class Network {
	private ArrayList<Node> nodes;
	private boolean splitHorizon;

	public Network(int numNodes) {
		nodes = new ArrayList<Node>();
		splitHorizon = false;
		
		for (int i=1; i <= numNodes; i++) {
			Node n = new Node(i);
			nodes.add(n);
		}
	}
	
	// ---------- GETTERS

	public ArrayList<Node> getNodes() {
		return nodes;
	}
	
	public int getNumNodes() {
		return nodes.size();
	}
	
	public boolean getSplitHorizon() {
		return splitHorizon;
	}
	
	public void toggleSplitHorizon() {
		splitHorizon = !splitHorizon;
	}
	
	// ---------- ADD INITIAL VALUES TO NETWORK
	
	// Used to add the initial nodes into the network, as described in the network input file
	public void addInitialNodes(int node1, int node2, int cost){
		Node n1 = nodes.get(node1-1);
		Node n2 = nodes.get(node2-1);
		
		n1.addRoute(n2, n2, cost);
        n2.addRoute(n1, n1, cost);
    }
	
	// Initialise neighbours for a node upon creation of the network
	public void addNeighbours(int node1, int node2) {
		Node n1 = nodes.get(node1-1);
		Node n2 = nodes.get(node2-1);
		
		n1.addNeighbour(n2);
		n2.addNeighbour(n1);
	}
	
	// ---------- DVT & LINK UPDATE FUNCTIONS
	
	public void updateDVT(int nodeLabel, boolean splitHorizon) {
		Node node = nodes.get(nodeLabel-1);
		Map<Node, Integer> dvt = node.getDistances();
		ArrayList<Node> neighbours = node.getNeighbours();
		
		// Get the DVT for each neighbour, update the current node's DVT from this information
		for (Node neighbour : neighbours) {
			Map<Node, Integer> neighbourDVT = neighbour.getDistances();
			int weight = dvt.get(neighbour);
//			System.out.println("\nw(" + node + "," + neighbour + "): " + weight);
			
			// Check neighbours DVT for better routes
			for (Node destination : neighbourDVT.keySet()) {
				if (!destination.getUnreachable() ) {
					// Split Horizon Implementation
					if (splitHorizon) {
						// Skip the currently looked at node, if the node is a neighbour of your neighbour
						if (node.getNeighbours().contains(destination)) {
							// skip to the next destination
							continue;
						}
					}
					
					// Distance from Neighbour->Destination, and Source->Destination
					int distanceND = neighbourDVT.get(destination);
					int distanceSD = dvt.get(destination);
	//				System.out.println(destination + " - ND:" + distanceND + ", SD:" + distanceSD);
					
					if (node != destination || neighbour != destination)  {
						if (distanceSD > distanceND + weight) {
							distanceSD = distanceND + weight;
							node.addRoute(destination, neighbour, distanceSD);
							
							System.out.println("Improved route identified from " + node + " to " + destination);
							System.out.println("       New Outgoing Node: " + neighbour);
							System.out.println("       New Cost: " + distanceSD);
						}
					}
				} else {
					// If a node is unreachable, then update the DVT to reflect that
					node.addRoute(destination, null, 999);
				}
			}
		}
	}
	
	// Builds the initial Distance Vector Table
	public void buildDVT(int numNodes) {
		for (Node node : nodes) {
			for (int i = 0; i < numNodes; i++) {
				Node currentNode = nodes.get(i);
				
				// If you come to the node you're currently on, set the distance to itself as 0
				if (node == currentNode) {
					node.addRoute(node, null, 0);
				} else {
					// Otherwise create an "unknown" link
					node.addRoute(currentNode, null, 999);	
				}				
			}
		}
	}
	
	public void updateLink(int source, int dest, int outgoing, int cost) {
		Node sourceNode = nodes.get(source-1);
		Node destNode = nodes.get(dest-1);
		Node outgoingNode = nodes.get(outgoing-1);
		
		sourceNode.addRoute(destNode, outgoingNode, cost);
	}
	
	// ---------- PRINT FUNCTIONS
		
	public void printNetwork() {
		System.out.println("Source || (Destination Node, Cost, Outgoing Node)");
		System.out.println("----------------------------------------------------");
		for (int i=0; i < nodes.size(); i++) {
			Node n = nodes.get(i);
			n.printDVT();
			System.out.println();
		}
	}
	
	public int printRoute(int sourceNode, int destinationNode) {
		System.out.println("\nRoute from nodes " + sourceNode + " to " + destinationNode + ":");
		Node source;
		Node destination; 
		
		try {
			source = nodes.get(sourceNode - 1);
			destination = nodes.get(destinationNode - 1);
		} catch (IndexOutOfBoundsException e) {
			return 0;
		}
		
		if (source == destination) {
			System.out.println("Source node is the same as destination node!");
			return 0;
		}
		
		if (source.getDistances().get(destination) == 999) {
			System.out.println("Route is currently unknown");
			return 0;
		}
		
		// Build a list for the route
		ArrayList<Node> route = new ArrayList<Node>();
		Node currentNode = source;
		route.add(currentNode);
				
		while (currentNode != destination) {
			Map<Node, Node> outgoingNodes = currentNode.getOutgoingNodes();
			Node nextNode = outgoingNodes.get(destination);
			route.add(nextNode);
			currentNode = nextNode;
		}
				
		for (Node node : route) {
			if (node != destination) {
				System.out.print(node + " -> ");
			} else {
				System.out.print(node);
			}
		}
		
		System.out.println("\nCost: " + source.getDistances().get(destination));
		return 1;
	}


}


