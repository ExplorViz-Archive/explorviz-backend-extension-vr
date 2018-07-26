package net.explorviz.extension.vr.model;

import java.util.List;

public class SystemModel extends BaseModel {

	private final List<NodeGroupModel> nodeGroups;

	private boolean opened = true;

	public SystemModel(final List<NodeGroupModel> nodeGroups) {
		this.nodeGroups = nodeGroups;
	}

	boolean isOpened() {
		return opened;
	}

	void setOpened(final boolean opened) {
		this.opened = opened;
	}

	List<NodeGroupModel> getNodeGroups() {
		return nodeGroups;
	}

}
