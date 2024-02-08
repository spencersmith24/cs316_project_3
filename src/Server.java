import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Server {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please specify server port.");
            return;
        }
        int port = Integer.parseInt(args[0]);

        ServerSocketChannel welcomeChannel = ServerSocketChannel.open();
        welcomeChannel.socket().bind(new InetSocketAddress(port));

        while (true) {
            SocketChannel serveChannel = welcomeChannel.accept();
            ByteBuffer request = ByteBuffer.allocate(1000);
            int numBytes = serveChannel.read(request);

            request.flip();
            //find out what command is in this request
            String command;

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
            serveChannel.close();
            
        }
    }
}