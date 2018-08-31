package net.explorviz.extension.vr.model;

public class HighlightingModel {

	private final String highlightedApp;
	private final String highlightedEntity;
	private final long originalColor;

	public HighlightingModel(final boolean isHighlighted, final String appID, final String entityID,
			final long originalColor) {
		this.highlightedApp = appID;
		this.highlightedEntity = entityID;
		this.originalColor = originalColor;
	}

	public String getHighlightedApp() {
		return highlightedApp;
	}

	public String getHighlightedEntity() {
		return highlightedEntity;
	}

	public long getOriginalColor() {
		return originalColor;
	}

}
