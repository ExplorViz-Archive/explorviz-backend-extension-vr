package net.explorviz.extension.vr.model;

public class SystemModel {

	private final NodeGroupModel[] nodeGroups;

	private boolean opened;

	SystemModel(final NodeGroupModel[] nodeGroups) {
		this.nodeGroups = nodeGroups;
	}

	boolean isOpened() {
		return opened;
	}

	void setOpened(final boolean opened) {
		this.opened = opened;
	}

	NodeGroupModel[] getNodeGroups() {
		return nodeGroups;
	}

}
