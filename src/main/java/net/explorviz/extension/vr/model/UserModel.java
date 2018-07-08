package net.explorviz.extension.vr.model;

public class UserModel extends BaseModel {

	private String userName;
	private ControllerModel leftController;
	private ControllerModel rightController;

	public UserModel() {
		leftController = new ControllerModel();
		rightController = new ControllerModel();
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

	public ControllerModel getLeftController() {
		return this.leftController;
	}

	public ControllerModel getRightController() {
		return this.rightController;
	}

}
