package org.tbe.jug.businesslogic.entities.group;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.tbe.jug.businesslogic.common.AbstractEntity;
import org.tbe.jug.businesslogic.entities.user.User;
import org.tbe.jug.tools.annotations.AutoTest;

@Entity
@AutoTest(persistanceUnit = "test")
public class UserGroup extends AbstractEntity {

	private static final long serialVersionUID = -6646772109289407543L;

	@Id
	@Column
	private int groupId;
	@Column
	private String groupName;
	@OneToMany(mappedBy = "assignedGroup" , cascade=CascadeType.ALL)
	private List<User> users;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupId;
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
		UserGroup other = (UserGroup) obj;
		if (groupId != other.groupId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Group [groupId=" + groupId + ", groupName=" + groupName + ", users=" + users + "]";
	}

}
