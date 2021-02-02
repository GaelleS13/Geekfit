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

	// Converting an array to JSon
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

	// Getting a list of challenges
	public static List<Challenge> getChall() {
		EntityManager entitymanager = getEntityManager();
		List<Challenge> chals = entitymanager.createQuery("from Challenge c", Challenge.class).getResultList();
		entitymanager.close();
		return chals;
	}

	// Getting a Single challenge
	public static Challenge getChall(int id) {
		for (Challenge chall : getChall()) {
			if (chall.getChallengeId() == id) {
				return chall;
			}
		}
		return null;
	}

	// Adding an admin to a challenge
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

	// Deleting an admin from a challenge
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

	// Creating a challenge
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

	// Deleting a challenge
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

	// Converting an id list to a list of challenges
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

	// Adding a description to the challenge
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

	// Getting the users
	public static List<User> getUsers() {
		EntityManager entitymanager = getEntityManager();
		List<User> users = entitymanager.createQuery("from User u", User.class).getResultList();
		entitymanager.close();
		return users;
	}

	// Testing if a user exists
	public static boolean existingUser(String username) {
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}

	// Getting a single user
	public static User getUser(String username) {
		for (User user : getUsers()) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}

	// Adding a user
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

	// Deleting a user
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

	// Setting the password
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

	// Adding a user to a challenge
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

	// Setting the number of players
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

	// Getting the suggestions
	public static List<Suggestion> getSuggestion() {
		EntityManager entitymanager = getEntityManager();
		List<Suggestion> sugs = entitymanager.createQuery("from Suggestion s", Suggestion.class).getResultList();
		entitymanager.close();
		return sugs;
	}

	// Getting a single suggestion
	public static Suggestion getSuggestion(int id) {
		for (Suggestion suggestion : getSuggestion()) {
			if (suggestion.getSuggestionId() == id) {
				return suggestion;
			}
		}
		return null;
	}

	// Adding a suggestion
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

	// Deleting a suggestion
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

	// Voting yes
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

	// Voting no
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

	// Set the valdiation
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

	// Getting a crossPoint
	public static CrossingPoint getCrossPoint(int id, int crossPointId) {
		for (CrossingPoint crossPoint : getChall(id).getCrossingPoints()) {
			if (crossPoint.getCrossingPointId() == crossPointId) {
				return crossPoint;
			}
		}
		return null;
	}

	// Adding a crossPoint
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

	// Getting a segment
	public static Segment getSegment(int id, int segmentId) {
		for (Segment segment : getChall(id).getSegments()) {
			if (segment.getSegmentId() == id) {
				return segment;
			}
		}
		return null;
	}

	// Adding a segment
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

	// Deleting a segment
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

	// Adding an obstacle
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

	// Deleting an obstacle
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
