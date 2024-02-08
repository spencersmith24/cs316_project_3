import java.io.File;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Locale;

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
            ByteBuffer request = ByteBuffer.allocate(1024);
            ByteBuffer reply = ByteBuffer.allocate(1024);
            int numBytes = serveChannel.read(request);

            request.flip();

            byte[] clientQueryArray = new byte[numBytes];
            request.get(clientQueryArray);
            String command = new String(clientQueryArray);
            String commandChar = command.substring(0, 1).toUpperCase();

            File directoryPath = new File("src/files");

            switch (commandChar) {
                case "L":
                    String contents[] = directoryPath.list();
                    String returnString = "";
                    for (int i = 0; i < contents.length; i++) {
                        returnString += String.format("%d) %s\n", (i + 1), contents[i]);
                    }
                    if (returnString.equals("")) {
                        reply = ByteBuffer.wrap("No files found".getBytes());
                        serveChannel.write(reply);
                    } else {
                        reply = ByteBuffer.wrap(returnString.getBytes());
                        serveChannel.write(reply);
                    }

                    break;
                case "D":
                    String selectedFile = command.substring(1);
                    File f = new File(directoryPath + "/" + selectedFile);
                    if (!f.delete()) {
                        reply = ByteBuffer.wrap("Couldn't find that file".getBytes());
                        serveChannel.write(reply);
                    } else {
                        reply = ByteBuffer.wrap("Operation successful".getBytes());
                        serveChannel.write(reply);
                    }
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
            serveChannel.close();

        }
    }
}