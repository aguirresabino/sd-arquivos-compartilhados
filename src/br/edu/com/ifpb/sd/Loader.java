package br.edu.com.ifpb.sd;

import br.edu.com.ifpb.sd.Message.MessageType;
import jcifs.smb.NtlmPasswordAuthentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Loader {
	
	private static Scanner scanner = new Scanner(System.in);
	private static MessageController messageController = new MessageController(
			new MessageService(
					"smb://192.168.0.109/chat/chat.bin", 
					new NtlmPasswordAuthentication(null, "aguirre", "1234")
					)
			);
	private static String nome;
	
	private static boolean login() {
		Message msg = new Message(nome, MessageType.LOGIN, String.format("%s está disponível.", nome), Instant.now());
		return messageController.writeMessage(msg);
	}
	
	private static void exit() {
		Message msg = new Message(nome, MessageType.EXIT, String.format("%s saiu do chat.", nome), Instant.now());
		while(!messageController.writeMessage(msg)) print("Saindo...");
	}
	
	private static void sendMsg(Message msg) {
		messageController.writeMessage(msg);
	}
	
	private static void print(String msg) {
		System.out.println(msg);
	}
	
	private static void menu() {
		print("(1) Escrever mensagem\n(2) Ler novas mensagens\n(3) Sair");
		int op = scanner.nextInt();
		
		switch(op) {
		case 1:
			print("Digite uma mensagem:");
			String bodyMsg = scanner.next();
			Message msg = new Message(nome, MessageType.MESSAGE, bodyMsg, Instant.now());
			if(!messageController.writeMessage(msg)) print("Tente enviar esta mensagem novamente mais tarde!");
			break;
		case 2:
			print("Carregando todas as mensagens...");
			List<Message> messages = messageController.readMessages();
			messages.stream().forEach(ms -> {
				print(String.format("%s: %s", ms.getId(), ms.getMsg()));
			});
			break;
		case 3:
			print("Saindo do chat.");
			exit();
			System.exit(0);
			break;
		}
	}

	public static void main(String[] args) {
		
		Logger log = java.util.logging.Logger.getLogger(Loader.class.getName());
		
		print("Acesse o chat");
		boolean loginFeito = false;
		while(!loginFeito) {
			print("Informe o seu nome:");
			nome = scanner.next();
			log.info("Realizando login do usuário");
			loginFeito = login();
		}
		
		log.info("Carregando as mensagens pela primeira vez");
		List<Message> messages = messageController.readMessages();
		log.info("Imprimindo as mensagens no console");
		messages.stream().forEach(msg -> print(String.format("%s: %s", msg.getId(), msg.getMsg())));
		
		for(;;) menu();
	}

}
