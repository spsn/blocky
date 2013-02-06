package ge.demo.actor;

import org.lwjgl.util.vector.Vector3f;

import ge.framework.mesh.Mesh;

public class Player extends Actor
{
	private int health = 100;

	private int air = 10;
	
	//TODO
	private Mesh headMesh;
	private Mesh bodyMesh;

	//TODO
	protected Vector3f headPosition;
	protected Vector3f bodyPosition;

	//TODO
	protected Vector3f headRotation;
	protected Vector3f bodyRotation;

	public Player()
	{
		// Call super constructor
		super();

		//TODO
		headPosition = new Vector3f();
		bodyPosition = new Vector3f();
		headRotation = new Vector3f();
		bodyRotation = new Vector3f();
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

	public Mesh getHeadMesh()
	{
		return headMesh;
	}

	public void setHeadMesh(Mesh headMesh)
	{
		//TODO
		this.headMesh = headMesh;

		//TODO
		headMesh.setPosition(headPosition);
		headMesh.setRotation(headRotation);
	}

	public Mesh getBodyMesh()
	{
		return bodyMesh;
	}

	public void setBodyMesh(Mesh bodyMesh)
	{
		//TODO
		this.bodyMesh = bodyMesh;

		//TODO
		bodyMesh.setPosition(bodyPosition);
		bodyMesh.setRotation(bodyRotation);
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;

		headPosition.x = position.x;
		headPosition.y = position.y + 2.0f;
		headPosition.z = position.z;

		bodyPosition.x = position.x;
		bodyPosition.y = position.y + 0.5f;
		bodyPosition.z = position.z;
	}

	public void setRotation(Vector3f rotation)
	{
		this.rotation = rotation;

		headRotation.x = rotation.x;
		headRotation.y = rotation.y;
		headRotation.z = rotation.z;

		bodyRotation.x = 0;
		bodyRotation.y = rotation.y;
		bodyRotation.z = 0;
	}

}
