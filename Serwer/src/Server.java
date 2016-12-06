import java.io.*;
import java.net.*;
 
public class Server {
 
    public static void main(String[] args) {
        ServerSocket myServerSocket;
        try {
            myServerSocket = new ServerSocket(110);
 
            while (true) {
                try {
 
                    // klasa zarzadzajaca, odbiera rozkaz i go wykonuje
                    Post poczta = new Post();
 
                    // Oczekiwanie na po³¹czenie od hosta
                    System.out.println("Serwer: Start na hoœcie-" + InetAddress.getLocalHost().getCanonicalHostName()
                            + " port: " + myServerSocket.getLocalPort());
                    Socket skt = myServerSocket.accept();
 
                    // do odbierania i wysylania tekstu
                    BufferedReader Input = new BufferedReader(new InputStreamReader(skt.getInputStream())); // odczyt
                    PrintStream Output = new PrintStream(skt.getOutputStream()); // zapis
                    String buf = null;
                    String out = null;
 
                    // do wysylania plikow
                    // InputStream inputFileData=null;
 
                    do {
 
                        // Próba odczytania wejœcia ze strumienia
                        buf = Input.readLine();
                        out = null;
                        // Sprawdzenie, czy serwer odebra³ wiadomoœæ i próba
                        // odpisania hostowi
                        if (buf != null) {
                            System.out.println("Klient:  " + buf);
                            out = poczta.getOrder(buf);
                            Output.println(out); // wyslanie hostowi danych
 
                            // wysylanie zalacznikow
                            int iloscPlikowDoWyslania = poczta.kolejkaPlikow.size();
 
                            // wyslanie wiadomosci ile zalacznikow zostanie
                            // wyslanych
                            if (buf.startsWith("retr")) {
                                Output.println(iloscPlikowDoWyslania);
                               
 
                                BufferedOutputStream bos = new BufferedOutputStream(skt.getOutputStream());
                                DataOutputStream dos = new DataOutputStream(bos);
 
                                for (int i = 0; i < iloscPlikowDoWyslania; i++) {
 
                                    String fileName = poczta.kolejkaPlikow.poll();
                                    System.out.println(fileName);
                                    // wyslanie nazwy pliku
                                    Output.println(fileName);
 
                                    // wyslanie pliku
                                    File plik = new File(fileName);
                                    // wyslanie rozmiaru pliku
                                    Output.println(plik.length());
 
                                    /////////////////////////////////////////////////////////////////////////
 
                                    FileInputStream fis = new FileInputStream(plik);
                                    BufferedInputStream bis = new BufferedInputStream(fis);
                                    String potwierdzenie;
                                    int theByte = 0;
                                    while ((theByte = bis.read()) != -1) {
                                    	
                                        bos.write(theByte);
                                        //potwierdzenie=Input.readLine();
                                        //System.out.println(potwierdzenie);
                                    }
                                   
                                    bis.close();
                                    dos.flush();
                                    ////////////////////////////////////////////////////////////////////////
                                }
                                //dos.close();
                            }
                        }
 
                    } while (!buf.equals("quit"));
 
                    Input.close();
                    Output.close();
                    // Zamkniêcie po³¹czenia
                    skt.close();
                    System.out.println("Serwer - Od³¹czony");
                    // myServerSocket.close();
 
                } catch (FileNotFoundException e) {
                    System.out.println("B³¹d plik nie istnieje");
                }
 
               /* catch (IOException ex) {
                    ex.printStackTrace();
                    System.out.println("Uuuups, coœ siê skopa³o. nie podzia³am!");
                }*/
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // stworzenie gniazda servwera i przypisanie mu portu
 
    }
 
}