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

	public boolean isOpen() {
		return this.isOpen;
	}

	public void setOpen(final boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void openComponent(final Long id) {
		openComponents.add(id);
	}

	public void closeComponent(final Long id) {
		openComponents.remove(id);
	}

	public ArrayList<Long> getOpenComponents() {
		return openComponents;
	}

}
