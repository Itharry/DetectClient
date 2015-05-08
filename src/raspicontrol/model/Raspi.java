package raspicontrol.model;

import java.io.IOException;

import raspicontrol.control.Common;
import ch.ethz.ssh2.Connection;

import com.google.gson.annotations.SerializedName;

public class Raspi {
	@SerializedName("ipAddress")
	private String ipAddress;

	@SerializedName("index")
	private int index;

	private transient int streaming_camera_index;
	private transient boolean is_configured;
	private transient boolean is_chosen_to_configure;
	private transient Connection connection;

	public Raspi() {
		this.ipAddress = null;
		index = -1;
		streaming_camera_index = 0;
		is_configured = false;
		is_chosen_to_configure = true;
		connection = null;
	}

	public Raspi(String ipAddress) {
		this.ipAddress = ipAddress;
		index = Common.raspies.getRaspies().size() + 1;
		streaming_camera_index = 0;
		is_configured = false;
		is_chosen_to_configure = true;
		connection = null;
	}

	public Raspi(String ipAddress, int index, int streaming_camera_index,
			boolean is_configured, boolean is_chosen_to_configure,
			Connection connection) {
		super();
		this.ipAddress = ipAddress;
		this.index = index;
		this.streaming_camera_index = streaming_camera_index;
		this.is_configured = is_configured;
		this.is_chosen_to_configure = is_chosen_to_configure;
		this.connection = connection;
	}

	public boolean isIs_chosen_to_configure() {
		return is_chosen_to_configure;
	}

	public void setIs_chosen_to_configure(boolean is_chosen_to_configure) {
		this.is_chosen_to_configure = is_chosen_to_configure;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isIs_configured() {
		return is_configured;
	}

	public void setIs_configured(boolean is_configured) {
		this.is_configured = is_configured;
	}

	public int getStreaming_camera_index() {
		return streaming_camera_index;
	}

	public void setStreaming_camera_index(int streaming_camera_index) {
		this.streaming_camera_index = streaming_camera_index;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void DisconnetcSSH() {
		connection.close();
	}

	public boolean ConnectSSH() {
		connection = new Connection(ipAddress);
		try {
			connection.connect(null, 1000, 1000);
			if (connection.authenticateWithPassword("pi", "raspberry"))
				return true;
			else
				return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean check_raspi_config() throws InterruptedException,
			IOException {
		if ((Common.readfile(connection,
				" mjpg-streamer-experimental/status.txt")).length() > 0)
			return true;
		return false;
	}
}
