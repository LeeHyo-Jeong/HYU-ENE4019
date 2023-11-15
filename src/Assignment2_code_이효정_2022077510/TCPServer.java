package Assignment2_code_이효정_2022077510;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {

    private static int port = 8080;
    private ServerSocket serverSocket;
    private PageManager pageManager;
    public TCPServer(){
        port = 8080;
        pageManager = new PageManager();
    }
    public static void main(String[] args){
        TCPServer tcpServer = null;
        try{
            tcpServer = new TCPServer();
            tcpServer.serverSocket = new ServerSocket(port);
            tcpServer.handle(tcpServer.serverSocket);
        } catch(Exception e){
            e.printStackTrace();
        } finally{
          tcpServer.stop(tcpServer.serverSocket);
        }
    }
    private void stop(ServerSocket serverSocket){
        try{
            serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    private int requestCnt = 0;
    private void handle(ServerSocket serverSocket) throws IOException{
        while(true){
            Socket connectSocket = null;
            try {
                connectSocket = serverSocket.accept();
                OutputStream out = connectSocket.getOutputStream();
                String path = pageManager.getPath(connectSocket);
                String bodyFile = pageManager.readHtmlFile(path);

                out.write(new String("HTTP/1.1 200 OK\r\n").getBytes());
                out.write(new String("Content-Length: " + bodyFile.getBytes().length + "\r\n").getBytes());
                out.write(new String("Content-Type: text/html;charset=UTF-8\r\n").getBytes());

                pageManager.handleLogic(connectSocket, out, port, path, requestCnt);
                System.out.println("Path: " + path + " cnt: " + requestCnt);
                if(path.equals("Index")) requestCnt++;
                out.write("\r\n".getBytes());

                out.write(bodyFile.getBytes());
                out.flush();
                out.close();

                connectSocket.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
