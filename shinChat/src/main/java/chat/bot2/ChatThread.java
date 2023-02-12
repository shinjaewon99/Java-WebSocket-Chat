package chat.bot2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatThread extends Thread {
    private String name;
    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;
    List<ChatThread> list;

    // Server에서 접속한 소켓을 넘겨준다.
    public ChatThread(Socket socket, List<ChatThread> list) throws Exception {
        this.socket = socket;
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.br = br;
        this.pw = pw;
        this.name = br.readLine();
        this.list = list;
        this.list.add(this);
    }

    public void sendMessage(String msg) {
        pw.println(msg);
        pw.flush();
    }

    @Override
    public void run() {
        /*
        broadcast
        ChatThread는 사용자가 보낸 메시지를 읽어들여서
        접속된 모든 클라이언트에게 메시지를 보내야된다.
        */


        // 나를 제외한 모든 사용자(클라이언트) 에게 "..님이 연결되었습니다"
        // 현재 ChatThread를 제외하고 보낸다
        try {
            broadcast(name + "님이 연결되었습니다.", false);


            String line = null;


            while ((line = br.readLine()) != null) {

                if ("/exit".equals(line)) {
                    break;
                }
                // 나를 포함한 ChatThread에게 메시지를 보낸다.
                broadcast(name + " : " + line, true);

            }
        } catch (Exception e) { // ChatThread가 연결이 끊어졌다.
            e.printStackTrace();
        } finally {
            broadcast(name + "님의 연결이 끊어졌습니다.", false);
            this.list.remove(this);
            try {
                br.close();
            } catch (Exception e) {

            }
            try {
                pw.close();
            } catch (Exception e) {

            }
            try {
                socket.close();
            } catch (Exception e) {

            }
        }
    }

    private void broadcast(String msg, boolean includeMe) {

        List<ChatThread> chatThreads = new ArrayList<>();

        for (int i = 0; i < this.list.size(); i++) {
            chatThreads.add(list.get(i));
        }

        try {
            for (int i = 0; i < chatThreads.size(); i++) {
                ChatThread chatThread = chatThreads.get(i);
                if (!includeMe) { // 나를 포함 X

                    if (chatThread == this) {
                        continue;
                    }
                }
                chatThread.sendMessage(msg);


            }
        } catch (Exception e) {
            System.out.println("/////");
        }
    }
}
