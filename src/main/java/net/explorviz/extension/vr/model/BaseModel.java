package net.explorviz.extension.vr.model;

import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.github.jasminb.jsonapi.LongIdHandler;
import com.github.jasminb.jsonapi.annotations.Id;

//Needed for cyclical serialization
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class, property = "id")
public class BaseModel {

	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	// position data
	private float xPos, yPos, zPos;
	private float xQuat, yQuat, zQuat, wQuat;

	@Id(LongIdHandler.class)
	private Long id;

	public BaseModel() {
		id = ID_GENERATOR.incrementAndGet();
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public float[] getPosition() {
		final float[] coordinates = { xPos, yPos, zPos };
		return coordinates;
	}

	public void setPosition(final float[] coordinates) {
		this.xPos = coordinates[0];
		this.yPos = coordinates[1];
		this.zPos = coordinates[2];
	}

	public void setPosition(final float x, final float y, final float z) {
		this.xPos = x;
		this.yPos = y;
		this.zPos = z;
	}

	public float[] getQuaternion() {
		final float[] quaternion = { xQuat, yQuat, zQuat, wQuat };
		return quaternion;
	}

	public void setQuaternion(final float[] quaternion) {
		this.xQuat = quaternion[0];
		this.yQuat = quaternion[1];
		this.zQuat = quaternion[2];
		this.wQuat = quaternion[3];
	}

	public void setQuaternion(final float xQuat, final float yQuat, final float zQuat, final float wQuat) {
		this.xQuat = xQuat;
		this.yQuat = yQuat;
		this.zQuat = zQuat;
		this.wQuat = wQuat;
	}

}
