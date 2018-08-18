package net.explorviz.extension.vr.model;

import java.awt.Color;

public class UserModel extends BaseModel {

	private String userName;
	private final ControllerModel controller1;
	private final ControllerModel controller2;
	private String state;
	private byte color = -1;
	private static final Color[] colors = { new Color(255, 0, 0), // red
			new Color(0, 117, 242), // blue
			new Color(219, 208, 0), // yellow
			new Color(0, 209, 188), // turquoise
			new Color(209, 0, 209), // pink
			new Color(144, 0, 206), // purple
			new Color(0, 175, 206), // ocean blue
			new Color(241, 141, 0), // orange

	};
	private static byte[] assignedColors = new byte[colors.length];

	public Color getColor() {
		return colors[color];
	}

	public void removeColor() {
		if (color != -1)
			synchronized (assignedColors) {
				assignedColors[color]--;
				color = -1;
			}
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public UserModel() {
		controller1 = new ControllerModel();
		controller2 = new ControllerModel();
		assignColor();
	}

	private void assignColor() {
		synchronized (assignedColors) {
			byte bestChoice = 0;
			for (byte i = 0; i < assignedColors.length; i++) {
				if (assignedColors[i] == 0) { // no user has color i yet
					bestChoice = i;
					break;
				} else if (assignedColors[i] < assignedColors[bestChoice]) { // color i is less often assigned than
																				// color bestChoice
					bestChoice = i;
				}
			}
			// assign best color choice
			assignedColors[bestChoice]++;
			color = bestChoice;
		}
	}

	public UserModel(final String userName) {
		this.userName = userName;
		controller1 = new ControllerModel();
		controller2 = new ControllerModel();
		assignColor();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public ControllerModel getController1() {
		return this.controller1;
	}

	public ControllerModel getController2() {
		return this.controller2;
	}

}
