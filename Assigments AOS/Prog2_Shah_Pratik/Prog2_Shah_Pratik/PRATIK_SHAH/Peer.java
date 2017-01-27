import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.charset.Charset;
import java.net.ServerSocket;
import java.net.UnknownHostException;

/*
Consist of Peer working as Client and Server
*/


public class Peer {
	
	
  @Deprecated public static void main(String[] args) {
	    
try{ /*Starts the client*/
	Client_Connection obClient_Connection=new Client_Connection();			
				obClient_Connection.StartClient(); 
	
	}
	  catch(Exception ex){}
     finally{}//obClient_Connection=null;}
}
}

/*
Purpose:Implements the client connection method
*/
class Client_Connection //implements Runnable
{
	
	/***************************Variable declaration***************************************************************/
Socket client_Socket = null;
   DataInputStream DataInStream=null ;
    PrintStream DataOutStream = null;
    DataInputStream SendDataToServer = null;
	 ObjectOutputStream objectOutput=null;
	String Data_String;
	String Location=null;
	String Server_IP=null;
	String Key=null;
	PrintStream DataPrintStream;
	int Hash_Value=0;
	String Valuetoadd=null;
	/*Purpose:stores Socket and Message Streams*/
	String Client_Messages[]=(new String[] {"Enter Value 1,2,3 or 4 \n 1.Put \n 2.Get \n 3.Delete \n 4.Close Peer  \n"});
ArrayList<String> ArrayList_ServerList=new ArrayList<String>();
public static final Hashtable<Integer,PrintStream> SendData=new Hashtable<Integer,PrintStream>();
public static final Hashtable<Integer,DataInputStream> GetData=new Hashtable<Integer,DataInputStream>();
public static final Hashtable<Integer,Socket> HashStorageSocket = new Hashtable<Integer,Socket>();
public static final Hashtable<Integer,ObjectOutputStream> HashStorageObjectOutputStream = new Hashtable<Integer,ObjectOutputStream>();
public static final Hashtable<Integer,PrintStream> HashStoragePrintStream = new Hashtable<Integer,PrintStream>();
	String Value_String=null;
/***********************************************************************************************/

 /*Purpose:Starts the client by reading configuration from config.properties and starts server thread of client*/
  @Deprecated public  void StartClient(){
	Location="Client_Connection/StartClient";
	try {
		Get_Server_IP_Hash objGet_Server_IP_Hash=new Get_Server_IP_Hash();
		ArrayList_ServerList=objGet_Server_IP_Hash.ServerIPHash();
		ReadConfig objReadConfig=new ReadConfig();
		int Total_Servers=Integer.parseInt(objReadConfig.GetTotalServers());
		try{
		new Thread(new Server_Connection()).start();}  /*starts the server*/
		catch(Exception ex){}
		
	while(true){	
			
		System.out.println(Client_Messages[0]);
		SendDataToServer = new DataInputStream(new BufferedInputStream(System.in));
		String Choice;
		System.out.println("Select Choice");
		
		while(true){
		 Choice=SendDataToServer.readLine();
		if(Choice.equals("1") || Choice.equals("2") || Choice.equals("3"))
		{	
		Key=SendDataProcess(Choice);
		break;	
		}
		if(Choice.equals("4")) break;
else  System.out.println("Enter 1 or 2 or 3 or 4");
	}
		
if(Choice.equals("4")) break;
	
	 if(Integer.parseInt(Choice)==1){
	 System.out.println("Enter String  Value");
	Valuetoadd=SendDataToServer.readLine();
	 }
	 else Valuetoadd=null;
	 
		Hash_Value=HashFunction(Key,Total_Servers);
      if(HashStorageSocket.get(Hash_Value)==null){  /*Checks if connection to be created is new or already exisit*/
		  Server_IP=ArrayList_ServerList.get(Hash_Value);
		client_Socket = new Socket(Server_IP,Integer.parseInt(objReadConfig.GetServerPort(Hash_Value)));
      	HashStorageSocket.put(Hash_Value,client_Socket);
		DataPrintStream = new PrintStream(client_Socket.getOutputStream());	
		DataInStream = new DataInputStream(client_Socket.getInputStream()); 
		SendData.put(Hash_Value,DataPrintStream);
		GetData.put(Hash_Value,DataInStream);}
else{
	/*Purpose:If connection exsist gets socket values and sends data*/
client_Socket=HashStorageSocket.get(Hash_Value);
DataPrintStream=SendData.get(Hash_Value);
DataInStream=GetData.get(Hash_Value);}
			System.out.println("Using Hash Table of Server"+Hash_Value);
				if(client_Socket!=null){Send_Server_Data(DataPrintStream,Key,Valuetoadd,Choice);}
Value_String=DataInStream.readLine();

/*Purpose:Displays output to user*/

	if(Integer.parseInt(Choice)==1 || Integer.parseInt(Choice)==3)
	{	if(Integer.parseInt(Choice)==1){
		if(Boolean.parseBoolean(Value_String)) System.out.println("Key/Value Added Successfully");
			else  System.out.println("Error--->Value Not Added");}
			
				if(Integer.parseInt(Choice)==3){
		if(Boolean.parseBoolean(Value_String)) System.out.println("Key/Value Deleted Successfully");
			else  System.out.println("Error--->Value Not deleted/not found at server");}
	}
	else 
	{	if(Value_String.equals("")){System.out.println("Value Not Found at Server");}
		else System.out.println("Value is --->"+Value_String);	
	}
Value_String=null;
	}
	
	
	for(int i=0;i<Total_Servers;i++)
	{	if(HashStorageSocket.get(i)!=null){
		CloseClientSocket(HashStorageSocket.get(i));
		GetData.remove(i);
		SendData.remove(i);   /*Close the socket connection and remove all entries */
	HashStorageSocket.remove(i);

}
	}
	System.out.println("Peer-Closed");
		System.exit(0);
 } 
	catch (Exception e) {
	
		DisplayError_Client(e,Location);
      System.err.println("Cannot connect to HOST,check IP");
    }
}


/*Purpose Hash function,convets to byte array,adds them and divides by total number of servers*/
 public int HashFunction(String ServerString,int Total_Servers)
 {int Total=0;
try{
	byte[] valuesDefault = ServerString.getBytes();
	for(int i=0;i<ServerString.length();i++)
	Total =valuesDefault[i]+Total;
}
catch(Exception ex){}
return (Total%Total_Servers);
}
 
   
 
/*Sends the specified key,Value pairs to the client depending on the operation*/ 
public void  Send_Server_Data(PrintStream objectOutput,String Key,String Value,String Operation)
{
String[] Client_Key_Value_Operation_Pair=new String[1024];
try{objectOutput.println(Operation+"<"+Key+"<"+Value);}
catch(Exception ex ){System.out.println(ex);}
finally{Client_Key_Value_Operation_Pair=null;}
}  
  
public void CloseClientSocket(Socket ClientSocket)
{
	try{ClientSocket.close();}
	catch(IOException ex){DisplayError_Client(ex,"");}
	
}
/*Purpose:Checks for the type of operation(insert,get,delete)*/
 @Deprecated public String SendDataProcess(String Send)
{	
try{
	
 	Data_String=null;
		System.out.println("Enter Key Value:");
		Data_String=SendDataToServer.readLine().toString();
	 return Data_String;
}
catch(IOException ex) {DisplayError_Client(ex,"SendDataProcess");}
return "";

}

/*Maps  Client errors to log files*/
 public void DisplayError_Client(Exception ex,String Details)
{
	ErrorHandler objError=new ErrorHandler();
	objError.LogError(ex,Details);
}
	  
}/*Class closed*/

/*Purpose:Creates and Starts server thread */
  class Server_Connection implements Runnable
{
public void run(){
Sockets_Functions  objServerSocket=new Sockets_Functions(null);

objServerSocket.Sockets_Set_Connection();
	}
}

/*Purpose:Server Code*/

 class Sockets_Functions extends Thread
{
ServerSocket server;
DataInputStream DataInput;
DataInputStream DataInStream;
PrintStream DataPrintStream;
String Client_Send_Data;
Socket objConnection;
public static final Hashtable<String,String> HashStorageTable = new Hashtable<String,String>();
public Sockets_Functions(Socket objConnection)
{
	try{
	this.objConnection=objConnection;
if(objConnection!=null){
	this.DataInStream = new DataInputStream(objConnection.getInputStream());
	this.DataPrintStream = new PrintStream(objConnection.getOutputStream());
		}
	}
catch(Exception ex){}
}
	
	/*Purpose:Set up Server-Client Connection
	by accepting server details from config.properties and ServerNumber.properties
	*/
     public void Sockets_Set_Connection()
    {
	   try{
		   ReadConfig objReadConfig=new ReadConfig();
		   ServerReadConfig objServerReadConfig=new ServerReadConfig();
      		server = new ServerSocket(Integer.parseInt(objReadConfig.GetServerPort(Integer.parseInt(objServerReadConfig.GetServerNumber())))); 
	   }catch (IOException ex) {
            	DisplayError_ServerPeer(ex,"Sockets_Set_Connection");}

  	 while(true){
				try {
					try{					
						objConnection=server.accept();	
						System.out.println("Connected---->"+objConnection);
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

String Operation_Value;
String key;
String Value;
int start,end;
boolean Set_Success=true;
String Set_Value=null;
	try{	

		while(true){
	
	
 							 Operation_Value=DataInStream.readLine();
							 if(Operation_Value!=null){
							 start =Operation_Value.indexOf('<');
							 end=Operation_Value.lastIndexOf('<');
							Value=Operation_Value.substring(end+1).trim();
							key	=Operation_Value.substring(start+1,end).trim();
	
							if(Integer.parseInt(Operation_Value.substring(0,start))==1){
							Set_Success=Put(key,Value);
							DataPrintStream.println(Set_Success);
							}
							
							if(Integer.parseInt(Operation_Value.substring(0,start))==2){ Set_Value=Get(key);
							
							DataPrintStream.println(Set_Value);}

							if(Integer.parseInt(Operation_Value.substring(0,start))==3){Set_Success=Del(key);DataPrintStream.println(Set_Success);}
					}
					else{
					DataPrintStream.println(Set_Success);}				
		}
}
	catch(Exception ex){DisplayError_ServerPeer(ex,"Server-->Run");closeConnection(objConnection);
	}
	
finally{Operation_Value=null;key=null;Set_Value=null;}
}	

/*Purpose:Put function*/
public boolean Put(String key,String Value){
boolean Return_Sucess=true;
try{
HashStorageTable.put(key,Value);
if (!(HashStorageTable.containsKey(key))){Return_Sucess=false;}
}catch(Exception ex){DisplayError_ServerPeer(ex,"Put");Return_Sucess=false;}
return Return_Sucess;}
/*Purpose:Get function*/
public String Get(String key){
String Return_Sucess=null;
try{	

if(HashStorageTable.containsKey(key))
Return_Sucess=HashStorageTable.get(key);

else{Return_Sucess="";}
}
catch(Exception ex) {DisplayError_ServerPeer(ex,"Get");}	
return Return_Sucess;
}
/*Purpose:Delete function deletes the key/value from the hash table*/	
public boolean Del(String key)
	{boolean Return_Sucess=true;
		try
		{
		if(HashStorageTable.get(key)!=null){
		HashStorageTable.remove(key);}
		else {Return_Sucess=false;}
	}
catch(Exception ex){DisplayError_ServerPeer(ex,"Del");Return_Sucess=false;}
	return Return_Sucess;
	}
	
/*Purpose:close Socket Connection of peer as a Server*/
public void closeConnection(Socket objConnection)
{try{objConnection.close();}
catch(IOException ex) {DisplayError_ServerPeer(ex,"closeConnection");}
}

public void DisplayError_ServerPeer(Exception ex,String Details)
{
	ErrorHandler objError=new ErrorHandler();
	objError.LogError(ex,Details);
}
}
/*Purpose Reads value from config.Properties file*/

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
		public String GetServerPort(int ServerNumber)
	{return (objProp.getProperty("serverport"+ServerNumber).trim());}	
	
		public String GetServerIP(int prmAddress)
	{return (objProp.getProperty("serverIP"+prmAddress).trim());}	
	
			public String GetTotalServers()
	{return (objProp.getProperty("Total_Servers").trim());}	
	
public String GetClientPort(){return (objProp.getProperty("clientPort").trim());}
	}
	
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




