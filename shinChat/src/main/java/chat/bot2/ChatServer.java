package chat.bot2;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8888);

        // 동시에 접근하지 못하게한다.
        List<ChatThread> list = Collections.synchronizedList(new ArrayList<>());
        while (true) {
            Socket socket = serverSocket.accept(); // 클라이언트가 접속한다.
            ChatThread chatThread = new ChatThread(socket, list);
            chatThread.start();
        } // while

    } // main
}
