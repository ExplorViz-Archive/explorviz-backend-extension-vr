package net.explorviz.extension.vr.model;

import java.util.ArrayList;

public class ApplicationModel extends BaseModel {

	boolean isOpen;
	boolean isBound;
	String boundByUser;

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

	public void setBound(final boolean isBound) {
		this.isBound = isBound;
	}

	public boolean isBound() {
		return isBound;
	}

	public void setBoundByUser(final String userID) {
		this.boundByUser = userID;
	}

	public void setUnboundByUser(final String userID) {
		if (isBoundByUser(userID)) {
			setBound(false);
		}
	}

	public boolean isBoundByUser(final String userID) {
		if (isBound && userID == boundByUser) {
			return true;
		} else {
			return false;
		}
	}

}
