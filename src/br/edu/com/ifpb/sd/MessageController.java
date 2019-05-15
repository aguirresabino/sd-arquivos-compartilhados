package br.edu.com.ifpb.sd;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MessageController {
	
	private static final Logger log = Logger.getLogger(MessageController.class.getName());
	
	private MessageService messageService;
	private FileServiceLocal fileServiceLocal;
	
	public MessageController(MessageService messageService, FileServiceLocal fileServiceLocal) {
		this.fileServiceLocal = fileServiceLocal;
		this.messageService = messageService;
	}
	
	public boolean writeMessage(Message msg) {
		boolean isWrite = false;
		if(this.messageService.canWrite()) {
//			log.info("O arquivo está disponível para escrita");
//			log.info("Travando o arquivo");
			// Caso mais de um usuário consiga acessar o arquivo simultâneamente, é verificado se o lock foi concluído.
			// Quando o primeiro lock for feito, os outros receberão uma exceção e o método MessageService.lock() retornará false.
			if(this.messageService.lock()) {
//				log.info("Enviando mensagem");
				messageService.save(msg);
//				log.info("Removendo trava do arquivo");
				messageService.unlock();
				isWrite = true;
			}
		} else {
			log.info("O arquivo não está disponível para escrita...\nTente novamente mais tarde.");
		}

		return isWrite;
	}
	
	public List<Message> readMessages() {
		List<Message> msgs = messageService.read();
		if(msgs != null & !msgs.isEmpty()) fileServiceLocal.write(msgs.get(msgs.size() - 1).getTmstp());
		return msgs;
	}

	public List<Message> readLastMessages() {
		Instant instant = fileServiceLocal.read();
		List<Message> msgs = readMessages();
		if(msgs != null && !msgs.isEmpty()) {
			msgs = msgs.stream()
					.filter(msg -> msg.getTmstp().isAfter(instant))
					.collect(Collectors.toList());
		}
		return msgs;
	}
	
}
