import java.io.IOException;
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


        Scanner keyboard = new Scanner(System.in);
        String command;
        do {
            System.out.println("Input a command\n" +
                    "1) L - list all files\n" +
                    "2) D - remove a file\n" +
                    "3) R - rename a file\n" +
                    "4) G - download a file\n" +
                    "5) U - upload a file");

            command = keyboard.nextLine().toUpperCase();

            switch (command) {
                case "L":
                    ByteBuffer commandBuffer = ByteBuffer.wrap(command.getBytes());
                    SocketChannel channel = SocketChannel.open();
                    sendRequest(channel, args, serverPort, commandBuffer);
                    displayReply(channel);
                    channel.close();
                    break;
                case "D":
                    System.out.println("Please enter the name of the file you would like to delete:\n");
                    String fileName = keyboard.nextLine();
                    command += fileName;

                    commandBuffer = ByteBuffer.wrap(command.getBytes());
                    channel = SocketChannel.open();
                    sendRequest(channel, args, serverPort, commandBuffer);
                    break;
                case "R":
                    // TODO make rename functionality
                    break;
                case "G":
                    // TODO make download functionality
                    break;
                case "U":
                    // TODO make upload functionality
                    break;
                default:
                    if (!command.equals("0")) {
                        System.out.println("Invalid command");
                    }
            }
        } while (command.equals("q") || command.equals("quit"));

    }

    private static void sendRequest(SocketChannel channel, String[] args, int serverPort, ByteBuffer queryBuffer) throws IOException {
        channel.connect(new InetSocketAddress(args[0], serverPort));
        channel.write(queryBuffer);
    }


    private static void displayReply(SocketChannel channel) throws IOException {
        ByteBuffer replyBuffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(replyBuffer);

        replyBuffer.flip();

        byte[] replyArray = new byte[bytesRead];

        replyBuffer.get(replyArray);
        System.out.println(new String(replyArray));
    }
}