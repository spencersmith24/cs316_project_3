import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;

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

        ByteBuffer reply;
        String selectedFile;
        File f;
        File directoryPath = new File("src/files");

        while (true) {
            SocketChannel serveChannel = welcomeChannel.accept();
            ByteBuffer request = ByteBuffer.allocate(1024);
            int numBytes = serveChannel.read(request);

            request.flip();

            byte[] clientQueryArray = new byte[numBytes];
            request.get(clientQueryArray);
            String command = new String(clientQueryArray);
            String commandChar = command.substring(0, 1).toUpperCase();

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
                    if (f.exists() && !f.delete()) {
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
                    f = new File(directoryPath + "/" + selectedFile);

                    if (!f.exists()) {
                        serveChannel.write(ByteBuffer.wrap(("F").getBytes()));
                        serveChannel.close();
                    } else {
                        serveChannel.write(ByteBuffer.wrap(("S").getBytes()));

                        // check to see if client is ready to receive file
                        ByteBuffer responseBuffer = ByteBuffer.allocate(1);
                        serveChannel.read(responseBuffer);

                        responseBuffer.flip();
                        byte[] responseArray = new byte[1];
                        responseBuffer.get(responseArray);
                        if (new String(responseArray).equals("R")) {

                            FileInputStream fs = new FileInputStream(directoryPath + "/" + selectedFile);
                            FileChannel fc = fs.getChannel();
                            int bufferSize = 1024;
                            ByteBuffer content = ByteBuffer.allocate(bufferSize);
                            while (fc.read(content) >= 0) {
                                content.flip();
                                serveChannel.write(content);
                                content.clear();
                            }
                            fs.close();
                            serveChannel.shutdownOutput();
                        }
                    }
                    break;
                case "U":
                    selectedFile = command.substring(1);
                    FileOutputStream fo = new FileOutputStream(directoryPath + "/" + selectedFile, true);
                    FileChannel fc = fo.getChannel();

                    serveChannel.write(ByteBuffer.wrap("S".getBytes()));

                    ByteBuffer newFileContent = ByteBuffer.allocate(1024);

                    while (serveChannel.read(newFileContent) >= 0) {
                        newFileContent.flip();
                        fc.write(newFileContent);
                        newFileContent.clear();
                    }
                    fo.close();
                    serveChannel.close();
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
