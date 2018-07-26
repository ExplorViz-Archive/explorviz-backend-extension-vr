package net.explorviz.extension.vr.model;

public class UserModel extends BaseModel {

	private String userName;
	private ControllerModel controller1;
	private ControllerModel controller2;
	private String state;

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public UserModel() {
		controller1 = new ControllerModel();
		controller2 = new ControllerModel();
	}

	public UserModel(final String userName) {
		this.userName = userName;
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
