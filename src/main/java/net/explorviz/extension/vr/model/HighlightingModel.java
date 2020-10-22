package net.explorviz.extension.vr.model;

public class HighlightingModel {

	private final String highlightedApp;
	private final String highlightedEntity;
	private final String entityType;

	public HighlightingModel(final boolean isHighlighted, final String appID, final String entityID,
			final String entityType) {
		this.highlightedApp = appID;
		this.highlightedEntity = entityID;
		this.entityType = entityType;
	}

	public String getHighlightedApp() {
		return this.highlightedApp;
	}

	public String getHighlightedEntity() {
		return this.highlightedEntity;
	}

	public String getEntityType() {
		return this.entityType;
	}

}
