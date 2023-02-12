package chat.bot1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
    public static void main(String[] args) throws Exception{
        // ServerSocket = 서버를 기다리기위해(대기) 사용
        ServerSocket serverSocket = new ServerSocket(9999);
        //공유 객체에서 쓰레드에 안전한 리스트를 만든다.
        List<PrintWriter> outList = Collections.synchronizedList(new ArrayList<>());


        /**
         * 여러개의 클라이언트와 연결하기 위해
         */
        while (true) {
            // 연결을 수용한다. (클라이언트와 통신하기위한 socket)
            Socket socket = serverSocket.accept();
            System.out.println("접속 : " + socket);


            ChatThread chatThread = new ChatThread(socket, outList);
            chatThread.start();
        }
    }
}

class ChatThread extends Thread{

    private Socket socket;
    private List<PrintWriter> outList;
    private PrintWriter out;
    private BufferedReader in;
    public ChatThread(Socket socket, List<PrintWriter> outList) {
        this.socket = socket;
        this.outList = outList;

        // 1. socket으로 부터 읽어드릴 수 있는 객체를 얻는다
        // 2. socket에게 쓰기위한 객체를 얻는다. (현재 연결된 클라이언트에게 쓰는 객체)

        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 공유객체에서 PrintWriter를 담은거를 알게된다.
            outList.add(out);

        }catch (IOException e){
            e.printStackTrace();
        }



    }

    public void run(){


        String line = null;

        try {
            // 3. 클라이언트가 보낸 메시지를 읽는다.
            while ((line = in.readLine()) != null){
                for (int i = 0; i < outList.size(); i++) { // 접속한 모든 클라이언트에게 메시지를 전송한다.
                    PrintWriter print = outList.get(i);
                    print.println(line);
                    print.flush();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally { // 접속 연결이 끊어질경우
            try {
                outList.remove(out);
            }catch (Exception e){
                e.printStackTrace();
            }

            for (int i = 0; i < outList.size(); i++) {
                PrintWriter print = outList.get(i);
                print.println("다른 클라이언트가 연결을 끊었습니다.");
                print.flush();
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
