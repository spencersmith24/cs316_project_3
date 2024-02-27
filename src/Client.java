import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
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
            System.out.println("""
                    Input a command
                    1) L - list all files
                    2) D - remove a file
                    3) R - rename a file
                    4) G - download a file
                    5) U - upload a file""");

            command = keyboard.nextLine().toUpperCase();
            String fileName;
            ByteBuffer commandBuffer;
            SocketChannel channel;
            FileChannel fc;

            switch (command) {
                case "L":
                    commandBuffer = ByteBuffer.wrap(command.getBytes());
                    channel = SocketChannel.open();
                    sendRequest(channel, args, serverPort, commandBuffer);

                    System.out.println(new String(displayReply(channel)));
                    channel.close();
                    break;
                case "D":
                    System.out.println("Please enter the name of the file you would like to delete:\n");
                    fileName = keyboard.nextLine();
                    command += fileName;

                    commandBuffer = ByteBuffer.wrap(command.getBytes());
                    channel = SocketChannel.open();
                    sendRequest(channel, args, serverPort, commandBuffer);

                    System.out.println(new String(displayReply(channel)));
                    channel.close();
                    break;
                case "R":
                    System.out.println("Please enter the name of the file you would like to rename:\n");
                    fileName = keyboard.nextLine();
                    command += fileName;

                    System.out.println("Please enter the new name for the file:\n");
                    String newFileName = keyboard.nextLine();
                    command += ":" + newFileName;

                    commandBuffer = ByteBuffer.wrap(command.getBytes());
                    channel = SocketChannel.open();
                    sendRequest(channel, args, serverPort, commandBuffer);

                    System.out.println(new String(displayReply(channel)));
                    channel.close();
                    break;
                case "G":
                    System.out.println("Please enter the name of the file you would like to download:\n");
                    fileName = keyboard.nextLine();
                    command += fileName;

                    commandBuffer = ByteBuffer.wrap(command.getBytes());
                    channel = SocketChannel.open();
                    sendRequest(channel, args, serverPort, commandBuffer);

                    if(new String(displayReply(channel)).equals("F")){
                        System.out.println("Could not find that file.");
                    } else {
                        channel.write(ByteBuffer.wrap("R".getBytes()));
                        FileOutputStream newFileStream = new FileOutputStream("src/files/" + fileName, true);
                        fc = newFileStream.getChannel();

                        ByteBuffer content = ByteBuffer.allocate(1024);

                        while (channel.read(content) >= 0) {
                            content.flip();
                            fc.write(content);
                            content.clear();
                        }

                        newFileStream.close();
                        System.out.println("File successfully downloaded!");
                    }

                    channel.close();
                    break;
                case "U":
                    System.out.println("Please enter the name of the file you want to upload:\n");
                    fileName = keyboard.nextLine();
                    File f = new File("src/files/" + fileName);
                    if (f.exists()) {
                        command += fileName;

                        commandBuffer = ByteBuffer.wrap(command.getBytes());
                        channel = SocketChannel.open();
                        sendRequest(channel, args, serverPort, commandBuffer);

                        if (new String(displayReply(channel)).equals("S")) {

                            FileInputStream fs = new FileInputStream(f);
                            fc = fs.getChannel();
                            int bufferSize = 1024;
                            if (bufferSize > fc.size()) {
                                bufferSize = (int) fc.size();
                            }

                            ByteBuffer fileContent = ByteBuffer.allocate(bufferSize);
                            while (fc.read(fileContent) >= 0) {
                                channel.write(fileContent.flip());
                                fileContent.clear();
                            }
                            channel.shutdownOutput();
                            channel.close();
                            System.out.println("File successfully uploaded");
                        }
                    } else {
                        System.out.println("File not in current directory");
                    }
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


    private static byte[] displayReply(SocketChannel channel) throws IOException {
        ByteBuffer replyBuffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(replyBuffer);

        replyBuffer.flip();
        byte[] replyArray = new byte[bytesRead];
        replyBuffer.get(replyArray);

        return replyArray;
    }
}
