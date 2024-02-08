import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Locale;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {
        // expect two command-line args from user
        if (args.length != 2) {
            System.out.println("Syntax: Client <serverIP> <serverPort>");
            return;
        }
        int serverPort = Integer.parseInt(args[1]);


        String command;
        do {
            System.out.println("Input a command\n1) List - list all files\n2) Delete - remove a file\n3) Rename - rename a file\n4) Download - download a file\n5) Upload - upload a file\n");
            Scanner keyboard = new Scanner(System.in);
            command = keyboard.nextLine().toUpperCase(Locale.ROOT);
            switch (command) {
                case "LIST":
                    // TODO make list functionality
                    break;
                case "DELETE":
                    // TODO make delete functionality
                    break;
                case "RENAME":
                    // TODO make rename functionality
                    break;
                case "DOWNLOAD":
                    // TODO make download functionality
                    break;
                case "UPLOAD":
                    // TODO make upload functionality
                    break;
                default:
                    if (!command.equals("0")) {
                        System.out.println("Invalid command");
                    }
            }
        } while (command.equals("q") || command.equals("quit"));

    }
}