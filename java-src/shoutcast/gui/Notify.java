package shoutcast.gui;

public interface Notify {
	
	public void setPlay(boolean b);
	public void setSave(boolean b);
	public void setVolume(double d);
	public void setTempMute(boolean mute);
	public void setBoost (boolean boost);
}
