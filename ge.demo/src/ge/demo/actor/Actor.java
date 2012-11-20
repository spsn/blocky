package ge.demo.actor;

import org.lwjgl.util.vector.Vector3f;

import ge.framework.mesh.Mesh;

public abstract class Actor
{
	//TODO
	private Mesh mesh;

	//TODO
	protected Vector3f position;

	//TODO
	protected Vector3f rotation;

	//TODO
	protected int action;

	//TODO
	protected float amount;

	//TODO
	protected float duration;

	//TODO
	public Actor()
	{
	}

	public abstract void draw(Mesh mesh);

	public abstract void act(long delta);

	public Mesh getMesh()
	{
		return mesh;
	}

	public void setMesh(Mesh mesh)
	{
		//TODO
		this.mesh = mesh;

		//TODO
		mesh.setPosition(position);
		mesh.setRotation(rotation);
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public void setRotation(Vector3f rotation) {
		this.rotation = rotation;
	}

}
