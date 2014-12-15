package rtree;

import java.util.*;

public class LeafPage {
	private int lowX;
	private int lowY;
	private int highX;
	private int highY;
	private List<Coordinate> leafPage;
	
	public LeafPage() {
		this.lowX = Integer.MAX_VALUE;
		this.lowY = Integer.MAX_VALUE;
		this.highX = Integer.MIN_VALUE;
		this.highY = Integer.MIN_VALUE;
		this.leafPage = new ArrayList<Coordinate>();
	}
	
	public void addRNode(Coordinate node) {
		this.leafPage.add(node);
		
		if(node.getX() < this.lowX)
			this.lowX = node.getX();
		
		if(node.getY() < this.lowY)
			this.lowY = node.getY();
		
		if(node.getX() > this.highX)
			this.highX = node.getX();
		
		if(node.getY() > this.highY)
			this.highY = node.getY();
		
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
		return this.leafPage.get(this.leafPage.size()-1).getHilbert();
	}
	
	public List<Coordinate> getCoordinates() {
		return this.leafPage;
	}
	
	public String toString(){
		return "LHV: " + getLHV() + ", low (" + String.valueOf(getLowX()) + ", " +
				String.valueOf(getLowY()) + "), high: (" +
				String.valueOf(getHighX()) + ", " + String.valueOf(getHighY()) + ")";
	}
}