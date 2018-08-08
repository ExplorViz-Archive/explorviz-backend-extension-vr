package net.explorviz.extension.vr.model;

import java.util.ArrayList;

public class ApplicationModel extends BaseModel {

	boolean isOpen;
	boolean isBound;
	Long boundByUser;

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

	public void setBound(final boolean isBound) {
		this.isBound = isBound;
	}

	public boolean isBound() {
		return isBound;
	}

	public void setBoundByUser(final Long userID) {
		this.boundByUser = userID;
	}

	public boolean isBoundByUser(final Long userID) {
		if (isBound && userID == boundByUser) {
			return true;
		} else {
			return false;
		}
	}

}
