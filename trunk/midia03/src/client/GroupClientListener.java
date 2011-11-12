package client;

import java.util.ArrayList;

/**
 * @author Pedro
 * Call back functions, set this in GroupClient
 */
public interface GroupClientListener{

	/**
	 * When Client is refused in the group
	 * @param groupName
	 */
	void groupRefused(String groupName);

	/**
	 * When client is accepted in group
	 * @param groupName
	 */
	void groupAccepted(String groupName); //client accepted

	/**
	 * Administrator fails to create group
	 * @param groupName
	 */
	void createFailed(String groupName); //administrator couldn't create

	/**
	 * Group is created, administrator
	 * @param groupName
	 */
	void createOk(String groupName);

	/**
	 * Somebody wants to join your group, for administrator
	 * @param groupName
	 * @param clientName
	 */
	void clientWantsToJoin(String groupName, String clientName);

	/**
	 * Current list of clients in your group, for administrator
	 * @param participants
	 */
	void updateClientsInGroup(ArrayList<String> participants);

	/**
	 * Current status message of joining process, clients
	 * @param newLine
	 */
	void joinGroupStatus(String newLine);

	/**
	 * new list of groups available
	 * @param groups
	 */
	void updateGroupNames(ArrayList<String> groups);

	/**
	 * When the group you're in closes
	 * @param groupName
	 */
	void groupEnded(String groupName);

}