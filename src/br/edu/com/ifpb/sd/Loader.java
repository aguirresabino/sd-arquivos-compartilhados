package br.edu.com.ifpb.sd;

import br.edu.com.ifpb.sd.Message.MessageType;
import jcifs.smb.NtlmPasswordAuthentication;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Loader implements Runnable {

    private static final Logger log = java.util.logging.Logger.getLogger(Loader.class.getName());

    private static String nome;
    private static String lastConsoleText;
    private static MessageController messageController = new MessageController(
            new MessageService("smb://192.168.0.105/chat/chat.bin",
                    new NtlmPasswordAuthentication(null, "aguirre", "1234")),
            new FileServiceLocal("instantlastmsgread.bin"));
    private static Scanner scanner = new Scanner(System.in);

    private static void printTextAndSaveLastConsoleOutput(String msg) {
        System.out.println(msg);
        lastConsoleText = msg;
    }

    private static boolean login() {
        Message msg = new Message(nome, MessageType.LOGIN, String.format("%s está disponível.", nome), Instant.now());
        return messageController.writeMessage(msg);
    }

    private static void exit() {
        System.out.println("__________________________________________\nSaindo do chat.\n__________________________________________\n");
        Message msg = new Message(nome, MessageType.EXIT, String.format("%s saiu do chat.", nome), Instant.now());
        while (!messageController.writeMessage(msg)) System.out.println("Saindo...");
        System.exit(0);
    }

    private static void sendMsg() {
        printTextAndSaveLastConsoleOutput("\nDigite uma mensagem:");
        String bodyMsg = scanner.nextLine();
        Message msg = new Message(nome, MessageType.MESSAGE, bodyMsg, Instant.now());
        if (!messageController.writeMessage(msg))
            System.out.println("__________________________________________\nTente enviar esta mensagem novamente mais tarde!\n__________________________________________\n");
    }

    private static void readAllMsgs() {
        System.out.println("__________________________________________\nExibindo todas as mensagens...\n__________________________________________\n");
        List<Message> messages = messageController.readMessages();
        messages.stream().forEach(msg -> {
            System.out.println(String.format("[%s] - %s: %s", msg.getTmstp().toString(), msg.getId(), msg.getMsg()));
        });
    }

    private static List<Message> readLastMesgs() {
//		System.out.println("__________________________________________\nÚltimas mensagens\n__________________________________________\n");
        List<Message> messages = messageController.readLastMessages();
//		messages.stream().forEach(msg -> System.out.println(String.format("%30s: %s", msg.getId(), msg.getMsg())));
        return messages;
    }

    private static void menu() {
        printTextAndSaveLastConsoleOutput("__________________________________________\n(1) Escrever mensagem\n() Ler novas mensagens\n(2) Sair\n__________________________________________\n");
        int op = scanner.nextInt();
        scanner.nextLine();

        switch (op) {
            case 1:
                sendMsg();
                break;
            case 2:
                readAllMsgs();
                break;
            case 3:
                exit();
                break;
        }
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<Message> msgs = messageController.readLastMessages();
            if (!lastConsoleText.equals("\nDigite uma mensagem:") && msgs != null && !msgs.isEmpty()) {

                System.out.println("__________________________________________\nÚltimas mensagens\n__________________________________________\n");
                msgs.stream().forEach(msg -> System.out.println(String.format("[%s] - %s: %s", msg.getTmstp().toString(), msg.getId(), msg.getMsg())));
                printTextAndSaveLastConsoleOutput(lastConsoleText);
            }
        }
    }

    public static void main(String[] args) {
        printTextAndSaveLastConsoleOutput("__________________________________________\nAcesse o chat\n__________________________________________\n");
        boolean loginFeito = false;
        //login
        while (!loginFeito) {
            printTextAndSaveLastConsoleOutput("\nInforme o seu nome:");
            nome = scanner.nextLine();
            loginFeito = login();
        }
        //carregando últimas mensagens
        readAllMsgs();
        // Thread separada para imprimir no console as mensagens do arquivo.
        Thread thread = new Thread(new Loader());
        thread.start();
        // exibir menu até que o usuário solicite a saída
        for (; ; ) menu();
    }

}
