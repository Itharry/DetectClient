package raspicontrol.model;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Raspies {
	@SerializedName("rapies")
	private ArrayList<Raspi> raspies;
	@SerializedName("username")
	private String username;
	@SerializedName("password")
	private String password;
	@SerializedName("resolution")
	private String resolution;

	public Raspies(ArrayList<Raspi> raspies) {
		super();
		this.raspies = new ArrayList<Raspi>();
		this.raspies.addAll(raspies);
		this.username = "username";
		this.password = "password";
		this.resolution = "-x 1280 -y 720 -fps 15";
	}

	public Raspies() {
		super();
		this.raspies = new ArrayList<Raspi>();
		this.username = "username";
		this.password = "password";
		this.resolution = "-x 1280 -y 720 -fps 15";
	}

	public ArrayList<Raspi> getRaspies() {
		return raspies;
	}

	public void setRaspies(ArrayList<Raspi> raspies) {
		this.raspies = raspies;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
}
