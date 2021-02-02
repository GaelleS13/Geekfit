package fr.uha.dao;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import fr.uha.models.Challenge;
import fr.uha.models.CrossingPoint;
import fr.uha.models.Obstacle;
import fr.uha.models.Segment;
import fr.uha.models.Suggestion;
import fr.uha.models.User;

public class DAO implements ServletContextListener {

	@PersistenceUnit(unitName = "geekfit")
	private static EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("geekfit");

	@PersistenceContext(unitName = "geekfit")
	private static EntityManager em;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		emfactory.close();
	}

	@Transactional(dontRollbackOn = Exception.class)
	private static EntityManager getEntityManager() {
		em = emfactory.createEntityManager();
		return em;
	}

	// _________________________________ JSON _________________________________ //

	@SuppressWarnings("rawtypes")
	public static String arrayToJson(List list) {
		String result = "{";
		for (Object obj : list) {
			result += obj.toString();
		}
		result += "}";
		return result;
	}

	// ______________________________ CHALLENGES ______________________________ //

	public static List<Challenge> getChall() {
		EntityManager entitymanager = getEntityManager();
		List<Challenge> chals = entitymanager.createQuery("from Challenge c", Challenge.class).getResultList();
		entitymanager.close();
		return chals;
	}

	public static Challenge getChall(int id) {
		for (Challenge chall : getChall()) {
			if (chall.getChallengeId() == id) {
				return chall;
			}
		}
		return null;
	}

	public static void addAdmin(int id, int userId)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge challenge = getChall(id);
		challenge.addAdmin(userId);
		transaction.commit();
		entitymanager.close();
	}

	public static void delAdmin(int id, int userId)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge challenge = getChall(id);
		challenge.delAdmin(userId);
		transaction.commit();
		entitymanager.close();
	}

	public static void addChall(Challenge challenge)
			throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		entitymanager.persist(challenge);
		transaction.commit();
		entitymanager.close();
	}

	public static void delChall(int id)
			throws NotSupportedException, SystemException, NamingException, SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge chall = getChall(id);
		if (!entitymanager.contains(chall)) {
			chall = entitymanager.merge(chall);
		}
		entitymanager.remove(chall);
		transaction.commit();
		entitymanager.close();

	}

	public static ArrayList<Challenge> challIdsToChall(ArrayList<Integer> cids) {
		ArrayList<Challenge> listChall = new ArrayList<Challenge>();
		for (int challId : cids) {
			for (Challenge chal : getChall()) {
				if (chal.getChallengeId() == challId)
					listChall.add(chal);
			}
		}
		return listChall;
	}

	public static void addDescription(int id, String descritpion)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge challenge = getChall(id);
		challenge.setDescription(descritpion);
		transaction.commit();
		entitymanager.close();
	}

	// _________________________________ USERS _________________________________ //

	public static List<User> getUsers() {
		EntityManager entitymanager = getEntityManager();
		List<User> users = entitymanager.createQuery("from User u", User.class).getResultList();
		entitymanager.close();
		return users;
	}

	public static boolean existingUser(String username) {
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	public static User getUser(String username) {
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	public static void addUser(User user)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		entitymanager.persist(user);
		transaction.commit();
		entitymanager.close();
	}

	public static void delUser(User user)
			throws NotSupportedException, SystemException, NamingException, SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		if (!entitymanager.contains(user)) {
			user = entitymanager.merge(user);
		}
		entitymanager.remove(user);
		transaction.commit();
		entitymanager.close();
	}

	public static void setPassword(String username, String password)
			throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		getUser(username).setPassword(password);
		transaction.commit();
		entitymanager.close();
	}

	public static void addUserToChall(String username, int id)
			throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		getUser(username).addChallenge(id);
		transaction.commit();
		entitymanager.close();
	}

	public static void setMode(int id, int mode)
			throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge challenge = getChall(id);
		challenge.setMode(mode);
		transaction.commit();
		entitymanager.close();
	}

	// ______________________________ SUGGESTIONS ______________________________ //

	public static List<Suggestion> getSuggestion() {
		EntityManager entitymanager = getEntityManager();
		List<Suggestion> sugs = entitymanager.createQuery("from Suggestion s", Suggestion.class).getResultList();
		entitymanager.close();
		return sugs;
	}

	public static Suggestion getSuggestion(int id) {
		for (Suggestion suggestion : getSuggestion()) {
			if (suggestion.getSuggestionId() == id) {
				return suggestion;
			}
		}
		return null;
	}

	public static void addSugg(Suggestion suggestion)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		entitymanager.persist(suggestion);
		transaction.commit();
		entitymanager.close();
	}

	public static void delSugg(Suggestion suggestion)
			throws NotSupportedException, SystemException, NamingException, SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		if (!entitymanager.contains(suggestion)) {
			suggestion = entitymanager.merge(suggestion);
		}
		entitymanager.remove(suggestion);
		transaction.commit();
		entitymanager.close();
	}

	public static void addVoteYes(int id)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Suggestion suggestion = getSuggestion(id);
		suggestion.addVoteYes();
		transaction.commit();
		entitymanager.close();
	}

	public static void addVoteNo(int id)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Suggestion suggestion = getSuggestion(id);
		suggestion.addVoteNo();
		transaction.commit();
		entitymanager.close();
	}

	public static void setValidated(int id, boolean bool)
			throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Suggestion suggestion = getSuggestion(id);
		suggestion.setValidated(bool);
		transaction.commit();
		entitymanager.close();
	}

	// _______________________________ CROSSPOINT _______________________________ //

	public static CrossingPoint getCrossPoint(int id, int crossPointId) {
		for (CrossingPoint crossPoint : getChall(id).getCrossingPoints()) {
			if (crossPoint.getCrossingPointId() == crossPointId) {
				return crossPoint;
			}
		}
		return null;
	}

	public static void addCrossPoint(int id, CrossingPoint crossingPoint)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge challenge = getChall(id);
		challenge.addCrossingPoint(crossingPoint);
		transaction.commit();
		entitymanager.close();
	}

	// _______________________________ SEGMENTS _______________________________ //

	public static Segment getSegment(int id, int segmentId) {
		for (Segment segment : getChall(id).getSegments()) {
			if (segment.getSegmentId() == id) {
				return segment;
			}
		}
		return null;
	}

	public static void addSegment(int id, Segment segment)
			throws NamingException, NotSupportedException, SystemException, SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Challenge challenge = getChall(id);
		challenge.addSegment(segment);
		transaction.commit();
		entitymanager.close();
	}

	public static void delSegment(int id, int segmentId)
			throws NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		getChall(id).delSegment(segmentId);
		transaction.commit();
		entitymanager.close();
	}

	// _______________________________ OBSTACLES _______________________________ //

	public static void addObstacle(int id, int segmentId, Obstacle obstacle)
			throws NamingException, NotSupportedException, SystemException, SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		Segment segment = getChall(id).getSegment(segmentId);
		segment.setObstacle(obstacle);
		transaction.commit();
		entitymanager.close();
	}

	public static void delObstacle(int id, int segmentId)
			throws NamingException, NotSupportedException, SystemException, SecurityException, IllegalStateException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		UserTransaction transaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		transaction.begin();
		EntityManager entitymanager = getEntityManager();
		getChall(id).getSegment(segmentId).setObstacle(null);
		transaction.commit();
		entitymanager.close();
	}
}
