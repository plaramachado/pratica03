package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import server.Server.ServerForker;

public class GroupServer implements ServerForker {

	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	public GroupServer(Server server) {
		bufferedReader = server.getBufferedReader();
		bufferedWriter = server.getBufferedWriter();
	}

	@Override
	public boolean fork(String receivedLine) {
		// TODO Auto-generated method stub
		return false;
	}

}
