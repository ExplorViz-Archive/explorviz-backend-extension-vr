package net.explorviz.extension.vr.model;

public class ControllerModel {

	private float x, y, z;

	public ControllerModel() {
	}

	public void setPosition(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float[] getPosition() {
		return new float[] { x, y, z };
	}

}
