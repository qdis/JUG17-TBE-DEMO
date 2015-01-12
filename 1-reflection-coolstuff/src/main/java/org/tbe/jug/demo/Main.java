package org.tbe.jug.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tbe.jug.demo.pojos.Group;
import org.tbe.jug.demo.pojos.Level;
import org.tbe.jug.demo.pojos.User;

public class Main {

	public static void main(String args[]) {

		User owner1 = new User("Timo", "Bejan", 25);
		User owner2 = new User("Someone", "Else", 25);

		final Group g1 = new Group("Group 1", Level.BEGGINNER);
		g1.setGroupOwner(owner1);
		final Group g2 = new Group("Group 2", Level.BEGGINNER);
		g2.setGroupOwner(owner1);
		final Group g3 = new Group("Group 3", Level.INTERMEDIATE);
		g3.setGroupOwner(owner1);
		final Group g4 = new Group("Group 4", Level.PRO);
		g4.setGroupOwner(owner2);
		final Group g5 = new Group("Group 5", Level.PRO);
		g5.setGroupOwner(owner2);

		@SuppressWarnings("serial")
		List<Group> groups = new ArrayList<Group>() {
			{
				add(g1);
				add(g2);
				add(g3);
				add(g4);
				add(g5);
			}
		};

		Map<String, Group> groupedById = ReflectionUtils.createGenericMap(String.class, Group.class, groups, "id");

		System.out.println(groupedById.toString().replace(",", "\n") + "\n");
		Map<Level, List<Group>> groupByLevel = ReflectionUtils.createAccumulatedMap(Level.class, Group.class, groups, "level");

		System.out.println(groupByLevel.toString().replace(",", "\n") + "\n");

		Map<String, List<Group>> groupByOwnerName = ReflectionUtils.createAccumulatedMap(String.class, Group.class, groups, "groupOwner.firstName");

		System.out.println(groupByOwnerName.toString().replace(",", "\n") + "\n");

	}
}
