package net.explorviz.extension.vr.model;

import java.util.List;

public class SystemModel extends BaseModel {

	private final List<NodeGroupModel> nodeGroups;

	private boolean opened = true;

	public SystemModel(final List<NodeGroupModel> nodeGroups) {
		this.nodeGroups = nodeGroups;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(final boolean opened) {
		this.opened = opened;
	}

	public List<NodeGroupModel> getNodeGroups() {
		return nodeGroups;
	}

}
