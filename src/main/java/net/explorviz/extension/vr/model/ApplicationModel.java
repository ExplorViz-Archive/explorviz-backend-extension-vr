package net.explorviz.extension.vr.model;

public class ApplicationModel extends BaseModel {

	float xPos, yPos, zPos;
	float xQuat, yQuat, zQuat, wQuat;
	boolean isOpen;

	public ApplicationModel() {

	}

	public float[] getPosition() {
		final float[] coordinates = { xPos, yPos, zPos };
		return coordinates;
	}

	public void setPosition(final float[] coordinates) {
		this.xPos = coordinates[0];
		this.yPos = coordinates[1];
		this.zPos = coordinates[2];
	}

	public float[] getQuaternion() {
		final float[] quaternion = { xQuat, yQuat, zQuat, zQuat };
		return quaternion;
	}

	public void setQuaternion(final float[] quaternion) {
		this.xQuat = quaternion[0];
		this.yQuat = quaternion[1];
		this.zQuat = quaternion[2];
		this.wQuat = quaternion[3];
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public void setOpen(final boolean isOpen) {
		this.isOpen = isOpen;
	}

}
