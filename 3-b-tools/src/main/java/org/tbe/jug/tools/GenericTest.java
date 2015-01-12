package org.tbe.jug.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;
import org.tbe.jug.tools.annotations.InternalTest;
import org.tbe.jug.tools.enums.TestType;

public abstract class GenericTest<ENTITY, PKEY> {

	protected static Date temporalValueDefault;

	protected static Integer integerValueDefault;

	protected static String stringValueDefault;

	protected static Long longValueDefault;

	protected static Date temporalValueUpdate;

	protected static Integer integerValueUpdate;

	protected static String stringValueUpdate;

	protected static Long longValueUpdate;

	private EntityManagerFactory factory;
	private EntityManager em;

	private Set<TestType> testsToRun = new LinkedHashSet<>();
	private Set<String> ignorableFields = new LinkedHashSet<>();

	@Before
	public void initialize() {
		try {
			InternalTest autoTest = this.getClass().getAnnotation(InternalTest.class);
			testsToRun.addAll(Arrays.asList(autoTest.testTypes()));
			ignorableFields.addAll(Arrays.asList(autoTest.ignorableFields()));
			factory = Persistence.createEntityManagerFactory(autoTest.persistanceUnit());
			em = factory.createEntityManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initializeValues() {
		Calendar calendar = Calendar.getInstance();
		int[] fields = new int[] { Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND, Calendar.MILLISECOND };
		for (int f : fields) {
			calendar.set(f, calendar.getMinimum(f));
		}

		temporalValueDefault = new Date((System.currentTimeMillis() / 1000) * 1000);
		integerValueDefault = 1;
		longValueDefault = 1l;
		stringValueDefault = "A";

		temporalValueUpdate = new Date((System.currentTimeMillis() / 1000) * 1000);
		integerValueUpdate = 2;
		longValueUpdate = 2l;
		stringValueUpdate = "B";

	}

	private void persist(ENTITY entity) {
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
	}

	private void update(PKEY key, ENTITY entity) {
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
	}

	private void compare(ENTITY e1, ENTITY e2) {
		assertEquals(e1, e2);
	}

	private ArrayList<Field> getAllClassFields(Class<?> clazz) {
		final Field[] classFields = clazz.getDeclaredFields();
		final ArrayList<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(classFields));
		Class<?> superClass = clazz.getSuperclass();
		while (superClass != null) {
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			superClass = superClass.getSuperclass();
		}
		return fields;
	}

	private void compareReflective(ENTITY e1, ENTITY e2) {
		final ArrayList<Field> fields = getAllClassFields(e1.getClass());
		for (Field f : fields) {
			if (!Modifier.isFinal(f.getModifiers()) && (f.getAnnotation(EmbeddedId.class) != null || f.getAnnotation(Column.class) != null)) {
				if (ignorableFields.contains(f.getName())) {
					continue;
				}
				f.setAccessible(true);
				Object e1Value = null;
				Object e2Value = null;
				try {
					e1Value = f.get(e1);
					e2Value = f.get(e2);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				if (e1Value != null && e2Value != null) {
					if (f.getType() == byte[].class) {
						e1Value = Arrays.toString((byte[]) e1Value);
						e2Value = Arrays.toString((byte[]) e2Value);
					}
					if (e1Value.equals(e2Value)) {
						continue;
					} else {
						throw new RuntimeException("Field values don't match : " + "\n ORIGINAL       : " + e1 + "\n FOUND          : "
								+ e2 + "\n FIELD NAME     : " + f.getName() + "\n ORIGINAL VALUE : " + e1Value + "\n FOUND VALUE    : "
								+ e2Value);
					}
				} else {
					continue;
				}
			}
		}

	}

	private void delete(PKEY key, ENTITY entity) {
		em.getTransaction().begin();
		em.remove(entity);
		em.getTransaction().commit();
	}

	@SuppressWarnings("unchecked")
	private ENTITY find(PKEY key, ENTITY entity) {
		return (ENTITY) em.find(entity.getClass(), key);
	}

	@Test
	public void testCRUD() {
		initializeValues();
		PKEY filledPkey = null;
		ENTITY filledEntity = null;
		try {
			// CREATE
			filledPkey = fillPKey();
			filledEntity = fillEntity(filledPkey, false);

			if (testsToRun.contains(TestType.CREATE)) {
				System.out.println("Running CRUD UnitTest for : " + filledEntity.getClass() + " " + filledEntity);
				System.out.println("Creating ... ");
				persist(filledEntity);
			}

			if (testsToRun.contains(TestType.READ)) {
				System.out.println("Lookup ... ");
				// READ
				ENTITY found = find(filledPkey, filledEntity);
				compareReflective(filledEntity, found);
			}

			ENTITY updated = fillEntity(filledPkey, true);
			if (testsToRun.contains(TestType.UPDATE)) {
				System.out.println("Update ... ");
				// UPDATE
				update(filledPkey, updated);

				if (testsToRun.contains(TestType.READ)) {
					System.out.println("Lookup ...");
					// READ
					ENTITY updateFound = find(filledPkey, filledEntity);
					compareReflective(updated, updateFound);
				}
			}

			if (testsToRun.contains(TestType.DELETE)) {
				System.out.println("Delete ...");
				// DELETE
				delete(filledPkey, filledEntity);
				ENTITY foundAfterDelete = find(filledPkey, filledEntity);
				compare(foundAfterDelete, null);
			}

			System.out.println("Done CRUD UnitTest for : " + filledEntity.getClass());
		} catch (Exception e) {
			e.printStackTrace();
			delete(filledPkey, filledEntity);
			fail();
		}
	}

	public void updateFields(Object object, PKEY id, ArrayList<Field> fields, boolean isUpdate) throws IllegalArgumentException,
			IllegalAccessException {
		for (Field f : fields) {
			// remove silly private modifier :)
			f.setAccessible(true);
		}
		for (Field f : fields) {
			if (!Modifier.isFinal(f.getModifiers())
					&& (f.getAnnotation(EmbeddedId.class) != null || f.getAnnotation(Id.class) != null
							|| f.getAnnotation(Column.class) != null || f.getAnnotation(OneToMany.class) != null || f
							.getAnnotation(ManyToOne.class) != null)) {
				Class<?> type = f.getType();
				if (type.isAssignableFrom(Date.class)) {
					if (isUpdate && f.getAnnotation(EmbeddedId.class) == null && f.getAnnotation(Id.class) == null) {
						f.set(object, temporalValueUpdate);
					} else {
						f.set(object, temporalValueDefault);
					}
				} else if (type.isAssignableFrom(Integer.class)) {
					if (isUpdate && f.getAnnotation(EmbeddedId.class) == null && f.getAnnotation(Id.class) == null) {
						f.set(object, integerValueUpdate);
					} else {
						f.set(object, integerValueDefault);
					}
				} else if (type.isAssignableFrom(String.class)) {
					if (isUpdate && f.getAnnotation(EmbeddedId.class) == null && f.getAnnotation(Id.class) == null) {
						f.set(object, stringValueUpdate);
					} else {
						f.set(object, stringValueDefault);
					}
				} else if (type.isAssignableFrom(Long.class)) {
					if (isUpdate && f.getAnnotation(EmbeddedId.class) == null && f.getAnnotation(Id.class) == null) {
						f.set(object, longValueUpdate);
					} else {
						f.set(object, longValueDefault);
					}
				} else {

					String typeString = f.getType().toString();
					if (typeString.equalsIgnoreCase("int")) {
						if (isUpdate && f.getAnnotation(EmbeddedId.class) == null && f.getAnnotation(Id.class) == null) {
							f.set(object, integerValueUpdate);
						} else {
							f.set(object, integerValueDefault);
						}
					} else if (typeString.equalsIgnoreCase("long")) {
						if (isUpdate && f.getAnnotation(EmbeddedId.class) == null && f.getAnnotation(Id.class) == null) {
							f.set(object, longValueUpdate);
						} else {
							f.set(object, longValueDefault);
						}
					} else {

						if (f.getAnnotation(EmbeddedId.class) != null || f.getAnnotation(Id.class) != null) {
							f.set(object, id);
						} else {
							if (type.isAssignableFrom(List.class)) {
								f.set(object, new ArrayList<>());
							} else {
								Object instance;
								try {
									instance = f.getType().getConstructor().newInstance();
									f.set(object, instance);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}

				}
			}
		}
	}

	private ENTITY fillEntity(PKEY pKey, boolean isUpdate) throws IllegalArgumentException, IllegalAccessException {
		ENTITY entity = createNewEntity();

		Class<?> clazz = entity.getClass();
		// get all fields, including privates
		ArrayList<Field> fields = getAllClassFields(clazz);
		updateFields(entity, pKey, fields, isUpdate);
		return entity;
	}

	private PKEY fillPKey() throws IllegalArgumentException, IllegalAccessException {
		PKEY pKey = createNewPK();
		Class<?> clazz = pKey.getClass();
		// get all fields, including privates
		ArrayList<Field> fields = getAllClassFields(clazz);
		updateFields(pKey, null, fields, false);

		return pKey;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private ENTITY createNewEntity() {
		Object instance = null;
		try {
			instance = ((Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (ENTITY) instance;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PKEY createNewPK() {
		Object instance = null;
		try {
			Class clazz = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
			if (clazz.equals(Integer.class)) {
				return (PKEY) integerValueDefault;
			}
			if (clazz.equals(String.class)) {
				return (PKEY) integerValueDefault;
			}
			if (clazz.equals(Double.class)) {
				return (PKEY) integerValueDefault;
			}
			if (clazz.equals(Long.class)) {
				return (PKEY) integerValueDefault;
			}
			instance = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (PKEY) instance;
	}

}
