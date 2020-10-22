package net.explorviz.extension.vr.model;

import java.util.ArrayList;

public class ApplicationModel extends BaseModel {

	boolean isOpen;
	boolean isGrabbed;
	String grabbedByUser;

	private final ArrayList<String> openComponents;

	public ApplicationModel() {
		openComponents = new ArrayList<>();
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public void setOpen(final boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void openComponent(final String id) {
		openComponents.add(id);
	}

	public void closeComponent(final String id) {
		openComponents.remove(id);
	}

	public void closeAllComponents() {
		openComponents.clear();
	}

	public ArrayList<String> getOpenComponents() {
		return openComponents;
	}

	public void setGrabbed(final boolean isGrabbed) {
		this.isGrabbed = isGrabbed;
	}

	public boolean isGrabbed() {
		return isGrabbed;
	}

	public void setGrabbedByUser(final String userID) {
		this.grabbedByUser = userID;
	}

	public void setUnboundByUser(final String userID) {
		if (this.isGrabbedByUser(userID)) {
			this.setGrabbed(false);
		}
	}

	public boolean isGrabbedByUser(final String userID) {
		if (this.isGrabbed && userID == this.grabbedByUser) {
			return true;
		} else {
			return false;
		}
	}

}
