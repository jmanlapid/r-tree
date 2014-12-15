package rtree;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private int lowX;
	private int lowY;
	private int highX;
	private int highY;
	private long LHV;
	private boolean isBottomNode;
	
	private List<Node> nodePtrs;
	private List<LeafPage> leafPtrs;
	
	public Node(List<LeafPage> lps, int LEAF_CAPACITY) {
		this.leafPtrs = new ArrayList<LeafPage>();
		this.lowX = Integer.MAX_VALUE;
		this.lowY = Integer.MAX_VALUE;
		this.highX = Integer.MIN_VALUE;
		this.highY = Integer.MIN_VALUE;
		this.LHV = Long.MIN_VALUE; 
		this.isBottomNode = true;
		this.leafPtrs.addAll(lps);
		this.LHV = this.leafPtrs.get(this.leafPtrs.size()-1).getLHV();
	
		LeafPage node;
		for(int i = 0; i < lps.size(); i++) {
			node = lps.get(i);
			
			if(node.getLowX() < this.lowX)
				this.lowX = node.getLowX();
			
			if(node.getLowY() < this.lowY)
				this.lowY = node.getLowY();
			
			if(node.getHighX() > this.highX)
				this.highX = node.getHighX();
			
			if(node.getHighY() > this.highY)
				this.highY = node.getHighY();
		}
	}
	
	public Node(int NODE_CAPACITY, List<Node> nodes) {
		this.nodePtrs = new ArrayList<Node>();
		this.lowX = Integer.MAX_VALUE;
		this.lowY = Integer.MAX_VALUE;
		this.highX = Integer.MIN_VALUE;
		this.highY = Integer.MIN_VALUE;
		this.LHV = Long.MIN_VALUE; 
		this.isBottomNode = false;
		this.nodePtrs.addAll(nodes);
		this.LHV = this.nodePtrs.get(this.nodePtrs.size()-1).getLHV();
		Node node;
		for(int i = 0; i < nodes.size(); i++) {
			node = nodes.get(i);
			
			if(node.getLowX() < this.lowX)
				this.lowX = node.getLowX();
			
			if(node.getLowY() < this.lowY)
				this.lowY = node.getLowY();
			
			if(node.getHighX() > this.highX)
				this.highX = node.getHighX();
			
			if(node.getHighY() > this.highY)
				this.highY = node.getHighY();
		}
	}
	
	public int getHighX() {
		return this.highX;
	}
	
	public int getLowX() {
		return this.lowX;
	}
	
	public int getHighY() {
		return this.highY;
	}
	
	public int getLowY() {
		return this.lowY;
	}
	
	public long getLHV() {
		return this.LHV;
	}
	
	public boolean isBottomNode() {
		return this.isBottomNode;
	}
	
	public List<Node> getNodes() {
		return this.nodePtrs;
	}
	
	public List<LeafPage> getLeaves() {
		return this.leafPtrs;
	}
	
	public String toString(){
		return "LHV: " + getLHV() + ", low (" + String.valueOf(getLowX()) + ", " +
				String.valueOf(getLowY()) + "), high: (" +
				String.valueOf(getHighX()) + ", " + String.valueOf(getHighY()) + ")";
	}
	
}
