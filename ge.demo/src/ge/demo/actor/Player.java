package ge.demo.actor;

import ge.framework.mesh.Mesh;

public class Player extends Actor
{
	private int health = 100;

	private int air = 10;
	
	public Player()
	{
		// TODO Auto-generated constructor stub
	}
	
	public void draw(Mesh mesh)
	{
		
	}
	
	public void act(long delta)
	{
		
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getAir() {
		return air;
	}

	public void setAir(int air) {
		this.air = air;
	}

}
