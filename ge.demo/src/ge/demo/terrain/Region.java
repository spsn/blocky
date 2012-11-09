package ge.demo.terrain;
import ge.framework.mesh.Mesh;

//TODO
public class Region
{
	// Visibility indicator
	private boolean visible;

	// Coordinates
	private int x1, x2, y1, y2, z1, z2;

	// Mesh
	private Mesh mesh1;
	private Mesh mesh2;

	/**
	 * Constructor.
	 */
	public Region(int x1, int x2, int y1, int y2, int z1, int z2)
	{
		// Call super constructor
		super();

		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(
		final boolean visible)
	{
		this.visible = visible;
	}

	public int getX1()
	{
		return x1;
	}

	public void setX1(int x1)
	{
		this.x1 = x1;
	}

	public int getX2()
	{
		return x2;
	}

	public void setX2(int x2)
	{
		this.x2 = x2;
	}

	public int getY1()
	{
		return y1;
	}

	public void setY1(int y1)
	{
		this.y1 = y1;
	}

	public int getY2()
	{
		return y2;
	}

	public void setY2(int y2)
	{
		this.y2 = y2;
	}

	public int getZ1() {
		return z1;
	}

	public void setZ1(int z1)
	{
		this.z1 = z1;
	}

	public int getZ2()
	{
		return z2;
	}

	public void setZ2(int z2)
	{
		this.z2 = z2;
	}

	public Mesh getMesh1()
	{
		return mesh1;
	}

	public void setMesh1(Mesh mesh1)
	{
		this.mesh1 = mesh1;
	}

	public Mesh getMesh2()
	{
		return mesh2;
	}

	public void setMesh2(Mesh mesh2)
	{
		this.mesh2 = mesh2;
	}

}
