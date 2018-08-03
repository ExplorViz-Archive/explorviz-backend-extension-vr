package net.explorviz.extension.vr.model;

import java.util.ArrayList;

public class ApplicationModel extends BaseModel {

	float xPos, yPos, zPos;
	float xQuat, yQuat, zQuat, wQuat;
	boolean isOpen;
	private final ArrayList<Long> openComponents;

	public ApplicationModel() {
		openComponents = new ArrayList<>();
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
		final float[] quaternion = { xQuat, yQuat, zQuat, wQuat };
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

	public void addOpenComponent(final Long id) {
		openComponents.add(id);
	}

	public ArrayList<Long> getOpenComponents() {
		return openComponents;
	}

}
