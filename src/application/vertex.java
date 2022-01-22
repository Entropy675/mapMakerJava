package application;

public class vertex
{
	private double x, y;
	
	vertex(double X, double Y)
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
	
	public void set(vertex a)
	{
		this.x = a.x;
		this.y = a.y;
	}
	
	public void add(vertex a, vertex b)
	{
		
		this.setX(a.getX() + b.getX());
		this.setY(a.getY() + b.getY());
		
	}	
	public void sub(vertex a, vertex b)
	{
		this.setX(a.getX() - b.getX());
		this.setY(a.getY() - b.getY());
	}
	
}
