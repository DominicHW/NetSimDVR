package network;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("Welcome, please select one of the following commands:");
		System.out.println("   1. route : Print the route between 2 nodes\n"
						 + "   2. printnet : Print the DVT for all nodes in network\n"
						 + "   3. toggle: Toggle split horizon capability\n"
						 + "   4. compute: Compute routing tables for a number of iterations\n"
						 + "   5. update: Update a link\n"
						 + "   6. fail: Select a node to become unreachable\n"
						 + "   7. load: Load in a new network from a txt file\n"
						 + "   8. quit : Close the program\n");
		
		Scanner scanner = new Scanner(System.in);
		String input = "";
		Network net = readNetworkFromFile("network1.txt");
		
		// Load in network1
		System.out.println("INITIAL LOAD: network1.txt loaded!");
		System.out.println("Number of nodes in network: " + net.getNumNodes());
		System.out.println("\nBegin typing one of the commands listed above (try printnet!):\n\n");
		
		// Continuously read in user input for commands
		while(!"quit".equalsIgnoreCase(input)) {
			// Check what the user input is, perform actions based on given commands
			if (input.contentEquals("route")) {
				int source;
				int destination;
				
				System.out.println("Please enter source node: ");
				System.out.print("> ");
				input = scanner.nextLine();
				
				// Checks if the source input is an integer and is valid
				try {
					source = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n ");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
								
				
				if (!(1 <= source && source <= net.getNumNodes())) {
					System.out.println("Node doesn't exist in the network\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
								
				System.out.println("Please enter destination node: ");
				System.out.print("> ");
				input = scanner.nextLine();
				destination = Integer.parseInt(input);
				
				// Check if the destination input is an integer and is valid
				try {
					destination = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				if (!(1 <= destination && destination <= net.getNumNodes())) {
					System.out.println("Node doesn't exist in the network\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				net.printRoute(source, destination);
				System.out.println("\nEnter a new command:");

				
			} else if (input.contentEquals("printnet")) {
				System.out.println();
				net.printNetwork();
				System.out.println();
				System.out.println("Enter a new command:");
			} else if (input.contentEquals("toggle")) {
				net.toggleSplitHorizon();
				System.out.println("Split Horizon Enabled: " + net.getSplitHorizon());
				System.out.println("\nEnter a new command:");
			} else if (input.contentEquals("")) {
				// No input, do nothing
				System.out.println("Enter a new command:");
				input = scanner.nextLine();
				continue;
			} else if (input.contentEquals("compute")) {				
				// Compute the DVTs for all nodes over a number of iterations
				// - each iteration is a periodic exchange
				int iterations;
				
				System.out.println("Please enter the number of iterations you want to perform: ");
				System.out.print("> ");
				input = scanner.nextLine();
				
				// Checks if the input is an integer and is valid
				try {
					iterations = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				// Print out the initial DVT so changes can be compared
				System.out.println("Initial Network DVT");
				net.printNetwork();
				
				for (int i = 1; i <= iterations; i++) {
					
					System.out.println("\n\n################################# Iteration: " + i + "\n");
					System.out.println("Calculating...\n");
					// Sleep is for the user: doesn't produce all the information at once
					TimeUnit.SECONDS.sleep(1);
					
					for (Node node : net.getNodes()) {
						// Only update the DVT of reachable nodes
						if (!node.getUnreachable()) {
							net.updateDVT(node.getLabel(), net.getSplitHorizon());
						}
					}
				}

				// Print out the new network DVT, allow for comparison to DVT prior to computation
				System.out.println("\nNew Network DVT");
				net.printNetwork();
				System.out.println();
				System.out.println("Enter a new command:");
				
				
			} else if (input.contentEquals("load")) {
				// User can enter the name of a file, program loads or rejects (if it doesn't exist)
				// NOTE: File MUST be in the format specified in the 'network_description.pdf'
				System.out.println("Please enter name of file (+ extension): ");
				System.out.print("> ");
				input = scanner.nextLine();
				net = readNetworkFromFile(input);
				
				if (net == null) {
					// If the load fails, just load in the default network (network1.txt)
		            System.err.println("Failed to open file: " + input + ".\nReopening network1.txt\n");
					net = readNetworkFromFile("network1.txt");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				} else {
					System.out.println("File loaded in successfully!\n");
					System.out.println("Enter a new command:");
				}
			} else if (input.contentEquals("update")) {
				// Update the link between two neighbours
				int source;
				int neighbour;
				int cost;
				ArrayList<Node> neighbours;
				
				System.out.println("Please enter source node: ");
				System.out.print("> ");
				input = scanner.nextLine();
				
				// Checks if the source input is an integer and is valid
				try {
					source = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
								
				// Check that node is in the network
				if (!(1 <= source && source <= net.getNumNodes())) {
					System.out.println("Node doesn't exist in the network\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				// Print the neighbours to choose from
				neighbours = net.getNodes().get(source-1).getNeighbours();
				System.out.println("Node " + source + "'s neighbours: " + neighbours.toString());
								
				System.out.println("Please enter neighbour link to change: ");
				System.out.print("> ");
				input = scanner.nextLine();
				neighbour = Integer.parseInt(input);
				
				// Check if the neighbour input is an integer and is valid
				try {
					neighbour = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				if (!(neighbours.contains(net.getNodes().get(neighbour-1)))) {
					System.out.println("That node isn't a neighbour!\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				// Select a cost (not negative)
				System.out.println("Please enter new cost: ");
				System.out.print("> ");
				input = scanner.nextLine();
				
				// Checks if the cost input is an integer and is valid
				try {
					cost = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				if (cost < 0) {
					System.err.println("Cost cannot be a negative number\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				// Update the link each way round, as the link is bi-directional
				net.updateLink(source, neighbour, neighbour, cost);
				net.updateLink(neighbour, source, source, cost);
				System.out.println("Neighbour link updated!\n");
				System.out.println("Enter a new command:");
				
			} else if (input.contentEquals("fail")) {
				// Drop a node from the network, implementation isn't fully working
				int n;
				
				System.out.println("Please enter node become unreachable: ");
				System.out.print("> ");
				input = scanner.nextLine();
				
				// Checks if the input is an integer and is valid
				try {
					n = Integer.parseInt(input);
				} catch (Exception e) {
					System.out.println("Not a valid input\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
								
				// Check that node is in the network
				if (!(1 <= n && n <= net.getNumNodes())) {
					System.out.println("Node doesn't exist in the network\n");
					System.out.println("Enter a new command:");
					input = scanner.nextLine();
					continue;
				}
				
				Node node = net.getNodes().get(n-1);
				node.setUnreachable();
				
				// Update the links to neighbours to have a cost of 999
				for (Node neighbour : node.getNeighbours()) {
					net.updateLink(n, neighbour.getLabel(), neighbour.getLabel(), 999);
					net.updateLink(neighbour.getLabel(), n, n, 999);
				}
				
				System.out.println("Node " + n + " is now unreachable!\n");
				System.out.println("Enter a new command:");
				
			} else {
				System.err.println("That is not a command, please try again!");
				input = scanner.nextLine();
				continue;
			}
			
			input = scanner.nextLine();
			
		}
		
		// Close input reader	
		scanner.close();

		System.out.println("Goodbye!");
		
	}
	
	// Read and build the network from a file
	// Creates all notes in the network, any edges between nodes, and initial DVT
	public static Network readNetworkFromFile(String filename) {
		FileReader fr = null;
		Scanner in = null;
		Network net;
		
		try {
			try {
                fr = new FileReader(filename);
                in = new Scanner(fr);
                
                // Get the number of nodes
                String line = in.nextLine();
                int numNodes = Integer.parseInt(line);
                
                // Initialise the network and each node's DVT
                net = new Network(numNodes);
            	net.buildDVT(numNodes);
                
            	// Keep reading in lines until the file is finished
                while (in.hasNextLine()) {
                	line = in.nextLine();
                	
                	// [node1, node2, cost]
                	String[] tokens = line.split(",");
                	
                	// n and m are the two connected nodes
                	int n = Integer.parseInt(tokens[0]);
                	int m = Integer.parseInt(tokens[1]);
                	int cost = Integer.parseInt(tokens[2]);
                	
                	// create initial known links for graph
                	net.addInitialNodes(n, m, cost);
                	net.addNeighbours(n, m);                
                }
			} finally {
				// Close the file readers, no memory leaks
                if (fr != null) {
                    fr.close();
                }
                if (in != null) {
                    in.close();
                }
			}
        } catch (IOException e) {
//            System.err.println("Could not open file: " + filename);
//            System.err.println(e);
//            System.exit(1);
            return null;
        }
		
		return net;
	}
}
