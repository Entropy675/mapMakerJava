package application;

public class Vertex
{
	private double x, y;
	
	Vertex(double X, double Y)
	{
		x = X;
		y = Y;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}
	
	public void set(Vertex a)
	{
		this.x = a.x;
		this.y = a.y;
	}
	
	public void add(Vertex a, Vertex b)
	{
		
		this.setX(a.getX() + b.getX());
		this.setY(a.getY() + b.getY());
		
	}	
	public void sub(Vertex a, Vertex b)
	{
		this.setX(a.getX() - b.getX());
		this.setY(a.getY() - b.getY());
	}
	
}
