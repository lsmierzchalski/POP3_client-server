/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
 
public class Client {
 
    public static void main(String[] args){
 
        //Tworzenie gniazda, i sprawdzenie czy host/pory serwera nasłuchuje
        String host;
        int port;
 
        if(args.length==0){
            host= "localhost";
            port = 110;
        }
        else{
            host = args[0];
            String portStr = args[1];
            try {
                port=Integer.parseInt(portStr);
            }
            catch(NumberFormatException nfe){
                System.out.println("Uuups, zły numer portu. Przełączam na domyslny port: 110");
                port = 110;
            }
        }
    try{
        //Próba połączenia z serwerem
        System.out.println("Klient: Próba podłączenia do serwera jako host-"+host+" port: "+port+'.');
        Socket skt = new Socket(host,port);
 
        //Opcje odczytu i zapisu z i do strumienia
        BufferedReader Input = new BufferedReader(new InputStreamReader(skt.getInputStream())); //odczyt
        PrintStream Output = new PrintStream(skt.getOutputStream());
 
        String comand; //w nim zapiszemy swoje imie
        Scanner order = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
           
        String buf = null;
        
        do{
            System.out.print("Podaj komende: ");
            comand = order.nextLine();
            
            //Przesłanie sprawdzającej wiadomości na serwer:
            Output.println(comand);
           
            buf = "";
            if(comand.startsWith("retr")){
            	do{
                    if(!buf.equals("<zzz>"))
                        buf+=Input.readLine();
                    buf+="\n";
                }while(!buf.endsWith("//////////\n"));
                
                System.out.println();
                System.out.println(buf);
                //odebranie ile jest  zalacznikow
                buf=Input.readLine();
                System.out.println("Liczba zalącznikow: [ "+buf+" ]:");
                int messageCount=Integer.parseInt(buf);
                File files[]=new File[messageCount];
                
                BufferedInputStream bis = new BufferedInputStream(skt.getInputStream());
                DataInputStream dis = new DataInputStream(bis);
                
                for(int i=0;i<messageCount;i++) {
                    
                    buf=Input.readLine(); //nazwa pliku
                    System.out.println(buf);
                    files[i]=new File(buf);
                    
                    buf=Input.readLine(); //rozmiar pliku
                    System.out.println("Roz:"+buf);
                    int rozmiarPliku=Integer.parseInt(buf);
                    System.out.println("Roz:"+rozmiarPliku);
             /////////////////////////////////////////////////////////////
                    FileOutputStream fos = new FileOutputStream(files[i]);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    for(int j = 0; j < rozmiarPliku; j++){
                        bos.write(bis.read());
                        System.out.println(j);
                    }
                    
                    bos.close();
                    
                }
                //dis.close();
            }
            else{
            	buf=Input.readLine();
                System.out.println("Klient: Odpowiedź serwera [ "+buf+" ]");
            }
            
        }while(!comand.equals("quit"));
        
        // Zamknięcie połączenia ze strony klienta
        skt.close();
        System.out.println("Klient - Odłączony");
 
    }
    catch (IOException ex){
        ex.printStackTrace();
        System.out.println("Coś nie działa!");
    }
    }
}