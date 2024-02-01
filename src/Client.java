
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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

        // "GetTime", "GetDate"
        Scanner keyboard = new Scanner(System.in);
        String message = keyboard.nextLine();

        ByteBuffer queryBuffer = ByteBuffer.wrap(message.getBytes());

        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(args[0], serverPort));
        channel.write(queryBuffer);

        ByteBuffer replyBuffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(replyBuffer);

        replyBuffer.flip();

        byte[] replyArray = new byte[bytesRead];
        replyBuffer.get(replyArray);
        System.out.println(new String(replyArray));

        channel.close();
    }
}