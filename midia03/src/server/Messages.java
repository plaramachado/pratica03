package server;

public class Messages {
	public static String wantJoin(String groupName, String clientName) {
		return "WANTJOIN "+groupName + " " + clientName + " \r\n";
	}
	
	public static String createError(String groupName) {
		return "CREATEERROR " + groupName + " \r\n";
	}

	public static String createOk(String groupName) {
		return "CREATEOK " + groupName + " \r\n";
	}
	
	public static final String GROUPGET = "GROUPGET \r\n";
	public static final String GROUP_101_FOUND = "TEMP 101 found \r\n";
	public static final String GROUP_404_NOT_FOUND = "TEMP 404 not found \r\n";
	public static final String GROUP_180_RINGING = "TEMP 180 ringing \r\n";
	public static final String GROUP_100_TRYING = "TEMP 100 trying \r\n";
	public static String refused(String groupName) {
		return "REFUSED "+groupName + " \r\n";
	}

	public static String accepted(String groupName) {
		return "ACCEPTGROUP "+groupName + " \r\n";
	}

	public static String close(String name) {
		return "CLOSE " + name + " \r\n";
	}

	public static String create(String groupName) {
		return "CREATE " + groupName + " \r\n";
	}

	public static String refuseJoin(String myGroupName, String client) {
		return "REFUSE " + myGroupName + " " + client+ " \r\n";
	}

	public static String acceptJoin(String myGroupName, String clientName) {
		return "ACCEPT " + myGroupName + " " + clientName+ " \r\n";
	}

	public static String requestJoin(String groupName) {
		return "JOIN " + groupName + " \r\n";
	}

	public static String leave(String groupJoined) {
		return "LEAVE " + groupJoined + " \r\n";
	}

	public static String groupText(String groupJoined, String message) {
		return "GROUPTEXT " + groupJoined + " \r\n" + message+ " \r\n\r\n";
	}

	public static String groupTextRepass(String groupName, String message,
			String userName) {
		return "TEXTGROUP " + groupName + " " + userName+ " \r\n" + message+ " \r\n\r\n";
	}
	
	

}
