package net.explorviz.extension.vr.model;

public class HighlightingModel {

	private final String highlightedApp;
	private final String highlightedEntity;
	private final String originalColor;

	public HighlightingModel(final boolean isHighlighted, final String appID, final String entityID,
			final String originalColor) {
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

	public String getOriginalColor() {
		return originalColor;
	}

}
