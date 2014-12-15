package rtree;
import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.*;

public class Main {

	public static final int DATA_POINTS = 30000;
	public static final int LEAF_CAPACITY = 50;
	public static final int NODE_CAPACITY = 5;
	public static final int BITS_PER_DIM = 14;
	
	public static final int LEAF_PAGES = (int)Math.ceil(DATA_POINTS / LEAF_CAPACITY);
	public static final int BOTTOM_NODES = (int)Math.ceil(LEAF_PAGES/NODE_CAPACITY);
	
	public static int IO_COST = 0;
	
	public static List<Coordinate> coordinates = new ArrayList<Coordinate>();
	public static List<LeafPage> leafPages = new ArrayList<LeafPage>();
	public static List<Node> rTree = new ArrayList<Node>();
	public static Node Root;
	
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.err.println("1st argument must be the name of the data file");
			System.exit(0);
		}
		
		Scanner console = new Scanner(System.in);
		int userInput = 0;
		File file = new File(args[0]);
		processFile(file);
		Collections.sort(coordinates);
		createLeafPages();
		buildBottomLevel();
		buildRTree();
		
		System.out.println("RTree has been built successfully!.\n");
		
		while(true) {
			
			IO_COST = 0;
			
			System.out.print("What would you like to do?\n" +
					"1) Point query\n" +
					"2) Range query\n" +
					"3) Exit\n" +
					"Please input digit: ");
	
			try {
				userInput = console.nextInt();
				if(userInput != 1 && userInput != 2 && userInput != 3)
					throw new InputMismatchException();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input.\n");
				continue;
			} finally {
                console.nextLine();
			}
			
			System.out.println();
			
			switch(userInput) {
				case 1:
					pointQueryInterface();
					break;
				case 2:
					rangeQueryInterface();
					break;
				case 3: 
					System.out.println("Goodbye!");
					console.close();
					System.exit(1);
				default: 
					System.out.println("Please try again.");
					break;
			}
		}
	}

	static long getHilbertValue(int x1, int x2) {
	    long res = 0;
	
	    for (int ix = BITS_PER_DIM - 1; ix >= 0; ix--) {
	        long h = 0;
	        long b1 = (x1 & (1 << ix)) >> ix;
	        long b2 = (x2 & (1 << ix)) >> ix;
	
	        if (b1 == 0 && b2 == 0) {
	            h = 0;
	        } else if (b1 == 0 && b2 == 1) {
	            h = 1;
	        } else if (b1 == 1 && b2 == 0) {
	            h = 3;
	        } else if (b1 == 1 && b2 == 1) {
	            h = 2;
	        }
	        res += h << (2 * ix);
	    }
	    return res;
	}
	
	public static void processFile(File file) {
		BufferedReader reader = null;
		try {
			int x, y = 0;
			long hilbertValue = 0;
		    reader = new BufferedReader(new FileReader(file));
		    String text = null;
		    String[] split = null;
		    while ((text = reader.readLine()) != null) {
		    	split = text.split(",");
		    	x = Integer.valueOf(split[0]);
		    	y = Integer.valueOf(split[1]);
		    	hilbertValue = getHilbertValue(x, y);
		    	coordinates.add(new Coordinate(x, y, hilbertValue));
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        if (reader != null) {
		            reader.close();
		        }
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}	
	}
	
	public static void createLeafPages() {
		int bulkCount = 0;
		for(int i = 0; i < LEAF_PAGES; i++) {
			LeafPage lf = new LeafPage();
			for(int x = 0; x < LEAF_CAPACITY; x++) {
				lf.addRNode(coordinates.get(bulkCount));
				bulkCount++;
			}
			leafPages.add(lf);
		}
	}
	
	public static void buildBottomLevel() {
		//System.out.println("leafPages: " + leafPages.size());
		//System.out.println("bottomNodes: " + BOTTOM_NODES);
		
		int startIndex = 0;
		int toIndex = NODE_CAPACITY;
		
		for(int i = 0; i <= BOTTOM_NODES; i++) {
			//System.out.println("startIndex: " + startIndex + ", toIndex: " + toIndex);
			if(toIndex <= leafPages.size())
				rTree.add(new Node(leafPages.subList(startIndex, toIndex), NODE_CAPACITY));
			else if (toIndex > leafPages.size()) {
				toIndex = leafPages.size();
				if(startIndex != toIndex)
					rTree.add(new Node(leafPages.subList(startIndex, toIndex), NODE_CAPACITY));
			}
			startIndex += NODE_CAPACITY;
			toIndex += NODE_CAPACITY;
		}
		/*
		for(int i = 0; i < rTree.size(); i++) {
			System.out.println(rTree.get(i).toString());
		}
		*/
	}
	
	public static void buildRTree() {
		if(rTree.size() == 1) {
			Root = rTree.get(0);
			return;
		}
		
		List<Node> newRTree = new ArrayList<Node>();
		int startIndex = 0;
		int toIndex = NODE_CAPACITY;
		
		while(startIndex <= rTree.size()) {
			/*
			System.out.println("rTree size: " + rTree.size());
			System.out.println("startIndex: " + startIndex + ", toIndex: " + toIndex);
			System.out.println();
			*/
			if(toIndex <= rTree.size())
				newRTree.add(new Node(NODE_CAPACITY, rTree.subList(startIndex, toIndex)));
			else if(toIndex > rTree.size()) {
				toIndex = rTree.size();
				if(startIndex != toIndex)
					newRTree.add(new Node(NODE_CAPACITY, rTree.subList(startIndex, toIndex)));
			}
			startIndex += NODE_CAPACITY;
			toIndex += NODE_CAPACITY;
		}
		rTree.clear();
		rTree.addAll(newRTree);
		buildRTree();
	}
	
	public static void pointQueryInterface() {
		Scanner console = new Scanner(System.in);
		String userInput;
		String inputArray[];
		int x = 0, y = 0;
		List<Coordinate> queryResults = new ArrayList<Coordinate>(); 
		
		do {
			try {
				System.out.println("Enter x and y coordinates, separated by a space, in range [0, 10000]: ");
				userInput = console.nextLine();
				inputArray = userInput.split(" ");
				x = Integer.valueOf(inputArray[0]);
				y = Integer.valueOf(inputArray[1]);
				if((x < 0 || x > 10000) || (y < 0 || y > 10000)) {
					throw new InputMismatchException();
				}
				break;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input.");
			} catch (Exception e) {
				System.out.println("Invalid input.");
			} finally {
				//console.nextLine();
				console.reset();
            }
			
			} while(true);
		
		pointQuery(x, y, getHilbertValue(x, y), Root, queryResults);
		if(queryResults.size() == 0) {
			System.out.println("Could not find the point (" + x + ", " + y + ").");
			System.out.println("I/O cost : " + IO_COST + "\n");
		} else {
			System.out.println(queryResults.get(0).toString() + " was found " + queryResults.size() + " times.");
			System.out.println("I/O cost : " + IO_COST + "\n");
		}
	}
	
	public static void pointQuery(int x, int y, long hilbert, Node root, List<Coordinate> queryResults) {
		IO_COST++;
		if(root.isBottomNode()) {
			for(LeafPage leaves : root.getLeaves()) {
				if(x >= leaves.getLowX() && x <= leaves.getHighX()
						&& y >= leaves.getLowY() && y <= leaves.getHighY()) {
					IO_COST++;
					for(Coordinate coord : leaves.getCoordinates()) {
						if(x == coord.getX() && y == coord.getY())
							queryResults.add(coord);
					}
				}
			}
		} else {
			for(Node node : root.getNodes()) {
				if(x > node.getLowX() && x < node.getHighX() &&
						y > node.getLowY() && y < node.getHighY()) {
					pointQuery(x, y, hilbert, node, queryResults);
				}
			}
		}
		return;
	}
	
	public static void rangeQueryInterface() {
		Scanner console = new Scanner(System.in);
		String userInput;
		String inputArray[];
		int LX = 0, LY = 0, HX = 0, HY = 0;
		List<Coordinate> queriedRange = new ArrayList<Coordinate>();
		
		do {
			try {
				System.out.println("Enter x and y coordinates of low corner, separated by a space, in range [0, 10000]: ");
				userInput = console.nextLine();
				inputArray = userInput.split(" ");
				LX = Integer.valueOf(inputArray[0]);
				LY = Integer.valueOf(inputArray[1]);
				if((LX < 0 || LX > 10000) || (LY < 0 || LY > 10000)) {
					throw new InputMismatchException();
				}
				
				System.out.println("Enter x and y coordinates of high corner, separated by a space, in range [0, 10000]: ");
				userInput = console.nextLine();
				inputArray = userInput.split(" ");
				HX = Integer.valueOf(inputArray[0]);
				HY = Integer.valueOf(inputArray[1]);
				if((HX < 0 || HX > 10000) || (HY < 0 || HY > 10000)) {
					throw new InputMismatchException();
				}
				
				break;
			} catch (InputMismatchException e) {
				System.out.println("Invalid input.");
			} catch (Exception e) {
				System.out.println("Invalid input.");
			} finally {
				//console.nextLine();
				console.reset();
            }
			
			} while(true);
		
		rangePointQuery(LX, LY, HX, HY, getHilbertValue(HX, HY), queriedRange, Root);
		if(queriedRange.isEmpty()) {
			System.out.println("Could not find any points between corners (" + LX + ", " + LY + ") and (" + HX + ", " + HY + ")");
			System.out.println("I/O cost : " + IO_COST + "\n");
		} else {
			for(Coordinate coord : queriedRange)
				System.out.println(coord.toString());
			System.out.println("Total coordinates found: " + queriedRange.size());
			System.out.println("I/O cost : " + IO_COST + "\n");
		}
	}
	
	public static void rangePointQuery(int LX, int LY, int HX, int HY, long hilbert, 
			List<Coordinate> queriedRange, Node root) {
		IO_COST++;
		if(root.isBottomNode()) {
			for(LeafPage leaves : root.getLeaves()) {
				if(rectanglesIntersect(LX, LY, HX, HY, 
						leaves.getLowX(), leaves.getLowY(), leaves.getHighX(), leaves.getHighY())) {
					IO_COST++;
					for(Coordinate coord : leaves.getCoordinates()) {
						if(LX <= coord.getX() && LY <= coord.getY() &&
								HX >= coord.getX() && HY >= coord.getY()) {
							queriedRange.add(coord);
						}
					}
				}
			}
		} else 
			for(Node node : root.getNodes())
				if(rectanglesIntersect(LX, LY, HX, HY, 
						node.getLowX(), node.getLowY(), node.getHighX(), node.getHighY()))
					rangePointQuery(LX, LY, HX, HY, hilbert, queriedRange, node);
	}
	
	public static boolean rectanglesIntersect(int minAx, int minAy, int maxAx, int maxAy,
		    int minBx, int minBy, int maxBx, int maxBy ) {
		    boolean aLeftOfB = maxAx < minBx;
		    boolean aRightOfB = minAx > maxBx;
		    boolean aAboveB = minAy > maxBy;
		    boolean aBelowB = maxAy < minBy;

		    return !( aLeftOfB || aRightOfB || aAboveB || aBelowB );
	}
}
