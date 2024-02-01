import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

public class Server {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Syntax: Server <serverPort>");
        }
        int port = Integer.parseInt(args[0]);

        // listenChannel always listens for connection requests from clients
        // performs three-way handshake with new clients
        ServerSocketChannel listenChannel = ServerSocketChannel.open();
        listenChannel.bind(new InetSocketAddress(port));


        while (true) {
            // serveChannel serves the client
            SocketChannel serveChannel = listenChannel.accept();

            ByteBuffer clientQueryBuffer = ByteBuffer.allocate(1024);

            // read from network and write to buffer
            int bytesRead = serveChannel.read(clientQueryBuffer);
            // flip is required between write and read
            clientQueryBuffer.flip();

            // read data from buffer
            byte[] clientQueryArray = new byte[bytesRead];
            clientQueryBuffer.get(clientQueryArray);
            String clientQuery = new String(clientQueryArray);
            System.out.println(clientQuery);

            ByteBuffer replyBuffer = ByteBuffer.wrap(clientQuery.getBytes());
            serveChannel.write(replyBuffer);
            serveChannel.close();
        }
    }
}