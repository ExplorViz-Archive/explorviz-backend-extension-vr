package net.explorviz.extension.vr.model;

public class UserModel extends BaseModel {

	private String userName;

	public UserModel() {
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

}
