package br.edu.com.ifpb.sd;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import br.edu.com.ifpb.sd.Message;

public class MessageService {
	
	private static final Logger log = Logger.getLogger(MessageService.class.getName());
	
	private File file;
	private String pathToFile;
	
	public MessageService(String pathToFile) {
		this.pathToFile = pathToFile;
		this.file = new File(pathToFile);
		if(!file.exists()) {
			log.info("Criando arquivo pois ele não existe");
			try {
				Files.createFile(Paths.get(pathToFile));
			} catch (IOException e) {
				log.info(e.getStackTrace().toString());;
			}
		}
	}
	
	public List<Message> read() {
		log.info("Lendo do arquivo.");
		List<Message> messages = new ArrayList<>();
		try {
			ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(Paths.get(pathToFile)));
			messages = ((List<Message>) ois.readObject());
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			log.info(e.getMessage());
		}
		
		return messages;
	}
	
	public void save(Message msg) {
		List<Message> messages = this.read();
		messages.add(msg);
		
		try {
			log.info("Escrevendo no arquivo.");
			ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(pathToFile)));
			oos.writeObject(messages);
			
			oos.flush();
			oos.close();
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}
	
	public void lock() {
		try {
			log.info("Travando o arquivo para escrita.");
			Files.createFile(Paths.get(pathToFile + ".lock"));
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}
	
	public void unlock() {
		try {
			log.info("Removendo trava do arquivo.");
			Files.deleteIfExists(Paths.get(pathToFile + ".lock"));
		} catch (IOException e) {
			log.info(e.getMessage());
		}
	}
	
	public boolean canWrite() {
		return !Files.exists(Paths.get(pathToFile + ".lock"));
	}
}
