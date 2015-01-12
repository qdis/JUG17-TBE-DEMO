package org.tbe.jug.businesslogic.entities.user;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.tbe.jug.businesslogic.common.AbstractEntity;
import org.tbe.jug.businesslogic.entities.group.UserGroup;
import org.tbe.jug.tools.annotations.AutoTest;

@Entity
@AutoTest(persistanceUnit = "test")
public class User extends AbstractEntity {

	private static final long serialVersionUID = -2178114371221842412L;

	@EmbeddedId
	private UserId userId;
	@Column
	private String firstName;
	@Column
	private String lastName;
	@ManyToOne
	private UserGroup assignedGroup;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public UserGroup getAssignedGroup() {
		return assignedGroup;
	}

	public void setAssignedGroup(UserGroup assignedGroup) {
		this.assignedGroup = assignedGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", firstName=" + firstName + ", lastName=" + lastName + ", assignedGroup=" + assignedGroup + "]";
	}

}
