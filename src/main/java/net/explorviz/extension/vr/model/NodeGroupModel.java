package net.explorviz.extension.vr.model;

public class NodeGroupModel extends BaseModel {

	private boolean opened;

	public NodeGroupModel() {
	}

	boolean isOpened() {
		return opened;
	}

	void setOpened(final boolean opened) {
		this.opened = opened;
	}

}
