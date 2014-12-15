package rtree;

public class Coordinate implements Comparable<Coordinate> {
	private int x;
	private int y;
	private long hilbertValue;
	
	public Coordinate(int x, int y, long z) {
		this.x = x;
		this.y = y;
		this.hilbertValue = z;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public long getHilbert() {
		return this.hilbertValue;
	}
	
	public int compareTo(Coordinate o) {
		return Long.compare(this.hilbertValue, o.getHilbert());
	}
	
	public String toString() {
		return "(" + String.valueOf(getX()) + ", " +
				String.valueOf(getY()) + ", " + String.valueOf(getHilbert()) + ")";
	}
	
}
