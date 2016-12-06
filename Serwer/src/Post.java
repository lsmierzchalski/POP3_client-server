import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Post {
	//gdy uzytkownik jest w bazie zapamietuje jego pozycje w pliku
	private int pozycja=0;
	private String actualUser=null;
	private boolean zalogowany=false;
	public boolean wysylPlikow=false;
	public Queue<String> kolejkaPlikow=new PriorityQueue<String>();
	
	//funkcja sprawdzajaca czy uzytkownik jest w bazie
	//user
	private boolean checkUser(String user){
		try {
			Scanner check=new Scanner(new File("users.txt"));
			//przeszukiwanie pliku 
			this.pozycja=0;
			while(check.hasNext()) {
				this.pozycja++;
				if(user.equals(check.next())){
					check.close();
					this.actualUser=user;
					return true;
				}
				else if(check.hasNext())
					check.next();
			}
			check.close();
			this.pozycja=0;
			return false;
		}
		catch(FileNotFoundException  e){
			System.out.println("B³¹d otwarcia pliku");
		}
		return true;
	}
	
	//funkcja sprawdzajaca poprawnosc hasla
	//pass
	private boolean checkPassword(String password){
		try {
			//sprawdzenie czy uzyto komendy "user"
			if(this.pozycja==0||this.actualUser.equals(null))
				return false;
			
			Scanner check=new Scanner(new File("users.txt"));
			//przeszukiwanie pliku 
			for(int i=0;i<pozycja-1;i++) {
				if(check.hasNextLine())
					check.nextLine();
			}
			check.next();
			if(password.equals(check.next())){
				check.close();
				this.zalogowany=true;
				return true;
			}
			else {
				check.close();
				pozycja=0;
				return false;
			}
		}
		catch(FileNotFoundException  e){
			System.out.println("B³¹d otwarcia pliku");
		}
		return false;
	}
	
	//funkcja zwracajaca ilosc wiadomosci
	//list
	private String getMessagesNumber(){
		try {
			//sprawdzenie czy uzytkownik jest zalogowany
			if(this.actualUser.equals(null)||this.pozycja==0||!this.zalogowany)
				return "You are not logged in";
			Scanner check=new Scanner(new File(this.actualUser+".txt"));
			if(check.hasNextLine()) {
				String help=check.nextLine();
				check.close();
				return help;
			}
			else {
				check.close();
				return "0";
			}
		} catch (FileNotFoundException e) {
			return "Blad otwarcia pliku";
		}
	}
	
	//funkcja zwracajaca wiadomosc
	//retr
	private String getMessage(String id){
		try{
			//sprawdzenie czy uzytkownik jest zalogowany
			if(this.actualUser.equals(null)||this.pozycja==0||!this.zalogowany)
				return "You are not logged in";
			
			int messageID=Integer.parseInt(id);
			
			//sprawdzenie czy istnieje wiadomosc o danym indeksie
			int liczbaWiadomosci=Integer.parseInt(this.getMessagesNumber());
			
			if(messageID>liczbaWiadomosci||messageID<0)
				return "Nie ma wiadomosci o takim indeksie";
			
			Scanner check=new Scanner(new File(actualUser+".txt"));
			//szukanie wiadomosci o danym numerze
			for(int i=0;i<messageID;){
				if(check.hasNextLine()) {
					if(check.nextLine().equals("//////////"))
						i++;
				}
			}
			
			//czytanie wiadomosci
			String message=new String("Email from: ");
			boolean czykoniec=false;
			do{
				if(check.hasNextLine()) {
					String help=check.nextLine();
					
					//jezeli zostal znaleziony znacznik pod ktorym sa nazwy plikow do wyslania
					if(wysylPlikow&&!help.equals("//////////"))
						this.kolejkaPlikow.add(help);
					else
						message+=help;
					
					//jezeli koniec wiadomosci
					if(message.endsWith("//////////"))
							czykoniec=true;
					//jezeli pojawil sie znacznik po ktorym sa zalaczniki(nazwy plikow)
					else if(message.endsWith("<zzz>")&&!this.wysylPlikow) {
						this.wysylPlikow=true;	
						message+="\n";
					}
					else if(!this.wysylPlikow)
						message+="\n";
					
					
				}
				
				
			}while(!czykoniec);
			wysylPlikow=false;
			check.close();
			return message;
			
				
		} catch(NumberFormatException e) {
			System.out.println("Zla komenda, po retr musi byc int");
		} catch(FileNotFoundException e){
			System.out.println("Blad otwarcia pliku");
		}
		
		return "0";
	}
	
	private String deleteMessage(String id){
		
		try {
			//sprawdzenie czy uzytkownik jest zalogowany
			if(this.actualUser.equals(null)||this.pozycja==0||!this.zalogowany)
				return "You are not logged in";
			
			Scanner pop=new Scanner(new File(this.actualUser+".txt"));
			PrintWriter push=new PrintWriter(this.actualUser+"_copy.txt");
			
			int messagesNumber;
			int deleteId=Integer.parseInt(id);
			
			//zmniejszenie liczby wiadomosci o 1
			if(pop.hasNextLine()) {
				messagesNumber=Integer.parseInt(pop.nextLine());
				//liczba poza przedzialem
				if(deleteId>messagesNumber||deleteId<0) {
					pop.close();
					push.close();
					return "Nie ma wiadomosci o takim indeksie";
				}
				messagesNumber--;
				
			}
			else {
				pop.close();
				push.close();
				return "Blad pliku";
			}
			
			//wpisanie do nowego pliku liczbe wiadomosci
			push.println(messagesNumber);
			
		//stworzenie nowego pliku bez wiadomosci ktora usuwamy
			String help=null;
			
			//przepisanie wszystkiego do rozpoczecia sie wiadomosci ktora chcemy usunac
			for(int i=0;i<deleteId;){
				if(pop.hasNextLine())
					help=pop.nextLine();
				//dotarcie do separatora wiadomosci
				if(help.equals("//////////"))
					i++;
				push.println(help);				
			}
			//pomijanie wiadomosci(usuwanie)
			do{
				help=pop.nextLine();
			}while(!help.equals("//////////"));
			//zapisanie reszty pliku
			while(pop.hasNextLine()){
				help=pop.nextLine();
				push.println(help);
			}
			//zamykanie strumieni czytania/pisania do plików
			push.close();
			pop.close();			
						
			//zamiana plikow
			File oldFile=new File(this.actualUser+".txt");
			File newFile=new File(this.actualUser+"_copy.txt");
			
			oldFile.delete();
			newFile.renameTo(oldFile);
			
			return "+OK";
			
		} catch (FileNotFoundException e) {
			System.out.println("Blad otwarcia pliku");
		} catch(NumberFormatException e) {
			System.out.println("Zla komenda, po dele musi byc int");
		}
		return "+OK";
	}
	
	public String getOrder(String order){
		
		if(order.length()==4){
			switch(order) {
			case "list":
				return this.getMessagesNumber();
			case "quit":
				this.actualUser=null;
				this.pozycja=0;
				this.zalogowany=false;
				return "+OK";
			default:
				return "Z³a komenda!";
			}
			
		
		}else {
			if(order.startsWith("retr ")) {
				return this.getMessage((order.substring(5)));
			}
			else if(order.startsWith("user ")) {
				if(this.checkUser((order.substring(5))))
					return "+OK";
				else 
					return "+NOT OK";
			}
			else if(order.startsWith("pass ")) {
				if(this.checkPassword((order.substring(5))))
					return "+OK";
				else 
					return "+NOT OK";
			}
			else if(order.startsWith("dele ")) {
				return this.deleteMessage(order.substring(5));
			}
			else return "Zlakomenda!";
		}
	}
}
