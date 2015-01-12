package org.tbe.jug.demo.pojos;


public class Group {

	private String id;
	private Level level;
	private User groupOwner;

	public Group(String id, Level level) {
		super();
		this.id = id;
		this.level = level;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public User getGroupOwner() {
		return groupOwner;
	}

	public void setGroupOwner(User groupOwner) {
		this.groupOwner = groupOwner;
	}

	@Override
	public String toString() {
		return "Group [id=" + id + ", level=" + level + ", groupOwner=" + groupOwner + "]";
	}

}
