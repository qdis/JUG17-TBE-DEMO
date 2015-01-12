package org.tbe.jug.businesslogic.entities.user;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.tbe.jug.businesslogic.common.AbstractId;

@Embeddable
public class UserId extends AbstractId {

	private static final long serialVersionUID = 7392341791864621032L;

	@Column
	private String email;
	@Column
	private int internalId;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getInternalId() {
		return internalId;
	}

	public void setInternalId(int internalId) {
		this.internalId = internalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + internalId;
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
		UserId other = (UserId) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (internalId != other.internalId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserId [email=" + email + ", internalId=" + internalId + "]";
	}

}