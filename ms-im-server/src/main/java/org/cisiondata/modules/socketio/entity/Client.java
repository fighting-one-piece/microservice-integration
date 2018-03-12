package org.cisiondata.modules.socketio.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "T_IM_CLIENT")
public class Client {

	private String clientId = null;
	private Short connected = null;
	private Long mostSignBits = null;
	private Long leastSignBits = null;
	private Date lastConnectedDate = null;
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	
	public Short getConnected() {
		return connected;
	}
	
	public void setConnected(Short connected) {
		this.connected = connected;
	}
	
	public Long getMostSignBits() {
		return mostSignBits;
	}
	
	public void setMostSignBits(Long mostSignBits) {
		this.mostSignBits = mostSignBits;
	}
	
	public Long getLeastSignBits() {
		return leastSignBits;
	}
	
	public void setLeastSignBits(Long leastSignBits) {
		this.leastSignBits = leastSignBits;
	}
	
	public Date getLastConnectedDate() {
		return lastConnectedDate;
	}
	
	public void setLastConnectedDate(Date lastConnectedDate) {
		this.lastConnectedDate = lastConnectedDate;
	}

}
