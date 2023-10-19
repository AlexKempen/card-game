package edu.utdallas.hearts2hearts;

public class Main {
    public static void main(String[] args) {
        System.out.println("(Main) Running simulation... "); 
        int port = 8888;
        String address = "localhost";       

        Server serverThread = new Server(port);
        Client clientThreads[] = new Client[4];

        serverThread.start();
        for (int i = 0; i < 4; i++){
            clientThreads[i] = new Client(address, port);
            clientThreads[i].start();
        }

        try{
            serverThread.join();
            for (int i = 0; i < 4; i++)
                clientThreads[i].join();
            System.out.print("(Main) Successfully joined threads.");
        } catch(InterruptedException i){
            System.out.println(i);
        }
        
    }
}