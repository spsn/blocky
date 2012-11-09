package ge.framework.render;
import ge.framework.mesh.BoundingBox;
import ge.framework.profile.Profiler;

public class Frustum
{

	class Plane
	{
		public float x;
		public float y;
		public float z;
		public float d;
	}

	// We create an enum of the sides so we don't have to call each side 0 or 1.
	// This way it makes it more understandable and readable when dealing with
	// frustum sides.
	public static final int RIGHT = 0; // The RIGHT side of the frustum
	public static final int LEFT = 1; // The LEFT side of the frustum
	public static final int BOTTOM = 2; // The BOTTOM side of the frustum
	public static final int TOP = 3; // The TOP side of the frustum
	public static final int BACK = 4; // The BACK side of the frustum
	public static final int FRONT = 5; // The FRONT side of the frustum

	// This holds the X Y Z and D values for each side of our frustum.
	private Plane[] m_Frustum;

	private float[] clip; // This will hold the clipping planes

	/**
	 * Frustum constructor.
	 */
	public Frustum()
	{
		m_Frustum = new Plane[6];
		m_Frustum[RIGHT] = new Plane();
		m_Frustum[LEFT] = new Plane();
		m_Frustum[BOTTOM] = new Plane();
		m_Frustum[TOP] = new Plane();
		m_Frustum[BACK] = new Plane();
		m_Frustum[FRONT] = new Plane();

		clip = new float[16];
	}

	// /////////////////////////////// NORMALIZE PLANE
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
	// ///
	// /// This normalizes a plane (A side) from a given frustum.
	// ///
	// /////////////////////////////// NORMALIZE PLANE
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

	public void normalizePlane(Plane plane)
	{
		// Here we calculate the magnitude of the normal to the plane (point A B
		// C)
		// Remember that (A, B, C) is that same thing as the normal's (X, Y, Z).
		// To calculate magnitude you use the equation: magnitude = sqrt( x^2 +
		// y^2 + z^2)
		float magnitude = (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);

		// Then we divide the plane's values by it's magnitude.
		// This makes it easier to work with.
		plane.x /= magnitude;
		plane.y /= magnitude;
		plane.z /= magnitude;
		plane.d /= magnitude;
	}

	// /////////////////////////////// CALCULATE FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
	// ///
	// /// This extracts our frustum from the projection and modelview matrix.
	// ///
	// /////////////////////////////// CALCULATE FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

	public void calculateFrustum(java.nio.FloatBuffer mvpMatrixBuffer)
	{
		// Local variables
		Plane plane;

		// Get the clipping planes from the MVP matrix
		mvpMatrixBuffer.get(clip);
		mvpMatrixBuffer.flip();

		// Now we actually want to get the sides of the frustum. To do this we
		// take
		// the clipping planes we received above and extract the sides from
		// them.

		// This will extract the RIGHT side of the frustum
		plane = m_Frustum[RIGHT];
		plane.x = clip[3] - clip[0];
		plane.y = clip[7] - clip[4];
		plane.z = clip[11] - clip[8];
		plane.d = clip[15] - clip[12];

		// Normalize the RIGHT side
		normalizePlane(plane);

		// This will extract the LEFT side of the frustum
		plane = m_Frustum[LEFT];
		plane.x = clip[3] + clip[0];
		plane.y = clip[7] + clip[4];
		plane.z = clip[11] + clip[8];
		plane.d = clip[15] + clip[12];

		// Normalize the LEFT side
		normalizePlane(plane);

		// This will extract the BOTTOM side of the frustum
		plane = m_Frustum[BOTTOM];
		plane.x = clip[3] + clip[1];
		plane.y = clip[7] + clip[5];
		plane.z = clip[11] + clip[9];
		plane.d = clip[15] + clip[13];

		// Normalize the BOTTOM side
		normalizePlane(plane);

		// This will extract the TOP side of the frustum
		plane = m_Frustum[TOP];
		plane.x = clip[3] - clip[1];
		plane.y = clip[7] - clip[5];
		plane.z = clip[11] - clip[9];
		plane.d = clip[15] - clip[13];

		// Normalize the TOP side
		normalizePlane(plane);

		// This will extract the BACK side of the frustum
		plane = m_Frustum[BACK];
		plane.x = clip[3] - clip[2];
		plane.y = clip[7] - clip[6];
		plane.z = clip[11] - clip[10];
		plane.d = clip[15] - clip[14];

		// Normalize the BACK side
		normalizePlane(plane);

		// This will extract the FRONT side of the frustum
		plane = m_Frustum[FRONT];
		plane.x = clip[3] + clip[2];
		plane.y = clip[7] + clip[6];
		plane.z = clip[11] + clip[10];
		plane.d = clip[15] + clip[14];

		// Normalize the FRONT side
		normalizePlane(plane);
	}

	// The code below will allow us to make checks within the frustum. For
	// example,
	// if we want to see if a point, a sphere, or a cube lies inside of the
	// frustum.
	// Because all of our planes point INWARDS (The normals are all pointing
	// inside the frustum)
	// we then can assume that if a point is in FRONT of all of the planes, it's
	// inside.

	// /////////////////////////////// POINT IN FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
	// ///
	// /// This determines if a point is inside of the frustum
	// ///
	// /////////////////////////////// POINT IN FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

	public boolean pointInFrustum(float x, float y, float z)
	{
		// Go through all the sides of the frustum
		for (int i = 0; i < 6; i++)
		{
			Plane plane = m_Frustum[i];

			// Calculate the plane equation and check if the point is behind a
			// side of the frustum
			if (plane.x * x + plane.y * y + plane.z * z + plane.d <= 0)
			{
				// The point was behind a side, so it ISN'T in the frustum
				return false;
			}
		}

		// The point was inside of the frustum (In front of ALL the sides of the
		// frustum)
		return true;
	}

	// /////////////////////////////// SPHERE IN FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
	// ///
	// /// This determines if a sphere is inside of our frustum by it's center
	// and radius.
	// ///
	// /////////////////////////////// SPHERE IN FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

	public boolean sphereInFrustum(float x, float y, float z, float radius)
	{
		// Go through all the sides of the frustum
		for (int i = 0; i < 6; i++)
		{
			Plane plane = m_Frustum[i];

			// If the center of the sphere is farther away from the plane than
			// the radius
			if (plane.x * x + plane.y * y + plane.z * z + plane.d <= -radius)
			{
				// The distance was greater than the radius so the sphere is
				// outside of the frustum
				return false;
			}
		}

		// The sphere was inside of the frustum!
		return true;
	}

	// /////////////////////////////// CUBE IN FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*
	// ///
	// /// This determines if a cube is in or around our frustum by it's center
	// and 1/2 it's length
	// ///
	// /////////////////////////////// CUBE IN FRUSTUM
	// \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\*

	public boolean cubeInFrustum(float x, float y, float z, float size)
	{
		// This test is a bit more work, but not too much more complicated.
		// Basically, what is going on is, that we are given the center of the
		// cube,
		// and half the length. Think of it like a radius. Then we checking each
		// point
		// in the cube and seeing if it is inside the frustum. If a point is
		// found in front
		// of a side, then we skip to the next side. If we get to a plane that
		// does NOT have
		// a point in front of it, then it will return false.

		// *Note* - This will sometimes say that a cube is inside the frustum
		// when it isn't.
		// This happens when all the corners of the bounding box are not behind
		// any one plane.
		// This is rare and shouldn't effect the overall rendering speed.

		for (int i = 0; i < 6; i++)
		{
			Plane plane = m_Frustum[i];

			float axm = plane.x * (x - size);
			float bym = plane.y * (y - size);
			float czm = plane.z * (z - size);
			float d = plane.d;

			if (axm + bym + czm + d > 0)
				continue;

			float axp = plane.x * (x + size);

			if (axp + bym + czm + d > 0)
				continue;

			float byp = plane.y * (y + size);

			if (axm + byp + czm + d > 0)
				continue;
			if (axp + byp + czm + d > 0)
				continue;

			float czp = plane.z * (z + size);

			if (axm + bym + czp + d > 0)
				continue;
			if (axp + bym + czp + d > 0)
				continue;
			if (axm + byp + czp + d > 0)
				continue;
			if (axp + byp + czp + d > 0)
				continue;

			// If we get here, it isn't in the frustum
			return false;
		}

		return true;
	}

	public boolean boxInFrustum(BoundingBox b, Profiler profiler)
	{
		return boxInFrustum(b.getMinx(), b.getMiny(), b.getMinz(), b.getMaxx(), b.getMaxy(), b.getMaxz(), profiler);
	}

	public boolean boxInFrustum(float x, float y, float z, float x2, float y2, float z2, Profiler profiler)
	{
		// Local variables
		Plane plane;
		float ax;
		float by;
		float cz;

		//TODO
		plane = m_Frustum[BACK];

		//TODO
		ax = plane.x * ((plane.x < 0) ? x : x2);
		by = plane.y * ((plane.y < 0) ? y : y2);
		cz = plane.z * ((plane.z < 0) ? z : z2);

		//TODO
		if (ax + by + cz + plane.d <= 0)
		{
			return false;
		}

		//TODO
		plane = m_Frustum[FRONT];

		//TODO
		ax = plane.x * ((plane.x < 0) ? x : x2);
		by = plane.y * ((plane.y < 0) ? y : y2);
		cz = plane.z * ((plane.z < 0) ? z : z2);

		//TODO
		if (ax + by + cz + plane.d <= 0)
		{
			return false;
		}

		//TODO
		plane = m_Frustum[RIGHT];

		//TODO
		ax = plane.x * ((plane.x < 0) ? x : x2);
		by = plane.y * ((plane.y < 0) ? y : y2);
		cz = plane.z * ((plane.z < 0) ? z : z2);

		//TODO
		if (ax + by + cz + plane.d <= 0)
		{
			return false;
		}

		//TODO
		plane = m_Frustum[LEFT];

		//TODO
		ax = plane.x * ((plane.x < 0) ? x : x2);
		by = plane.y * ((plane.y < 0) ? y : y2);
		cz = plane.z * ((plane.z < 0) ? z : z2);

		//TODO
		if (ax + by + cz + plane.d <= 0)
		{
			return false;
		}

		//TODO
		plane = m_Frustum[BOTTOM];

		//TODO
		ax = plane.x * ((plane.x < 0) ? x : x2);
		by = plane.y * ((plane.y < 0) ? y : y2);
		cz = plane.z * ((plane.z < 0) ? z : z2);

		//TODO
		if (ax + by + cz + plane.d <= 0)
		{
			return false;
		}

		//TODO
		plane = m_Frustum[TOP];

		//TODO
		ax = plane.x * ((plane.x < 0) ? x : x2);
		by = plane.y * ((plane.y < 0) ? y : y2);
		cz = plane.z * ((plane.z < 0) ? z : z2);

		//TODO
		if (ax + by + cz + plane.d <= 0)
		{
			return false;
		}

		// Return a true for the box being inside of the frustum
		return true;
	}

}
