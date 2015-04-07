package com.Model;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class SSHClient {
	private Client client;
	private Connection conn = null;
	private Session sess = null;
	private boolean isAutenticate = false;
	private String username = "pi";
	private String password = "raspberry";

	public SSHClient(Client client) {
		this.client = client;
		ConnectSSH();
	}
	public Connection getConnnection()
	{
		return conn;
	}
	public Session getSession()
	{
		return sess;
	}
	public boolean getisAuthenticate()
	{
		return isAutenticate;
	}
	public SSHClient(Client client,boolean onlyconnect)
	{
		this.client=client;
		ConnectSSH();
	}
	public void DisconnetcSSH()
	{
		sess.close();
		conn.close();
	}
	private boolean ConnectSSH() {
		conn = new Connection(client.ipAddress);
		try {
			conn.connect(null, 1000, 1000);
			isAutenticate = conn.authenticateWithPassword(username, password);
			if (isAutenticate) {
				sess = conn.openSession();
				return true;
			} else
				return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	private void setDefaultConfig()
	{
		if(isAutenticate&&sess!=null)
		{
			//Setup config
		}
	}
}
