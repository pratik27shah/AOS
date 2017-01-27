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
	new Thread(new Server_Connection()).start();
	obClient_Connection.StartClient(); 	    
  }
  //catch(IOException ex){ ex.printStackTrace();}
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
	boolean Status=true;
	String Server_IP=null;
	String delimiters = "\\$";
	String Client_Messages[]=(new String[] {"connect","file","Peer","Enter Value 1,2,3,4 or 5 \n 1.Request File Name \n 2.Close Connection and System \n 3.Display File List \n 4.Register Folder Files with Decentralized Server \n 5. Replicate Files"});
	int FileListCount=0; 
	int filesize=111000; 
	int bytesRead=0;
	int Get_ReplicationIP=0;
 ArrayList<String> ArrayList_ServerList=new ArrayList<String>();
 public static final Hashtable<Integer,Integer> Server_Replica_Status=new Hashtable<Integer,Integer>();
 public static final Hashtable<Integer,PrintStream> SendData=new Hashtable<Integer,PrintStream>();
public static final Hashtable<Integer,DataInputStream> GetData=new Hashtable<Integer,DataInputStream>();
public static final Hashtable<Integer,Socket> HashStorageSocket = new Hashtable<Integer,Socket>();
public static final Hashtable<Integer,ObjectOutputStream> HashStorageObjectOutputStream = new Hashtable<Integer,ObjectOutputStream>();
public static final Hashtable<Integer,PrintStream> HashStoragePrintStream = new Hashtable<Integer,PrintStream>();
int Total_Servers;
 
 /*Purpose: sends the server updated file list*/
  @Deprecated public void File_Transfer()
 { 
 try{
 			SendFileNames(0);
						
 }//String responseLine = DataInstream.readLine();}
  catch(Exception ex) {DisplayError_Client(ex,"File_Transfer");}
}

 /*Purpose:Starts the client by reading configuration from config.properties and starts server thread of client*/
  @Deprecated public  void StartClient(){
	Location="Client_Connection/StartClient";
	try {
		ReadConfig objReadConfig=new ReadConfig();
		ServerReadConfig objServerReadConfig=new ServerReadConfig();
		int Message_Count=0,Sync_flag=0; 
		Get_Server_IP_Hash objGet_Server_IP_Hash=new Get_Server_IP_Hash();
		ArrayList_ServerList=objGet_Server_IP_Hash.ServerIPHash();
		//ReadConfig objReadConfig=new ReadConfig();
	 Total_Servers=Integer.parseInt(objReadConfig.GetTotalServers());
		Get_ReplicationIP=Integer.parseInt(objServerReadConfig.GetServerNumber());
	  SendDataToServer = new DataInputStream(new BufferedInputStream(System.in));
File_Transfer();
} 
	catch (Exception e) {
		DisplayError_Client(e,Location);
      //System.err.println("Cannot connect to HOST,check IP");
    } 
	 int flag=1;	
	String responseLine;
	System.out.println(Client_Messages[3]);
  /**************Client-server communication************************************/
 try{
 while(true){	  

//System.out.println("1");
 responseLine=SendDataProcess(SendDataToServer.readLine());

if(responseLine==null) {break;}
System.out.println(responseLine);
System.out.println(Client_Messages[3]);   
 
}        

DataOutStream.close();
DataInstream.close();
} catch (UnknownHostException e) {
        System.err.println("Client Trying to connect to unknown host: " + e);
		DisplayError_Client(e,Location);
      } catch (IOException e) {DisplayError_Client(e,Location);}
		 }     

public void SetUp_OneTimeSocketConnections(int prmValue)
{
	
	 ReadConfig objReadConfig=new ReadConfig();
      	ServerReadConfig objServerReadConfig=new ServerReadConfig();
	try{
		
		Server_IP=ArrayList_ServerList.get(prmValue);
		 if(HashStorageSocket.get(prmValue)==null){
		client_Socket = new Socket(Server_IP,Integer.parseInt(objReadConfig.GetServerPort(prmValue)));
      	HashStorageSocket.put(prmValue,client_Socket);
		  DataOutStream = new PrintStream(client_Socket.getOutputStream());
		DataInstream = new DataInputStream(client_Socket.getInputStream()); 
		DataOutStream.println(Integer.parseInt(objReadConfig.GetPeerPort(Integer.parseInt(objServerReadConfig.GetServerNumber()))));
		SendData.put(prmValue,DataOutStream);
		 GetData.put(prmValue,DataInstream);}
		 }
	
	catch(IOException ex){DisplayError_Client(ex,"SetUp_OneTimeSocketConnections");}
	finally{objReadConfig=null;objServerReadConfig=null;}
	
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
 @Deprecated public void PeerProcessing(String prmPeerInformation)
{
	ArrayList<String> PeerList = new ArrayList<String>();
	try{	
    //PeerList =  (ArrayList<String>) objPeerInformation;
	
int i=0,j=1;

if(!(prmPeerInformation.equals("-"))){

 String[] tokensVal = prmPeerInformation.split(delimiters);

   int k=-1;
   for(String token : tokensVal) {
PeerList.add(++k,token.replaceAll(delimiters,""));	    
   
   } 	
tokensVal=null;	
System.out.println("-----Peer List Where File Exsist------");
System.out.println("Sr no.-------Peer-Id");
						while(i<PeerList.size()-1){
						System.out.println(j+"------------>"+PeerList.get(i));
						j++;
						i++;
						}
						
System.out.println("Enter any shown Sr no. or q to exit");	

String InputValue=SendDataToServer.readLine();

if(InputValue.equals("q") || InputValue.equals("Q")){System.out.println("File download cancelled");}
	 else{
 ConnecttoDownloadFile(PeerList.get(Integer.parseInt(InputValue)-1).split("/")[0],Integer.parseInt(((PeerList.get(Integer.parseInt(InputValue)-1)).split("/"))[1]),true);
	 }
}
	else
	System.out.println("File Not Found at any Peer");}
	catch(IOException ex){DisplayError_Client(ex,"PeerProcessing");}
	finally{PeerList=null;}
}

public void ProcessReplicas(){//String prmPeerInformation){
File folder =new File(".");
File[] listOfFiles = folder.listFiles();
int i=0,HashFile_Value,j=0,ReplicateFactor;
ArrayList<String> PeerList = new ArrayList<String>();
ReadConfig objReadConfig=new ReadConfig();
objReadConfig.GetReplicationFactor();
try{
System.out.println("Taking and Creating Replicas");
ReplicateFactor=objReadConfig.GetReplicationFactor();
String prmPeerInformation=GetReplica_PeerDetails(ReplicateFactor);
if(prmPeerInformation!="-"){
 String[] tokensVal = prmPeerInformation.split(delimiters);
   int k=-1;

   for(String token : tokensVal) {
PeerList.add(++k,token.replaceAll(delimiters,""));	    
  } 
//System.out.println("PROCESS"+PeerList);
while(j<ReplicateFactor){
System.out.println("\nReplication in progress");
for (File fileNames : listOfFiles) {
FileName_Receive=fileNames.getName();
//System.out.println(FileName_Receive);
 SendReplicaFiles(PeerList.get(j).split("/")[0],Integer.parseInt(((PeerList.get(j)).split("/"))[1]),false);
//ConnecttoDownloadFile(PeerList.get(j).split(delimiters)[0],((PeerList.get(j).split("/"))[1]);
}
j++;
}}
System.out.println("Replication completed");
 System.out.println("\n"+Client_Messages[3]);
}
catch(Exception ex){}
finally {listOfFiles=null;PeerList=null;objReadConfig=null;}
}



/*Used for replication of files*/
public String GetReplica_PeerDetails(int TotalReplicas)
{
String SendData_IP="-";
String PeerIP=null;
int PeerPort=0;
ReadConfig objReadConfig=new ReadConfig();
ServerReadConfig objServerReadConfig=new ServerReadConfig();
try{
int TotalPeers=objReadConfig.GetTotalPeers();
int CurrentPeer=Integer.parseInt(objServerReadConfig.GetServerNumber());
for(int i=0;i<TotalPeers;i++){
/*PRATIK*/
		try{
if(TotalPeers<=TotalReplicas) break;
if(i!=CurrentPeer){
PeerIP=objReadConfig.GetPeerIP(i);
PeerPort=Integer.parseInt(objReadConfig.GetPeerPort(i));
Socket socket=null;
//System.out.println(PeerPort);
socket =TempCreateSocket(socket,PeerIP,PeerPort);
if(socket!=null){
SendData_IP=PeerIP+"/"+PeerPort+"$"+SendData_IP;

socket.close();
socket=null;}
}
		}

		catch(IOException ex) {}
}}
catch(Exception ex) {}
return SendData_IP;
}

public Socket TempCreateSocket(Socket socket,String IP,int PeerPort)
{
try{
socket=new Socket(IP,PeerPort);
}
catch(IOException ex){socket=null;}
return socket;
}

/*Purpose:Processes the User inputs
1.File Transfer Request
2.Closed Connection
3.Displays folder file list
*/

 @Deprecated public String SendDataProcess(String Send)
{	
int Hash_Value=-1,flag=0;
try{
	
	while(true){	 
	 if(Send.equals("1") || Send.equals("2")){	 
	 
		if(Send.equals("1")){
		FileName_Receive=null;
		flag=1;
		System.out.println("Enter FileName:");
		FileName_Receive=SendDataToServer.readLine().toString();
		Hash_Value=HashFunction(FileName_Receive);
		Send=Client_Messages[Integer.parseInt(Send)]+" "+FileName_Receive;
		DataOutStream=SendData.get(Hash_Value);
		//System.out.println(Hash_Value);	
		DataOutStream.println(Send);
		DataInstream=GetData.get(Hash_Value);
		Send=DataInstream.readLine();
if(Send==null){Send=Client_Messages[1]+" "+FileName_Receive;Send=GetFromReplica(Hash_Value,Send);}
	PeerProcessing(Send);
	}
		else if(Send.equals("2"))
		{CloseClientSocket(client_Socket);System.out.println("Closing Socket");System.exit(0);}
		else
	Send=Client_Messages[Integer.parseInt(Send)];
		

		break;

}

else if(Send.equals("3")){File_Transfer();SendFileNames(1); System.out.println("\n"+Client_Messages[3]);}
else if(Send.equals("4")){System.out.println("\n Registering Files with Index Server");File_Transfer();System.out.println("\n"+Client_Messages[3]);}
else if(Send.equals("5")) {
	/*	flag=2;
	DateFormat dateformat=new SimpleDateFormat("ss");
Date obj=new Date();
		Hash_Value=Integer.parseInt(dateformat.format(obj))%Total_Servers;
		DataOutStream=SendData.get(Hash_Value);	
		DataOutStream.println("Copy");
		//DataOutStream.println(Send);
		DataInstream=GetData.get(Hash_Value);
		//Send=DataInstream.readLine();
		Send=DataInstream.readLine();
*/ProcessReplicas();
}
	 else
	 System.out.println("Enter 1 or 2 or 3 or 4 or 5");
	 Send=SendDataToServer.readLine();

	 }
	
	 
}
catch(IOException ex) {

	if(flag==2){Send=FileReplicas(Hash_Value,"Copy");ProcessReplicas();}
	if(flag==1){
	Send=GetFromReplica(Hash_Value,Send);
	PeerProcessing(Send);
	}else DisplayError_Client(ex,"SendDataProcess");}

return Send;}



public String FileReplicas(int Hash_Value,String Send)
{
	try{
		
		
	int New_Hash_Value=Server_Replica_Status.get(Hash_Value);
	DataOutStream=SendData.get(New_Hash_Value);


	DataOutStream.println(Send);
	DataInstream=GetData.get(New_Hash_Value);
	Send=DataInstream.readLine();
	
	HashStorageSocket.put(Hash_Value,client_Socket);
		SendData.put(Hash_Value,DataOutStream);
		 GetData.put(Hash_Value,DataInstream);
	
	}
	catch(Exception ex){}
	return Send;
}

public String GetFromReplica(int Hash_Value,String Send)
{
	try{
		
		System.out.println("Fetching Peer List from Replica");


int New_Hash_Value=Server_Replica_Status.get(Hash_Value);
//System.out.println(HashStorageSocket.get(New_Hash_Value)+" "+Send);
if(HashStorageSocket.get(New_Hash_Value)==null)

SetUp_OneTimeSocketConnections(New_Hash_Value);	

	DataOutStream=SendData.get(New_Hash_Value);
DataOutStream.println(Send);	
DataInstream=GetData.get(New_Hash_Value);
	
	
	Send=DataInstream.readLine();
	HashStorageSocket.put(Hash_Value,client_Socket);
		SendData.put(Hash_Value,DataOutStream);
		 GetData.put(Hash_Value,DataInstream);
	//System.out.println(FileName_Receive);
	}
	catch(Exception ex){ex.printStackTrace();}
	return Send;
}

/*Purpose:Sends File Names(added/deleted) to indexing Server from the folder 
2 List are created which have the list of added/deleted files of the folder
the list is stored in a hashmap and send to the Index Server
*/
@SuppressWarnings( "unchecked" )
 @Deprecated public void SendFileNames(int display)
{
	List<String> AddedFiles =  new ArrayList<String>();
	File folder =new File("."); //new File("".");
ArrayList<Integer> AddHashValues=new ArrayList<Integer>();	
Hashtable<Integer,String> FileHashTable=new Hashtable<Integer,String>();
String Temp_FileValue;
ReadConfig objReadConfig=new ReadConfig();
//int ReplicaStatus=objReadConfig.MetaDataReplication();
try{
File[] listOfFiles = folder.listFiles();
int i=0,HashFile_Value;

for (File fileNames : listOfFiles) {
	HashFile_Value=HashFunction(fileNames.getName());
		if(FileHashTable.get(HashFile_Value)!=null){
		Temp_FileValue=FileHashTable.get(HashFile_Value);
		FileHashTable.put(HashFile_Value,Temp_FileValue+"$"+fileNames.getName());	
		}

		else{
		FileHashTable.put(HashFile_Value,fileNames.getName());	
		AddHashValues.add(HashFile_Value);
SetUp_OneTimeSocketConnections(HashFile_Value);
		}


		if(display==1){
		System.out.println(i+" "+fileNames.getName());}
		i++;}


		if(display==0){
	 
			FileNames_Send_Replicate(FileHashTable.size(),AddHashValues,true,FileHashTable);
		}
}catch(Exception ex) {DisplayError_Client(ex,"SendFileNames");}	
finally{Temp_FileValue=null;AddHashValues=null;FileHashTable=null;}

}

public void FileNames_Send_Replicate(int Total_Size,ArrayList<Integer> AddHashValues,boolean ServerReplication,Hashtable<Integer,String> FileHashTable)
{
	try{
		int i=0,j=0,HashFile_Value,HashFile_Value_Replica;
		//System.out.println(Array_Value_List);
		for(i=0,j=i+1;i<Total_Size;i++,j++)
			{
				
				if(FileHashTable.get(AddHashValues.get(i))!=null){
				HashFile_Value=AddHashValues.get(i);
				Send_DataToServer(HashFile_Value,true,FileHashTable.get(AddHashValues.get(i)));
					//System.out.println();
					if(ServerReplication){
							if(j>=Total_Size)
							{
							
								Server_Replica_Status.put(HashFile_Value,AddHashValues.get(0));
								Send_DataToServer(AddHashValues.get(0),false,FileHashTable.get(0));
							}
							else
							{	//System.out.println(Array_Value_List);
		//						System.out.println(FileHashTable.get(AddHashValues.get(j)));
								Server_Replica_Status.put(HashFile_Value,AddHashValues.get(j));
							//	Server_Replica m=Server_Replica_Status.get(j);
								Send_DataToServer(AddHashValues.get(j),false,FileHashTable.get(AddHashValues.get(i)));
									//System.out.println(Server_Replica_Status.get(i));
							}
					}
				}
		
		 }

}
	catch(Exception ex){DisplayError_Client(ex,"FileNames_Send_Replicate");}
	
}

	
public void Send_DataToServer(int HashFile_Value_Replica,boolean PrintStatus,String FileHashTable)
{
	String Send,responseLine;
	try{					
					DataOutStream=SendData.get(HashFile_Value_Replica);
					if(PrintStatus)
					DataOutStream.println("list"+FileHashTable);			
					else
					DataOutStream.println("Repl"+FileHashTable);	
					DataInstream=GetData.get(HashFile_Value_Replica);
				
					 responseLine = DataInstream.readLine();
					// System.out.println(responseLine);
						if(PrintStatus){ System.out.println(responseLine);}
					}
	
	
	catch(Exception ex){responseLine=GetFromReplica(HashFile_Value_Replica,("list"+FileHashTable));DisplayError_Client(ex,"Send_DataToServer");System.out.println(responseLine);}
	finally{responseLine=null;}
	
}

/*Purpose:File Hashing and storing in a server location*/
public int HashFunction(String ServerString)
 {int Total=0;
try{
	byte[] valuesDefault = ServerString.getBytes();
	for(int i=0;i<ServerString.length();i++)
	Total =valuesDefault[i]+Total;
}
catch(Exception ex){}
return (Total%Total_Servers);
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



/**************************************************/


public void SendReplicaFiles(String IP,int PortNumber,boolean Notreplica)
{
Socket objConnection;
DataInputStream DataInput;
DataInputStream DataInStream;
PrintStream DataPrintStream;
	try{
 objConnection = new Socket(IP,PortNumber);
int File_TransferSize=4096; 
		 double diff;
			  double length;
		
	   DataOutStream = new PrintStream(objConnection.getOutputStream());
 DataOutStream.println("repl");
        DataOutStream.println(FileName_Receive);
			 // System.out.println("Sending File"+FileNametoSend);
			  File objTransferFile = new File(FileName_Receive);
			  
			  FileInputStream objFileinput = new FileInputStream(objTransferFile);
			 BufferedInputStream objbufferedInStream = new BufferedInputStream(objFileinput);
 byte [] TransferBytearray  = new byte [File_TransferSize];	 
			  OutputStream objoutStream = objConnection.getOutputStream();
			  objoutStream.write(TransferBytearray,0,TransferBytearray.length);
 objoutStream.flush();
length=(Math.ceil(objTransferFile.length()/File_TransferSize));/*



//System.out.println((Math.floor(objTransferFile.length()/File_TransferSize))+1);

			  */for(int j=0;j<=(Math.floor(objTransferFile.length()/File_TransferSize))+1;j++){	
			//System.out.println(j);
			objbufferedInStream.read(TransferBytearray,0,TransferBytearray.length);
		     
		      objoutStream.write(TransferBytearray,0,TransferBytearray.length);
		      objoutStream.flush();}
		  
		  
		  
	
		  
		  objConnection.close();
		    //closeConnection(objConnection);
			//  System.out.println("File transfer complete.File---"+ FileNametoSend +"---Send Successfully");
			  TransferBytearray=null;
			  objbufferedInStream=null;
			  objFileinput=null;
			  objTransferFile=null;
		      
	}
	catch(IOException ex){System.out.println("File has been changed/does not exsist");
	}

}
/**************************************************/
/*Purpose:Connects to other Peer(Server ) for File Download*/
public void ConnecttoDownloadFile(String IP,int PortNumber,boolean Notreplica)
{
boolean Status=true;	
ReadConfig objReadConfig=new ReadConfig();
byte [] bytearray  = new byte [4096];
InputStream is;
PrintStream DataOutStream;
try {
			if(Notreplica)
			System.out.println("\nStarted Receiving file"+FileName_Receive+"      "  +IP);
	    Socket socket = new Socket(IP,PortNumber);
		System.out.println(socket);
		
	   DataOutStream = new PrintStream(socket.getOutputStream());
  DataOutStream.println("file");
        DataOutStream.println(FileName_Receive);
		 
         is= socket.getInputStream();
		FileOutputStream fos = new FileOutputStream(FileName_Receive);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	   
bytesRead = is.read(bytearray,0,bytearray.length);
	     int currentTotalValue = bytesRead;	   		
	    while(bytesRead > -1) {
// j++;
	   bytesRead =is.read(bytearray, currentTotalValue, (bytearray.length-currentTotalValue));
		  if(bytesRead >= 0) currentTotalValue += bytesRead;
bytesRead = is.read(bytearray,0,bytearray.length);
	     currentTotalValue = bytesRead;	   
		  
		 
if(currentTotalValue!=-1){
	    bos.write(bytearray, 0 , currentTotalValue);
}
//else{System.out.println("File Not Found at Peer or File is corrupted ");Status=false;break;}
		}
if(Status){
if(Notreplica)
System.out.println("\nFile Received"+FileName_Receive +"  ");}
	    bos.flush();
        fos.close();		
	    bos.close();
		DataOutStream.close();
	    socket.close();
		
		File_Transfer();
		
	  }

catch(IOException ex ){System.out.println("File Not Found at Peer ");}
finally{objReadConfig=null;bytearray=null;DataOutStream=null;is=null;}
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
int File_TransferSize;
int bytesRead=0;
public Sockets_Functions(Socket objConnection)
{
	this.objConnection=objConnection;
	
}
	
	/*Purpose:Set up Server-Client Connection*/
     public void Sockets_Set_Connection()
    {
	   try{
		   ReadConfig objReadConfig=new ReadConfig();
      			  ServerReadConfig objServerReadConfig=new ServerReadConfig();
		//	System.out.println(InetAddress.getLocalHost());
	//		System.out.println(Integer.parseInt(objReadConfig.GetPeerPort(1)));
			server = new ServerSocket(Integer.parseInt(objReadConfig.GetPeerPort(Integer.parseInt(objServerReadConfig.GetServerNumber()))));//Integer.parseInt(objReadConfig.GetPeerPort(Integer.parseInt(objServerReadConfig.GetServerNumber())))); 
			
				} catch (IOException ex) {
            		System.out.println("Server"+ex);
       	 }

  	 while(true){
				try {
					try{					
			//System.out.println(server);		
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
File_TransferSize=4096; 


		 double diff;
//PeerList.get(j).split("/")[0]
			  double length;
			  DataInput =new  DataInputStream(objConnection.getInputStream());
				String type=DataInput.readLine();
			  String FileNametoSend=DataInput.readLine();
			if(type==null || FileNametoSend==null){closeConnection(objConnection);}  
else
if(type.equals("file")){
//System.out.println("2");
			  File objTransferFile = new File(FileNametoSend);
			  
			  FileInputStream objFileinput = new FileInputStream(objTransferFile);
			 BufferedInputStream objbufferedInStream = new BufferedInputStream(objFileinput);
 byte [] TransferBytearray  = new byte [File_TransferSize];	 
			  OutputStream objoutStream = objConnection.getOutputStream();
			  objoutStream.write(TransferBytearray,0,TransferBytearray.length);
 objoutStream.flush();
length=(Math.ceil(objTransferFile.length()/File_TransferSize));/*

//System.out.println((Math.floor(objTransferFile.length()/File_TransferSize))+1);
			  */for(int j=0;j<=(Math.floor(objTransferFile.length()/File_TransferSize))+1;j++){	
			//System.out.println(j);
			objbufferedInStream.read(TransferBytearray,0,TransferBytearray.length);
		     
		      objoutStream.write(TransferBytearray,0,TransferBytearray.length);
		      objoutStream.flush();}
		  
		  
		  /*
	diff=(int)objTransferFile.length()-(length*4096);
	if(diff>=1){
	byte [] TransferBytearray_Small  = new byte [(int)diff];	 
	objbufferedInStream.read(TransferBytearray_Small,0,TransferBytearray_Small.length);
		     
		      objoutStream.write(TransferBytearray_Small,0,TransferBytearray_Small.length);
		      objoutStream.flush();
			  TransferBytearray_Small=null;
	}
		  */
		  System.out.println("File transfer complete.File---"+ FileNametoSend +"---Send Successfully");
		    closeConnection(objConnection);
			  
			  TransferBytearray=null;
			  objbufferedInStream=null;
			  objFileinput=null;
			  objTransferFile=null;
		      
	}
else
ReceiveReplicas(FileNametoSend,objConnection);
}
	catch(IOException ex){ System.out.println("File has been changed/does not exsist");closeConnection(objConnection);
	}
	
}		

public void ReceiveReplicas(String FileName_Receive,Socket socket)
{
boolean Status=true;	
ReadConfig objReadConfig=new ReadConfig();
byte [] bytearray  = new byte [4096];
InputStream is;
PrintStream DataOutStream;
try {
			
		//System.out.println(socket);
		
	   DataOutStream = new PrintStream(socket.getOutputStream());

     
		 
         is= socket.getInputStream();
		FileOutputStream fos = new FileOutputStream(FileName_Receive);
	    BufferedOutputStream bos = new BufferedOutputStream(fos);
	   
bytesRead = is.read(bytearray,0,bytearray.length);
	     int currentTotalValue = bytesRead;	   		
	    while(bytesRead > -1) {
// j++;
	   bytesRead =is.read(bytearray, currentTotalValue, (bytearray.length-currentTotalValue));
		  if(bytesRead >= 0) currentTotalValue += bytesRead;
bytesRead = is.read(bytearray,0,bytearray.length);
	     currentTotalValue = bytesRead;	   
		  
		 
if(currentTotalValue!=-1){
	    bos.write(bytearray, 0 , currentTotalValue);
}
//else{System.out.println("File Not Found at Peer or File is corrupted ");Status=false;break;}
		}
if(Status){}//System.out.println("\nReplication in progress");}
	    bos.flush();
        fos.close();		
	    bos.close();
		DataOutStream.close();
	    socket.close();
		
		//File_Transfer();
		
	  }

catch(IOException ex ){System.out.println("File Not Found at Peer ");}
finally{objReadConfig=null;bytearray=null;DataOutStream=null;is=null;}
	  }


/***********************************************************************/

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



public int GetTotalPeers()
	{return (Integer.parseInt(objProp.getProperty("Total_Peers").trim()));}


public String GetPeerIP(int prmVal){return (objProp.getProperty("peerIP"+prmVal));}

		public String GetServerPort(int ServerNumber)
	{return (objProp.getProperty("serverport"+ServerNumber).trim());}	
	
		public String GetPeerPort(int ServerNumber)
	{return (objProp.getProperty("peerport"+ServerNumber).trim());}	
	
	/*public int GetFileTransferSize()
	{return (Integer.parseInt(objProp.getProperty("TransferSize").trim()));}	*/
	
		public String GetServerIP(int prmAddress)
	{return (objProp.getProperty("serverIP"+prmAddress).trim());}	
	
			public String GetTotalServers()
	{return (objProp.getProperty("Total_Servers").trim());}	
	/*
	public int GetAutoPeerRegisteration() {return (Integer.parseInt(objProp.getProperty("Auto_Peer_File_Register").trim()));}*/
	
	public int GetReplicationFactor(){return (Integer.parseInt(objProp.getProperty("Replication_Factor").trim()));}
	
public String GetClientPort(){return (objProp.getProperty("clientPort").trim());}
/*
public int MetaDataReplication(){return (Integer.parseInt(objProp.getProperty("Meta_Data_Replication").trim()));}
	*/}
	
class ServerReadConfig
{
Properties objProp = new Properties();
FileInputStream InputConfigStream;
						
					
		public ServerReadConfig()
		{
			try{
				InputConfigStream = new FileInputStream("ServerNumber.properties");
			objProp.load(InputConfigStream);
			}
		catch(IOException ex){}	
		}		

		public String GetServerNumber()
		{
			return (objProp.getProperty("ServerNumber").trim());
			
		}
		
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


class Get_Server_IP_Hash
	{
		
		public ArrayList<String> ServerIPHash()
		{ArrayList<String> TempArrayList=new  ArrayList<String>();
		ReadConfig objReadConfig=new ReadConfig();
			try{
			//ArrayList<String> TempArrayList=new  ArrayList<String>();
			
			for(int i=0;i<Integer.parseInt(objReadConfig.GetTotalServers());i++)
				TempArrayList.add(objReadConfig.GetServerIP(i));
			//	TempArrayList[i]=objReadConfig.GetServerIP(i++);
			}
			catch(Exception ex){ErrorHandler objError=new ErrorHandler();
	objError.LogError(ex,"Get_Server_IP_Hash/ServerIPHash");}
	finally{objReadConfig=null;}
			return TempArrayList;
		}
	}
