package org.tbe.jug.businesslogic.entities.message;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.tbe.jug.businesslogic.common.AbstractEntity;
import org.tbe.jug.tools.annotations.AutoTest;

@Entity
@AutoTest(persistanceUnit="test")
public class Message extends AbstractEntity {

	private static final long serialVersionUID = 2281232706018398291L;

	@Id
	@Column
	private int messageId;
	@Column
	private String value;

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + messageId;
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
		Message other = (Message) obj;
		if (messageId != other.messageId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", value=" + value + "]";
	}

}
