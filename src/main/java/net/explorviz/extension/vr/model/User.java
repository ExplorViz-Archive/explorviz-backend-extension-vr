package net.explorviz.extension.vr.model;

public class User extends BaseModel {

	private String userName;

	public User() {
	}

	public User(final String dummyName) {
		this.userName = dummyName;
	}

	public String getDummyName() {
		return userName;
	}

	public void setDummyName(final String dummyName) {
		this.userName = dummyName;
	}

}
