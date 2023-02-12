package chat.bot1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) throws Exception {
        // 외부로 부터 argument가 파라미터로 넘어옴
        String name = args[0];

        // host 주소와 9999번 포트에 접속
        Socket socket = new Socket("127.0.0.1", 9999);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // 키보드로부터 입력을 받음
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

        String line = null;
        InputThread inputThread = new InputThread(in);
        inputThread.start();



        while ((line = keyboard.readLine()) != null) {
            out.println(name + " : " + line);
            out.flush();
        }

    }
}

class InputThread extends Thread {
    BufferedReader in = null;

    public InputThread(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            String line = null;
            while((line = in.readLine()) != null){
                System.out.println(line);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
