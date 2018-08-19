package net.explorviz.extension.vr.model;

import java.awt.Color;
import java.util.Random;

public class UserModel extends BaseModel {

	private String userName;
	private final ControllerModel controller1;
	private final ControllerModel controller2;
	private String state;
	private final Color color;
	private long timeOfLastMessage;

	public Color getColor() {
		return color;
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
		color = getRandomColor();
	}

	private Color getRandomColor() {
		final Random ran = new Random();
		final int x = ran.nextInt(3);

		final int keep = ran.nextInt(3);
		final float red = ran.nextFloat();
		final float green = ran.nextFloat();
		final float blue = ran.nextFloat();
		final Color c = new Color(red, green, blue);
		return c;
	}

	public UserModel(final String userName) {
		this.userName = userName;
		controller1 = new ControllerModel();
		controller2 = new ControllerModel();
		color = getRandomColor();
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

	public long getTimeOfLastMessage() {
		return this.timeOfLastMessage;
	}

	public void setTimeOfLastMessage(final long time) {
		this.timeOfLastMessage = time;
	}

}
