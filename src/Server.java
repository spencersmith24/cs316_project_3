import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

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

            String selectedFile = "";
            File f;

            switch (commandChar) {
                case "L":
                    String contents[] = directoryPath.list();
                    String returnString = "";
                    for (int i = 0; i < contents.length; i++) {
                        returnString += format("%d) %s\n", (i + 1), contents[i]);
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
                    selectedFile = command.substring(1);
                    f = new File(directoryPath + "/" + selectedFile);
                    if (!f.delete()) {
                        reply = ByteBuffer.wrap("Couldn't find that file".getBytes());
                        serveChannel.write(reply);
                    } else {
                        reply = ByteBuffer.wrap("Operation successful".getBytes());
                        serveChannel.write(reply);
                    }
                    break;
                case "R":
                    int colon1Index = command.indexOf(':');
                    selectedFile = command.substring(1, colon1Index);
                    String newFileName = command.substring(colon1Index + 1);
                    f = new File(directoryPath + "/" + selectedFile);
                    File renamedFile = new File(directoryPath + "/" + newFileName);
                    if (f.renameTo(renamedFile)) {
                        String returnMessage = String.format("Renamed %s to %s", selectedFile, newFileName);
                        reply = ByteBuffer.wrap(returnMessage.getBytes());
                        serveChannel.write(reply);
                    } else {
                        String returnMessage = String.format("Could not rename %s to %s", selectedFile, newFileName);
                        reply = ByteBuffer.wrap(returnMessage.getBytes());
                        serveChannel.write(reply);
                    }
                    break;
                case "G":
                    selectedFile = command.substring(1);
                    FileInputStream fs = new FileInputStream(directoryPath + "/" + selectedFile);
                    FileChannel fc = fs.getChannel();
                    int bufferSize = 1024;
                    if (bufferSize > fc.size()) {
                        bufferSize = (int) fc.size();
                    }
                    ByteBuffer content = ByteBuffer.allocate(bufferSize);
                    while (fc.read(content) >= 0) {
                        content.flip();
                        serveChannel.write(content);
                        content.clear();
                    }
                    serveChannel.shutdownOutput();
                    break;

                case "U":
                    // TODO make upload functionality

                    break;
                default:
                    if (!command.equals("0")) {
                        System.out.println("Invalid command");
                    }
                    serveChannel.close();
            }
        }
    }
}