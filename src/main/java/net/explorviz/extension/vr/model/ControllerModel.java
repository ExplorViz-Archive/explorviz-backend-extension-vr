package net.explorviz.extension.vr.model;

public class ControllerModel {

	private float x, y, z;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

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
