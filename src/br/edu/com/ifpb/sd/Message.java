package br.edu.com.ifpb.sd;

import java.io.Serializable;
import java.time.Instant;

public class Message implements Serializable, Comparable {

	public static enum MessageType {
		LOGIN, MESSAGE, EXIT
	}
	private String id;
	private MessageType tp;
	private String msg;
	private Instant tmstp;
	
	public Message() {}
	
	public Message(String id, MessageType tp, String msg, Instant tmstp) {
		super();
		this.id = id;
		this.tp = tp;
		this.msg = msg;
		this.tmstp = tmstp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MessageType getTp() {
		return tp;
	}

	public void setTp(MessageType tp) {
		this.tp = tp;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Instant getTmstp() {
		return tmstp;
	}

	public void setTmstp(Instant tmstp) {
		this.tmstp = tmstp;
	}

	@Override
	public int compareTo(Object o) {
		if(this.getTmstp().isBefore(((Message) o).getTmstp())) return -1;
		if(this.getTmstp().isAfter(((Message) o).getTmstp())) return 1;
		return 0;
	}
}
