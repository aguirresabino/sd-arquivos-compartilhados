package br.edu.com.ifpb.sd;

import jcifs.smb.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessageService {
	
	private static final Logger log = Logger.getLogger(MessageService.class.getName());
	
	private String pathToFile;
	private NtlmPasswordAuthentication auth;
	
	public MessageService(String pathToFile, NtlmPasswordAuthentication auth) {
		this.pathToFile = pathToFile;
		this.auth = auth;
		try {
			SmbFile file = new SmbFile(pathToFile, this.auth);
			if(!file.exists()) {
//				log.info("Criando arquivo pois ele não existe");
				file.createNewFile();
			}
		} catch (MalformedURLException | SmbException e) {
			log.info(e.getMessage());
			System.exit(1);
		}
	}
	
	public List<Message> read() {
//		log.info("Lendo do arquivo.");
		List<Message> messages = new ArrayList<>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new SmbFileInputStream(new SmbFile(this.pathToFile, this.auth)));
			messages = ((List<Message>) ois.readObject());
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			log.info(e.getMessage());
		}
		
		return messages;
	}

	public List<Message> read(Instant timestamp) {
		List<Message> messages = new ArrayList<>();
		try {
			ObjectInputStream ois = new ObjectInputStream(new SmbFileInputStream(new SmbFile(this.pathToFile, this.auth)));
			messages = ((List<Message>) ois.readObject());
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			log.info(e.getMessage());
		}

		messages = messages.stream()
				.filter(msg -> msg.getTmstp()
				.isAfter(timestamp))
				.collect(Collectors.toList());

		return messages;
	}
	
	public void save(Message msg) {
		List<Message> messages = this.read();

		if(!messages.isEmpty() && messages != null && messages.get(messages.size() - 1).getTmstp().isAfter(msg.getTmstp())) {
			messages.add(msg);
			Collections.sort(messages);
		} else {
			messages.add(msg);
		}

		try {
//			log.info("Escrevendo no arquivo.");
			ObjectOutputStream oos = new ObjectOutputStream(new SmbFileOutputStream(new SmbFile(this.pathToFile, this.auth)));
			oos.writeObject(messages);
			
			oos.flush();
			oos.close();
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}
	
	public boolean lock() {
		try {
//			log.info("Travando o arquivo para escrita.");
			SmbFile newFile = new SmbFile(String.format("%s.lock", this.pathToFile), this.auth);
			newFile.createNewFile();
			return true;
		} catch (IOException e) {
			log.info(e.getMessage());
			return false;
		}
	}
	
	public void unlock() {
		try {
//			log.info("Removendo trava do arquivo.");
			SmbFile newFile = new SmbFile(String.format("%s.lock", this.pathToFile), this.auth);
			if(newFile.exists()) newFile.delete();
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}
	
	public boolean canWrite() {
		boolean exists = false;
		try {
			SmbFile file = new SmbFile(String.format("%s.lock", this.pathToFile), this.auth);
			exists = file.exists();
//			log.info("Arquivo lock existe: " + exists);
		} catch (SmbException | MalformedURLException e) {
			log.info(e.getMessage());
		}
		
		return !exists;
	}
}
