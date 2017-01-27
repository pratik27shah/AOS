import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/*
This code is for the Client Side .Consist of Peer working as Client and Server
*/
public class Client {
  @Deprecated public static void main(String[] args) {
	Client_Connection obClient_Connection=new Client_Connection();    
try{
	obClient_Connection.StartClient(); 	    
  }
	  catch(Exception ex){ ex.printStackTrace();}
     finally{obClient_Connection=null;}
}
}

/*
Purpose:Implements the client connection method
*/
class Client_Connection //implements Runnable
{
	Socket client_Socket = null;
    DataInputStream DataInstream ;
    PrintStream DataOutStream = null;
    DataInputStream SendDataToServer = null;
	String FileName_Receive;
	String Location=null;
	String Client_Messages[]=(new String[] {"connect","file","Peer","Enter Value 1,2,3 or 4 \n 1.Request File Name \n 2.Close Connection and System \n 3.Display File List \n 4.Register Folder Files with Index_Server"});
	int FileListCount=0; 
	int filesize=111000; 
	int bytesRead=0;
 
 
 /*Purpose: sends the server updated file list*/
  @Deprecated public void File_Transfer()
 { 
 try{
 	DataOutStream.println("sync");          
		    ObjectOutputStream  objectOutput = new ObjectOutputStream(client_Socket.getOutputStream());
			SendFileNames(objectOutput,0);
			objectOutput.flush();
			objectOutput=null;
 String responseLine = DataInstream.readLine();}
  catch(IOException ex) {DisplayError_Client(ex,"File_Transfer");}
}

 /*Purpose:Starts the client by reading configuration from config.properties and starts server thread of client*/
  @Deprecated public  void StartClient(){
	Location="Client_Connection/StartClient";
	try {
		ReadConfig objReadConfig=new ReadConfig();
		int Message_Count=0,Sync_flag=0; 
      client_Socket = new Socket(objReadConfig.GetServerIP(), Integer.parseInt(objReadConfig.GetServerPort()));
      DataOutStream = new PrintStream(client_Socket.getOutputStream());
      DataInstream = new DataInputStream(client_Socket.getInputStream());
      SendDataToServer = new DataInputStream(new BufferedInputStream(System.in));

	  if(client_Socket!=null)
	  new Thread(new Server_Connection()).start(); //Starts Server Thread of client
    } 
	catch (UnknownHostException e) {
		DisplayError_Client(e,Location);
      System.err.println("Cannot connect to HOST,check IP");
    } catch (IOException e) {
		DisplayError_Client(e,Location);
      System.err.println("Couldn't get I/O for the connection to host"+e);
    }
	/*Purpose:Client establishes connection  to Index Server*/
    if (DataInstream != null && DataOutStream  != null) {
	     try {
        System.out.println("The client Started.Connecting to Server...");
		String responseLine;
		String Filename=null;
       DataOutStream.println(Client_Messages[0].toLowerCase()); /*sends connect to  client for 1st time*/
	   int flag=1;	
	
  /**************Client-server communication************************************/
 while(true){	  

 responseLine = DataInstream.readLine();
 System.out.println(responseLine);
if(responseLine==null) {break;}

if(flag==1){
File_Transfer();
flag=0;}

if(responseLine.contains("Peer List"))
{

	try{
    ArrayList<String>DetailofPeerLocation=new ArrayList<String>();
	ObjectInputStream objectPeerList = new ObjectInputStream(client_Socket.getInputStream());
	Object object = objectPeerList.readObject();		
	PeerProcessing(object);
	DetailofPeerLocation=null;
	}
	catch(ClassNotFoundException ex) {DisplayError_Client(ex,"Respones of Peer List");}
}

System.out.println(Client_Messages[3]);   
String Send=SendDataProcess(SendDataToServer.readLine());
DataOutStream.println(Send.toLowerCase()); 
}        

DataOutStream.close();
DataInstream.close();
} catch (UnknownHostException e) {
        System.err.println("Client Trying to connect to unknown host: " + e);
		DisplayError_Client(e,Location);
      } catch (IOException e) {DisplayError_Client(e,Location);}
    } 
	
}

public void CloseClientSocket(Socket ClientSocket)
{
	try{
		ClientSocket.close();
	}
	catch(IOException ex){DisplayError_Client(ex,"");}
	
}

/*Purpose:Gets Peer List where File exsist from Server on client request*/
@SuppressWarnings( "unchecked" )
 @Deprecated public void PeerProcessing(Object objPeerInformation)
{
	ArrayList<String> PeerList = new ArrayList<String>();
	try{	
    PeerList =  (ArrayList<String>) objPeerInformation;
	System.out.println("-----Peer List Where File Exsist------");
int i=0,j=1;
System.out.println("Sr no.-------Peer-Id");
if(PeerList.size()!=0){
						while(i<PeerList.size()){
						System.out.println(j+"------------>"+PeerList.get(i));
						j++;
						i++;
						}
						
System.out.println("Enter any shown Sr no. or q to exit");	
String InputValue=SendDataToServer.readLine();
if(InputValue.equals("q") || InputValue.equals("Q")){System.out.println("File download cancelled");}
	 else
ConnecttoDownloadFile(PeerList.get(Integer.parseInt(InputValue)-1));
}
	else
	System.out.println("File Not Found at any Peer");}
	catch(IOException ex){DisplayError_Client(ex,"PeerProcessing");}
	finally{PeerList=null;}
}

/*Purpose:Processes the User inputs
1.File Transfer Request
2.Closed Connection
3.Displays folder file list
*/

 @Deprecated public String SendDataProcess(String Send)
{	
try{
	
	while(true){	 
	 if(Send.equals("1") || Send.equals("2")){	 
	 
		if(Send.equals("1")){
		FileName_Receive=null;
		System.out.println("Enter FileName:");
		FileName_Receive=SendDataToServer.readLine().toString();
	
		Send=Client_Messages[Integer.parseInt(Send)]+" "+FileName_Receive;
		}
		else if(Send.equals("2"))
		{CloseClientSocket(client_Socket);System.out.println("Closing Socket");System.exit(0);}
		else
	Send=Client_Messages[Integer.parseInt(Send)];
		
		break;

}

else if(Send.equals("3")){SendFileNames(null,1); System.out.println("\n"+Client_Messages[3]);}
else if(Send.equals("4")){System.out.println("\n Registering Files with Index Server");File_Transfer();System.out.println("\n"+Client_Messages[3]);}
	 else
	 System.out.println("Enter 1 or 2 or 3 or 4");
	 Send=SendDataToServer.readLine();

	 }
	
	 return Send;
}
catch(IOException ex) {DisplayError_Client(ex,"SendDataProcess");}
return "";

}

/*Purpose:Sends File Names(added/deleted) to indexing Server from the folder 
2 List are created which have the list of added/deleted files of the folder
the list is stored in a hashmap and send to the Index Server
*/
@SuppressWarnings( "unchecked" )
 @Deprecated public void SendFileNames(ObjectOutputStream objectOutput,int display)
{
	List<String> AddedFiles =  new ArrayList<String>();
	File folder = new File(".");
	
try{
File[] listOfFiles = folder.listFiles();
int i=0;

for (File fileNames : listOfFiles) {

AddedFiles.add(i,fileNames.getName().toLowerCase());
		if(display==1){
		System.out.println(i+" "+fileNames.getName());}
i++;}
		if(display==0){
		objectOutput.writeObject(AddedFiles); }

}
catch(IOException ex) {DisplayError_Client(ex,"SendFileNames");}	
finally{AddedFiles=null;}

}

/*Purpose:Compares the file folder to check if file is added/deleted*/
public boolean FileCompare()
{
File folder = new File(".");
try{

File[] listOfFiles = folder.listFiles();
	if(listOfFiles.length!=FileListCount){
	FileListCount=listOfFiles.length;
	return true;}
return false;
}	catch(Exception ex){DisplayError_Client(ex,"FileCompare");return false;}
	finally{folder=null;}

	}

/*Purpose:Connects to other Peer(Server ) for File Download*/
public void ConnecttoDownloadFile(String IP)
{
	 ReadConfig objReadConfig=new ReadConfig();
try {
			System.out.println("\nStarted Receiving file"+FileName_Receive);
	    Socket socket = new Socket(IP,Integer.parseInt(objReadConfig.GetClientPort()));
		byte [] bytearray  = new byte [filesize];
	    PrintStream DataOutStream = new PrintStream(socket.getOutputStream());	
        DataOutStream.println(FileName_Receive);		 
        InputStream is = socket.getInputStream();
		FileOutputStream fos = new FileOutputStream(FileName_Receive);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	    bytesRead = is.read(bytearray,0,bytearray.length);
	     int currentTotalValue = bytesRead;
		
	    while(bytesRead > -1) {
	       bytesRead =is.read(bytearray, currentTotalValue, (bytearray.length-currentTotalValue));
		  if(bytesRead >= 0) currentTotalValue += bytesRead;
		 } 
if(currentTotalValue!=-1){
	    bos.write(bytearray, 0 , currentTotalValue);
System.out.println("\nFile Received"+FileName_Receive);}
else{System.out.println("File Not Found at Peer or File is corrupted ");

}
	    bos.flush();
        fos.close();		
	    bos.close();
		DataOutStream.close();
	    socket.close();
		File_Transfer();
		
	  }

catch(IOException ex ){System.out.println("File Not Found at Peer ");}
finally{objReadConfig=null;}
	  }


	  public void DisplayError_Client(Exception ex,String Details)
{
	ErrorHandler objError=new ErrorHandler();
	objError.LogError(ex,Details);
}
	  
}/*Class closed*/

/*Purpose:Creates a New Peer Server for File Transfer*/
  class Server_Connection implements Runnable
{
public void run(){
Sockets_Functions  objServerSocket=new Sockets_Functions(null);
objServerSocket.Sockets_Set_Connection();
	}
}

/*Purpose:Creates a Peer(Server) which listening continously for other connection and also sends file to other peers*/

 class Sockets_Functions extends Thread
{
ServerSocket server;
DataInputStream DataInput;
DataInputStream DataInStream;
PrintStream DataPrintStream;
String Client_Send_Data;
Socket objConnection;
	
public Sockets_Functions(Socket objConnection)
{
	this.objConnection=objConnection;
	
}
	
	/*Purpose:Set up Server-Client Connection*/
     public void Sockets_Set_Connection()
    {
	   try{
		   ReadConfig objReadConfig=new ReadConfig();
      			
			System.out.println(InetAddress.getLocalHost());
				server = new ServerSocket(Integer.parseInt(objReadConfig.GetClientPort()));
				} catch (IOException ex) {
            		System.out.println("Server"+ex);
       	 }

  	 while(true){
				try {
					try{					
						objConnection=server.accept();						
						new Sockets_Functions(objConnection).start();
						}
				catch(NullPointerException ex){
					ex.printStackTrace();
					break;			}
				}
   
   
   catch(IOException ex){closeConnection(objConnection);break;}
		}
	}
	
	  @Override
	  /*Purpose:Sends file to client(Peer) requesting it */
@Deprecated public  void run()
{
	try{
		 
			  DataInput =new  DataInputStream(objConnection.getInputStream());
			  String FileNametoSend=DataInput.readLine();
			  System.out.println("Sending File"+FileNametoSend);
			  File objTransferFile = new File(FileNametoSend);
			  
			  FileInputStream objFileinput = new FileInputStream(objTransferFile);
			
		      BufferedInputStream objbufferedInStream = new BufferedInputStream(objFileinput);
		      byte [] TransferBytearray  = new byte [(int)objTransferFile.length()];
	          objbufferedInStream.read(TransferBytearray,0,TransferBytearray.length);
		      OutputStream objoutStream = objConnection.getOutputStream();
		      objoutStream.write(TransferBytearray,0,TransferBytearray.length);
		      objoutStream.flush();
		    closeConnection(objConnection);
			  System.out.println("File transfer complete.File---"+ FileNametoSend +"---Send Successfully");
			  TransferBytearray=null;
			  objbufferedInStream=null;
			  objFileinput=null;
			  objTransferFile=null;
		      
	}
	catch(IOException ex){ System.out.println("File has been changed/does not exsist");closeConnection(objConnection);
	}
	
}		



/*Purpose:close Socket Connection of peer as a Server*/
public void closeConnection(Socket objConnection)
	{
	try{
	objConnection.close();
	}
catch(IOException ex) {
DisplayError_ServerPeer(ex,"closeConnection");}
	
}

public void DisplayError_ServerPeer(Exception ex,String Details)
{
	ErrorHandler objError=new ErrorHandler();
	objError.LogError(ex,Details);
}
}
/*Purpose Reads value from config.Properties file*/

 class ReadConfig
{
Properties objProp = new Properties();
FileInputStream InputConfigStream;
					
					
		public ReadConfig()
		{
			try{
				InputConfigStream = new FileInputStream("config.properties");
			objProp.load(InputConfigStream);
			}
		catch(IOException ex){}	
		}	
		public String GetServerPort()
	{return (objProp.getProperty("serverport").trim());}	
	
		public String GetServerIP()
	{return (objProp.getProperty("serverIP").trim());}	
	
public String GetClientPort(){return (objProp.getProperty("clientPort").trim());}
	}

 class ErrorHandler
{
	public void LogError(Exception ex,String MethodName){
try{
FileWriter objWriter = new FileWriter("ClientErrorLog.txt", true);
PrintWriter  ps = new PrintWriter (objWriter);
DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
Date objdate = new Date();
objWriter.write("\n["+ dateFormat.format(objdate)+"]"+MethodName);
ex.printStackTrace(ps);
objWriter.close();
objdate=null;
objWriter=null;}
catch(IOException e){ }	
}}