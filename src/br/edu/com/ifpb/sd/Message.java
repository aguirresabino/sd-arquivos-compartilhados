package br.edu.com.ifpb.sd;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
	public static enum MessageType {
		LOGIN, MESSAGE, EXIT
	}
	private String id;
	private MessageType tp;
	private String msg;
	private LocalDateTime tmstp;
	
	public Message() {}
	
	public Message(String id, MessageType tp, String msg, LocalDateTime tmstp) {
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

	public LocalDateTime getTmstp() {
		return tmstp;
	}

	public void setTmstp(LocalDateTime tmstp) {
		this.tmstp = tmstp;
	}
	
}
