
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
/**
 *
 * @author pratikshah
 */
public class Index_Server extends Thread{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)        // TODO code application logic here
    {
	Server_Connection  o1=new Server_Connection ();
	}
 }

/**/
class Server_Connection 
{

public Server_Connection (){
	try{
	Sockets_Functions  objServerSocket=new Sockets_Functions(null,null,"");
	objServerSocket.Sockets_Set_Connection();}
	catch(Exception ex){ex.printStackTrace();}
}
}

 class Sockets_Functions extends Thread
{
ServerSocket server;
DataInputStream DataInput = null;
DataInputStream DataInStream;
String[] Return_Codes=(new String[] {"Check->IP,Invalid Connection","OK"});
PrintStream DataPrintStream;
String Client_Send_Data=null;
String Name;
int FileNumbers=0;
Properties objProp = new Properties();
InputStream InputConfigStream = null;
int Client_Number=1;
 
 /*
 HashTable=(FileName(key),PeerName(value))
 HashTable_IP=(Peer Name(key),IP(value))
 */
 
public static final HashMap<String,ArrayList> HashTable = new HashMap<String,ArrayList>();
public static final HashMap<String,String> HashTable_IP = new HashMap<String,String>();
public static final HashMap<String,ArrayList> HashFileTable = new HashMap<String,ArrayList>();

Socket objConnection;
	/*Purpose:Set up Socket Details*/
	
	public Sockets_Functions(DataInputStream DataInput ,Socket objConnection,String Name)
	{
	this.DataInput =DataInput ;
	this.objConnection=objConnection;
	this.Name=Name;
	}

	/*Purpose:Set up Server-Client Connection*/
     public void Sockets_Set_Connection()
    {
			try{     	
					Properties objProp = new Properties();
					InputConfigStream = new FileInputStream("config.properties");
					objProp.load(InputConfigStream);
					System.out.println("Server Address-->"+objProp.getProperty("serverIP"));
    			    server = new ServerSocket(Integer.parseInt(objProp.getProperty("serverport").trim()), 20);
			} 			catch (IOException ex) { }

  	 while(true){
				try {
				objConnection=server.accept();
				String Name="Peer- "+objConnection.getInetAddress();
				System.out.println("Connected---->"+Name+" "+objConnection);
				HashTable_IP.put(Name,objConnection.getInetAddress().toString().substring(1));
				new Sockets_Functions(DataInput ,objConnection,Name).start();			
				}catch(IOException ex){closeConnection(objConnection);break;}
		}
}

/*Purpose:Close Socket Connection*/
public void closeConnection(Socket objConnection)
	{
	try{
		System.out.println("\n"+objConnection + Name +"Closed and Disconnected");
		HashTable.remove(Name);
		objConnection.close();}
catch(IOException ex) {}
}

  @Override
  
  /*Runs Socket (Server) Connection*/
public void run()
{
            try {
				DataPrintStream = new PrintStream(objConnection.getOutputStream());
				DataInStream = new DataInputStream(new BufferedInputStream(System.in));
				DataInput =new  DataInputStream(objConnection.getInputStream());
			}
catch(IOException ex) {closeConnection(objConnection);}
int Flag=1;
 while(true){
				try{ 
					Flag=1;
					Client_Send_Data = DataInput.readLine();
					/*
Console console = System.console();
String input = console.readLine("Enter input:");
if(input=="1")
{System.out.println("clodes");}*/
	
					if(Client_Send_Data==null){closeConnection(objConnection);break;}
				if(Client_Send_Data.substring(0,4).equals("file")){
					try{
						String FileName=Client_Send_Data.substring(5);	
						System.out.println("File processing "+FileName);
						DataPrintStream.println("Peer List");
						ObjectOutputStream objectOutput = new ObjectOutputStream(objConnection.getOutputStream());
						Search_Peers_WhereFileExsist(FileName,objectOutput);
						Flag=0;
						}catch(Exception ex){System.out.println("error");}
					}

		if(Client_Send_Data.equals("sync")){
							try{
								ObjectInputStream objectInputFileList = new ObjectInputStream(objConnection.getInputStream());
								Object object = objectInputFileList.readObject();
								GetFileList(object,Name);
							}catch(ClassNotFoundException ex){}
			}
CreateServerLoggerFile(Client_Send_Data.toLowerCase(),Name,SetUpClient(Client_Send_Data.toLowerCase(),Flag));
	if(Flag==1){DataPrintStream.println(" " + Client_Send_Data+" Successfully");}

/*Purpose Sends Ack message to client*/
}       
catch (IOException ex) {break;}            
}//while closed

}
/*Purpose:Writes the client-server communication in a file log.txt*/
public void CreateServerLoggerFile(String ClientMessage,String ClientName,String Server_Response)
{
try{
	
FileWriter objWriter = new FileWriter("log.txt", true);
DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
Date objdate = new Date();
objWriter.write("\n["+ dateFormat.format(objdate)+"]"+ClientName+"//"+objConnection +" ---"+ClientMessage);
objWriter.write("  Server---Message to --"+ClientName+"---"+Server_Response);
objWriter.close();
objdate=null;
objWriter=null;}
catch(IOException ex){System.out.println(ex);}	
}
/*Purpose:Deletes the file from Hashmap,that have been deleted from the client Folder*/
public void UpdateFileHaspMap(ArrayList<String>FileList,String PeerName)
{
	List<String> sources = new ArrayList<String>(FileList);
	ArrayList<String> TempArrayList=new  ArrayList<String>(FileList);
	List<String> destinationList = new ArrayList<String>();
try{

	
	if(HashFileTable.get(PeerName)!=null){
destinationList=HashFileTable.get(PeerName);
 destinationList.removeAll(sources);
FileList=null;
FileList=(ArrayList<String>)destinationList ;
for(int i=0;i<FileList.size();i++){
	if(HashTable.get(FileList.get(i))!=null && HashTable.get(FileList.get(i)).size()>=1){
		if(HashTable.get(FileList.get(i)).remove((PeerName).toString()))
		{}
	}
	else
	HashTable.remove(FileList.get(i));
}
	
	
	}

		HashFileTable.put(PeerName,TempArrayList);
	
}
catch(Exception ex){ex.printStackTrace();}
finally{destinationList=null;sources=null;}	
	
}

/*Purpose:Updates HashMap,after receving a new list of added/deleted files*/
public void GetFileList(Object objectInput,String PeerName)
{
	 
	 
	ArrayList<String> FileList = new ArrayList<String>();
	FileList=(ArrayList<String>)objectInput;

	try{	
   UpdateFileHaspMap(FileList,PeerName);
	int i=0,totalCount=FileList.size(); 
	for( i=0;i<totalCount;i++){ ArrayList<String>IP=new ArrayList<String>();
		
	if(HashTable.get(FileList.get(i))!=null) 
	{
		IP=(ArrayList<String>)HashTable.get(FileList.get(i));
		if (!(IP.contains(PeerName))) {IP.add(IP.size(),PeerName);}
	}			
	
	else	
	IP.add(0,PeerName);
	
	HashTable.put(String.valueOf(FileList.get(i)).toLowerCase(),IP);
	IP=null;
	}
	
}
catch(Exception ex){ex.printStackTrace();FileList=null;}
finally{FileList=null;}
	
}

/*Purpose:Find Files peers*/
public void  Search_Peers_WhereFileExsist(String SearchFileName,ObjectOutputStream objectOutput)
{
	ArrayList<String> PeerAddress=new ArrayList<String>();
	ArrayList<String> SendPeerAddress=new ArrayList<String>();
	try{	
	int j=0,i,flag=0,total; 	
	if(HashTable.get(SearchFileName)!=null )
	{PeerAddress=HashTable.get(SearchFileName);	}
		if(PeerAddress.size()>0){System.out.println("File Location Found in Server");}
		else{System.out.println("No Peer with this File Found");}
		for(j=0;j<PeerAddress.size();j++){
	   SendPeerAddress.add(j,HashTable_IP.get(PeerAddress.get(j)));
}
	objectOutput.writeObject(SendPeerAddress);
	}
catch(IOException ex ){ex.printStackTrace();}
finally{PeerAddress=null;SendPeerAddress=null;}
}

/*Purpose:Processe the client request messages*/
public String SetUpClient(String Client_Send_Data,int Flag)
{
//try{
	
	int Return_Status=0;
	if(Flag==1){
	if(Client_Send_Data.equals("connect") || Client_Send_Data.equals("sync"))
			{
				Return_Status= 1;
				
			}
			else 
			{
				Return_Status= 0;
			}

if(Return_Status==1){return Return_Codes[1];}
else {return Return_Codes[0];}
	}
	
	return Client_Send_Data;
} 
}/*Class closed*/




	
	/**/
