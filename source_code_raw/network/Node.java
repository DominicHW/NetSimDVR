package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {
	private int label;
	private boolean unreachable;
	// Distances from Node n to m
	private Map<Node, Integer> distances;
	// The next node to jump to, to get from Node n to m
	private Map<Node, Node> outgoingNodes;
	private ArrayList<Node> neighbours;
	
	public Node(int name) {
		label = name;
		unreachable = false;
		distances = new HashMap<Node, Integer>();
		outgoingNodes = new HashMap<Node, Node>();
		neighbours = new ArrayList<Node>();
	}
	
	// ---------- GETTERS
	
	public int getLabel() {
		return label;
	}
	
	public boolean getUnreachable() {
		return unreachable;
	}
	
	public ArrayList<Node> getNeighbours() {
		return neighbours;
	}
	
	public Map<Node, Integer> getDistances() {
		return distances;
	}

	public Map<Node, Node> getOutgoingNodes() {
		return outgoingNodes;
	}
	
	// ---------- ADDERS/SETTERS
	public void setUnreachable() {
		unreachable = false;
	}
	
	public void addNeighbour(Node n) {
		neighbours.add(n);
	}
	
	// Add a route to destination node, with the cost and outgoing node 
	public void addRoute(Node dest, Node outgoingNode, int cost) {
		distances.put(dest, cost);
		outgoingNodes.put(dest, outgoingNode);
	}
	
	// ---------- PRINT
	public void printDVT() {
		System.out.print(label + "      || ");
		
		for (Node destination : distances.keySet()) {
			System.out.print("(" + destination + "," + distances.get(destination) + "," + outgoingNodes.get(destination) + "), ");
		}
	}
	
	@Override
	public String toString() {
		return String.valueOf(label);
	}
	
	
}
