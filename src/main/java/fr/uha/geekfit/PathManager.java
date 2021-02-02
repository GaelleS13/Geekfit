package fr.uha.geekfit;

import java.util.ArrayList;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import fr.uha.dao.DAO;
import fr.uha.models.Challenge;
import fr.uha.models.CrossingPoint;
import fr.uha.models.Obstacle;
import fr.uha.models.Segment;
import fr.uha.models.Suggestion;
import fr.uha.models.User;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/")
public class PathManager {

	// _________________________________ PUBLIC _________________________________//

	/**
	 * Home page
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return "Home Page";
	}

	// _________________________________ PROFIL _________________________________ //

	/**
	 * To register on the geekfit application
	 * 
	 * @param username Username
	 * @param password Password
	 * @return
	 */
	@POST
	@Path("/signup")
	@Produces(MediaType.TEXT_PLAIN)
	public String signup(@Context HttpServletRequest request, @QueryParam("username") String username,
			@QueryParam("password") String password)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		if (!DAO.existingUser(username)) {
			DAO.addUser(new User(DAO.getUsers().size(), username, password));
			HttpSession session = request.getSession(true);
			session.setAttribute("username", username);
			return "Welcome " + username + " you are now signed up";
		} else {

			return "This user already exist";
		}
	}

	/**
	 * To sign in the geekfit application
	 * 
	 * @param username Username
	 * @param password Password
	 * @return
	 */
	@PUT
	@Path("/signin")
	@Produces(MediaType.TEXT_PLAIN)
	public String signin(@Context HttpServletRequest request, @QueryParam("username") String username,
			@QueryParam("password") String password) {
		HttpSession session = request.getSession(true);
		if (DAO.existingUser(username)) {
			if (DAO.getUser(username).getPassword().equals(password)) {
				if (DAO.getUser(username).isAdmin()) {
					session.setAttribute("username", username);
					session.setAttribute("logged", true);
					return "Welcome " + username + " you are now signed in";
				} else {
					session.setAttribute("username", username);
					session.setAttribute("logged", true);
					return "Welcome " + username + " you are now signed in";
				}
			} else {
				return "Incorrect password";
			}
		} else {
			return "You do not have an account yet, please sign up by going to /signup!";
		}
	}

	/**
	 * Display the profile of the connected user
	 * 
	 * @return
	 */
	@GET
	@Path("/profile")
	@Produces(MediaType.APPLICATION_JSON)
	public String profilGet(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String username = (String) session.getAttribute("username");
		if (session.getAttribute("logged") != null) {
			return DAO.getUser(username).toString();
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To change password
	 * 
	 * @param username    Username
	 * @param oldPassword Old password
	 * @param newPassword New password
	 * @return
	 */
	@PUT
	@Path("/profile")
	@Produces(MediaType.TEXT_PLAIN)
	public String profilPut(@Context HttpServletRequest request, @QueryParam("username") String username,
			@QueryParam("oldPassword") String oldPassword, @QueryParam("newPassword") String newPassword)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			if (DAO.getUser(username).getPassword().equals(oldPassword)) {
				DAO.setPassword(username, newPassword);
				return "Profil updated!";
			} else {
				return "Wrong password";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To delete the account
	 * 
	 * @param username Username
	 * @param password Passowrd
	 * @return
	 */
	@DELETE
	@Path("/profile")
	@Produces(MediaType.TEXT_PLAIN)
	public String profilDelete(@Context HttpServletRequest request, @QueryParam("username") String username,
			@QueryParam("password") String password)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, NamingException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			if (DAO.getUser(username).getPassword().equals(password)) {
				session.removeAttribute("username");
				session.removeAttribute("admin");
				DAO.delUser(DAO.getUser(username));
				return "Profil deleted!";
			} else {
				return "Wrong password";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To sign out of the account
	 * 
	 * @return
	 */
	@PUT
	@Path("/signout")
	@Produces(MediaType.TEXT_PLAIN)
	public String signout(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			session.removeAttribute("username");
			session.removeAttribute("admin");
			session.removeAttribute("logged");
			return "You successfully signed out!";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To see all the challenges the account is registered to
	 * 
	 * @return
	 */
	@GET
	@Path("/profile/challenges")
	@Produces(MediaType.APPLICATION_JSON)
	public String profilChallenges(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			return DAO.arrayToJson(DAO.challIdsToChall(DAO.getUser(username).getChallenges()));
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To register to a challenge
	 * 
	 * @param id Id of the challenge the account wants to register to
	 * @return
	 */
	@PUT
	@Path("/profile/challenge")
	@Produces(MediaType.TEXT_PLAIN)
	public String registerChallenge(@Context HttpServletRequest request, @QueryParam("id") int id)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			DAO.addUserToChall(username, id);
			return "You succesfully subscibed to the challenge n° " + id;
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To see all the existing challenges
	 * 
	 * @return
	 */
	@GET
	@Path("/challenges")
	@Produces(MediaType.APPLICATION_JSON)
	public String challenges(@Context HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			return DAO.arrayToJson(DAO.getChall());
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To suggest a new challenge
	 * 
	 * @param id   Id of the suggestion
	 * @param name Name of the suggestion
	 * @return
	 */
	@POST
	@Path("/votes/suggestion")
	@Produces(MediaType.TEXT_PLAIN)
	public String propChallenge(@Context HttpServletRequest request, @QueryParam("id") int id,
			@QueryParam("name") String name)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			DAO.addSugg(new Suggestion(id, username, name));
			return "The suggestion has been sent";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To vote for a suggestion
	 * 
	 * @param vote True or false to vote yes or no for a suggestion
	 * @param id   Id of the suggestion
	 * @return
	 */
	@PUT
	@Path("/votes")
	@Produces(MediaType.TEXT_PLAIN)
	public String votes(@Context HttpServletRequest request, @QueryParam("id") int id, @QueryParam("vote") boolean vote)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			if (DAO.getSuggestion(id).isValidated()) {
				if (vote) {
					DAO.addVoteYes(id);
					return "Your vote for the suggestion was registered";
				} else {
					DAO.addVoteNo(id);
					return "Your vote for the suggestion was registered";
				}
			} else {
				return "You can't vote for that challenge";
			}
		} else {
			return "You must be signed in first";
		}
	}

	// _______________________________ ADMIN _______________________________ //

	// ******************************* CHALLENGES ******************************* //

	/**
	 * To create a new challenge
	 * 
	 * @param id Id of the challenge to create
	 * @return
	 */
	@POST
	@Path("challenge")
	@Produces(MediaType.TEXT_PLAIN)
	public String addChallenge(@Context HttpServletRequest request, @QueryParam("id") int id)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				DAO.addChall(new Challenge(id, 1, 0));
				DAO.addAdmin(id, DAO.getUser(username).getUserId());
				return "New challenge created";
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To delete a challenge
	 * 
	 * @param id Id of the challenge to delete
	 * @return
	 */
	@DELETE
	@Path("/challenge")
	@Produces(MediaType.TEXT_PLAIN)
	public String delChallenge(@Context HttpServletRequest request, @QueryParam("id") int id)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				DAO.delChall(id);
				return "Challenge deleted";
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To add an admin of the challenge
	 * 
	 * @param id               Id of the concerned challenge
	 * @param usernameNewAdmin Username of the new challenge admin
	 * @return
	 */
	@POST
	@Path("/challenge/{id}/admin")
	@Produces(MediaType.TEXT_PLAIN)
	public String addAdmin(@Context HttpServletRequest request, @PathParam("id") int id,
			@QueryParam("usernameNewAdmin") String usernameNewAdmin)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				DAO.addAdmin(id, DAO.getUser(usernameNewAdmin).getUserId());
				return "New admin added";
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To remove and admin of the challenge
	 * 
	 * @param id               Id of the concerned challenge
	 * @param usernameDelAdmin Username of the admin to remove
	 * @return
	 */
	@DELETE
	@Path("/challenge/{id}/admin")
	@Produces(MediaType.TEXT_PLAIN)
	public String delAdmin(@Context HttpServletRequest request, @PathParam("id") int id,
			@QueryParam("usernameDelAdmin") String usernameDelAdmin)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				DAO.delAdmin(id, DAO.getUser(usernameDelAdmin).getUserId());
				return "Admin removed";
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To add a description to the challenge
	 * 
	 * @param id          Id of the concerned challenge
	 * @param description Description of the challenge
	 * @return
	 */
	@POST
	@Path("challenge/{id}/description")
	@Produces(MediaType.TEXT_PLAIN)
	public String addDescription(@Context HttpServletRequest request, @PathParam("id") int id,
			@QueryParam("description") String description)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							DAO.addDescription(id, description);
							return "The description of the challenge now is : " + description;
						} else {
							return "This challenge doesn't exist";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To add a segment in a challenge
	 * 
	 * @param id           Id of the concerned challenge
	 * @param segmentId    Id of the new segment
	 * @param previousCPId Id of the previous crossing point
	 * @param nextCPId     Id of the next crossing point
	 * @return
	 */
	@POST
	@Path("challenge/{id}/segment")
	@Produces(MediaType.TEXT_PLAIN)
	public String addSegment(@Context HttpServletRequest request, @PathParam("id") int id,
			@QueryParam("segmentId") int segmentId, @QueryParam("previousCPId") int previousCPId,
			@QueryParam("nextCPId") int nextCPId)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							DAO.addSegment(id, new Segment(segmentId, chal.getCrossingPoint(nextCPId),
									chal.getCrossingPoint(nextCPId)));
							return "Segment created";
						} else {
							return "Challenge unknown";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To remove a segment in a challenge
	 * 
	 * @param id        Id of the concerned challenge
	 * @param segmentId Id of the concerned segment
	 * @return
	 */
	@DELETE
	@Path("challenge/{id}/segment")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeSegment(@Context HttpServletRequest request, @PathParam("id") int id,
			@QueryParam("segmentId") int segmentId)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, NamingException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							ArrayList<Segment> segs = chal.getSegments();
							for (Segment seg : segs) {
								if (seg.getSegmentId() == segmentId) {
									DAO.delSegment(id, segmentId);
									return "Segment removed";
								}
							}
							return "Segment unknown";
						} else {
							return "Challenge unknown";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To add a crossing point in a challenge
	 * 
	 * @param id           Id of the concerned challenge
	 * @param crossPointId Id of the new crossing point
	 * @return
	 */
	@POST
	@Path("challenge/{id}/crosspoint")
	@Produces(MediaType.TEXT_PLAIN)
	public String addCrossPoint(@Context HttpServletRequest request, @PathParam("id") int id,
			@QueryParam("crossPointId") int crossPointId)
			throws SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SystemException, NotSupportedException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							DAO.addCrossPoint(id, new CrossingPoint(crossPointId, 0));
							return "Crossing point created";
						} else {
							return "Challenge unknown";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To add a new obstacle
	 * 
	 * @param id         Id of the concerned challenge
	 * @param segmentId  Id of the concerned segment
	 * @param obstacleId Id of the new obstacle
	 * @param name       Name of the obstacle
	 * @return
	 */
	@POST
	@Path("challenge/{id}/segment/{segmentId}/obstacle")
	@Produces(MediaType.TEXT_PLAIN)
	public String addObstacle(@Context HttpServletRequest request, @PathParam("id") int id,
			@PathParam("segmentId") int segmentId, @QueryParam("obstacleId") int obstacleId,
			@QueryParam("name") String name)
			throws SecurityException, IllegalStateException, NamingException, NotSupportedException, SystemException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							ArrayList<Segment> segs = chal.getSegments();
							for (Segment seg : segs) {
								if (seg.getSegmentId() == segmentId) {
									DAO.addObstacle(id, segmentId, new Obstacle(obstacleId, name));
									return "Obstacle added";
								} else {
									return "Segment unknown";
								}
							}
						} else {
							return "Challenge unknown";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To delete an obstacle
	 * 
	 * @param id         Id of the concerned challenge
	 * @param segmentId  Id of the concerned segment
	 * @param obstacleId Id of the concerned obstacle
	 * @return
	 */
	@DELETE
	@Path("challenge/{id}/segment/{segmentId}/obstacle")
	@Produces(MediaType.TEXT_PLAIN)
	public String removeObstacle(@Context HttpServletRequest request, @PathParam("id") int id,
			@PathParam("segmentId") int segmentId, @QueryParam("obstacleId") int obstacleId)
			throws SecurityException, IllegalStateException, NamingException, NotSupportedException, SystemException,
			RollbackException, HeuristicMixedException, HeuristicRollbackException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							ArrayList<Segment> segs = chal.getSegments();
							for (Segment seg : segs) {
								if (seg.getSegmentId() == segmentId) {
									DAO.delObstacle(id, segmentId);
									return "Obstacle removed";
								} else {
									return "Segment unknown";
								}
							}
						} else {
							return "Challenge unknown";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To change the number of players of the challenge
	 *
	 * @param id   Id of the concerned challenger
	 * @param mode Number of players
	 * @return
	 */
	@POST
	@Path("/challenge/{id}/mode")
	@Produces(MediaType.TEXT_PLAIN)
	public String changeMode(@Context HttpServletRequest request, @PathParam("id") int id, @QueryParam("mode") int mode)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			ArrayList<Integer> admins = DAO.getChall(id).getAdmins();
			for (int admin : admins) {
				if (DAO.getUser(username).getUserId() == admin) {
					for (Challenge chal : DAO.getChall()) {
						if (chal.getChallengeId() == id) {
							DAO.setMode(id, mode);
							return "Mode changed";
						} else {
							return "Challenge unknown";
						}
					}
				}
			}
			return "You are not challenge admin";
		} else {
			return "You must be signed in first";
		}
	}

	// ******************************* VOTES ******************************* //

	/**
	 * To validate a suggestion
	 * 
	 * @param id Id of the concerned suggestion
	 * @return
	 */
	@PUT
	@Path("/votes/validate")
	@Produces(MediaType.TEXT_PLAIN)
	public String validateChallenge(@Context HttpServletRequest request, @QueryParam("id") int id)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				DAO.setValidated(id, true);
				return "The suggestion has been validated with success";
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To close the votes for a suggestion
	 * 
	 * @param id Id of the concerned suggestion
	 * @return
	 */
	@PUT
	@Path("/votes/closing")
	@Produces(MediaType.TEXT_PLAIN)
	public String closeVote(@Context HttpServletRequest request, @QueryParam("id") int id)
			throws SecurityException, IllegalStateException, NotSupportedException, SystemException, RollbackException,
			HeuristicMixedException, HeuristicRollbackException, NamingException {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				DAO.setValidated(id, false);
				return "The vote for the suggestion " + DAO.getSuggestion(id).getDescription() + " is over";
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}

	/**
	 * To display the result of a vote
	 * 
	 * @param id Id of the concerned suggestion
	 * @return
	 */
	@GET
	@Path("/votes/result")
	@Produces(MediaType.APPLICATION_JSON)
	public String showResults(@Context HttpServletRequest request, @QueryParam("id") int id) {
		HttpSession session = request.getSession(true);
		if (session.getAttribute("logged") != null) {
			String username = (String) session.getAttribute("username");
			if (DAO.getUser(username).isAdmin()) {
				return DAO.getSuggestion(id).toString();
			} else {
				return "You are not an admin";
			}
		} else {
			return "You must be signed in first";
		}
	}
}