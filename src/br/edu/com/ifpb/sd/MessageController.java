package br.edu.com.ifpb.sd;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

import br.edu.com.ifpb.sd.Message.MessageType;

public class MessageController {
	
	private static final Logger log = Logger.getLogger(MessageController.class.getName());
	
	private MessageService messageService;
	
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}
	
	public void writeMessage(Message msg) {
		if(this.messageService.canWrite()) {
			log.info("O arquivo está disponível para escrita");
			log.info("Travando o arquivo");
			this.messageService.lock();
			log.info("Enviando mensagem");
			messageService.save(msg);
			log.info("Removendo trava do arquivo");
			messageService.unlock();
		} else {
			log.info("O arquivo não está disponível para escrita...\nTente novamente mais tarde.");
		}
	}
	
	public List<Message> readMessages() {
		return (List<Message>) messageService.read();
	}
	
}
