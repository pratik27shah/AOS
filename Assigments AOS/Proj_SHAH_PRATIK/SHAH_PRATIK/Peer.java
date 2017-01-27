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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.net.UnknownHostException;//import com.mongodb.BasicDBObject;

/*
Consist of Peer working as Client and Server
*/


public class Peer {
	
	
  @Deprecated public static void main(String[] args) {
final	Timer objTimerStart=new Timer();    
try{ /*Starts the client*/
ReadConfig objReadConfig=new ReadConfig();
		new Thread(new Server_Connection()).start();			
		if(Integer.parseInt(objReadConfig.Timeron())==0){
Client_Connection obClient_Connection=new Client_Connection();		

		System.out.println("Started Client");
obClient_Connection.StartClient(); }
		else
		{
	 objTimerStart.scheduleAtFixedRate(new TimerTask() {
		 @Override
        public void run() {
try{
Client_Connection obClient_Connection=new Client_Connection();		
ReadConfig objReadConfig=new ReadConfig();		
DateFormat dateFormat = new SimpleDateFormat("HH:mm");
Date date = new Date();
System.out.println(dateFormat.format(date));///
if(Integer.parseInt(objReadConfig.Timeron())==1){
if(((dateFormat.format(date)).toString()).equals(objReadConfig.GetStartTime())){
System.out.println("Started Client");
objTimerStart.cancel();
obClient_Connection.StartClient(); }}
else 
{
	
	System.out.println("Started Client");
objTimerStart.cancel();
obClient_Connection.StartClient(); 
	
}	


}
		catch(Exception ex){ex.printStackTrace();}}
} ,0, 10000);}
}	  catch(Exception ex){}
     finally{}//obClient_Connection=null;}

}}

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
	int upperValue=0;
	/*Purpose:stores Socket and Message Streams*/
	String Client_Messages[]=(new String[] {"Enter Value 1,2,3 or 4 \n 1.Put \n 2.Get \n 3.Delete \n 4.Close Peer  \n"});
ArrayList<String> ArrayList_ServerList=new ArrayList<String>();
public static final Hashtable<Integer,PrintStream> SendData=new Hashtable<Integer,PrintStream>();
public static final Hashtable<Integer,DataInputStream> GetData=new Hashtable<Integer,DataInputStream>();
public static final Hashtable<Integer,Socket> HashStorageSocket = new Hashtable<Integer,Socket>();
public static final Hashtable<Integer,ObjectOutputStream> HashStorageObjectOutputStream = new Hashtable<Integer,ObjectOutputStream>();

public static final Hashtable<Integer,PrintStream> HashStoragePrintStream = new Hashtable<Integer,PrintStream>();
public static final Hashtable<String,Integer> Hash_Selected_Value=new Hashtable<String,Integer>();
public static final ArrayList<String> Hash_keys=new ArrayList<String>();

	String Value_String=null;


	/***********************************************************************************************/

 /*Purpose:Starts the client by reading configuration from config.properties and starts server thread of client*/
  @Deprecated public  void StartClient(){
	Location="Client_Connection/StartClient";
	try {
DateFormat dateFormat = new SimpleDateFormat("ddyyHH:mm:ss:");
Date objdate = new Date();
	Valuetoadd = UUID.randomUUID().toString()+"/" + UUID.randomUUID().toString()+dateFormat.format(objdate).toString();
	Get_Server_IP_Hash objGet_Server_IP_Hash=new Get_Server_IP_Hash();
		ArrayList_ServerList=objGet_Server_IP_Hash.ServerIPHash();
		ReadConfig objReadConfig=new ReadConfig();
		int Counter=0;
		int UpperValue=objReadConfig.GetUpperValue();
		int Total_Servers=Integer.parseInt(objReadConfig.GetTotalServers());
		try{}  /*starts the server*/
		catch(Exception ex){}
		SetKeyValuePair(Total_Servers,UpperValue);
		long start_time = System.nanoTime();
		String Choice;
		
	while(true){	
		 //DataInputStream(new BufferedInputStream(System.in));
		Choice="1";
	Hash_Value=Hash_Selected_Value.get(Hash_keys.get(Counter));//HashFunction(Key,Total_Servers);
if(Counter%100000==0)
	System.out.println(Counter);
	Counter++;     
	 if(HashStorageSocket.get(Hash_Value)==null){  /*Checks if connection to be created is new or already exisit*/
		  Server_IP=ArrayList_ServerList.get(Hash_Value);
			client_Socket = new Socket(Server_IP,Integer.parseInt(objReadConfig.GetServerPort(Hash_Value)));
      	HashStorageSocket.put(Hash_Value,client_Socket);
		DataPrintStream = new PrintStream(client_Socket.getOutputStream());	
		DataInStream = new DataInputStream(client_Socket.getInputStream()); 
		SendData.put(Hash_Value,DataPrintStream);
		GetData.put(Hash_Value,DataInStream);
			}
else{
	/*Purpose:If connection exsist gets socket values and sends data*/
client_Socket=HashStorageSocket.get(Hash_Value);
DataPrintStream=SendData.get(Hash_Value);
DataInStream=GetData.get(Hash_Value);}
				if(client_Socket!=null){Send_Server_Data(DataPrintStream,Hash_keys.get(Counter),Valuetoadd,Choice);}
Value_String=DataInStream.readLine();


	if(Counter==UpperValue-1) break;
	}
	
	long end = System.nanoTime();

	for(int i=0;i<Total_Servers;i++)
	{	if(HashStorageSocket.get(i)!=null){
		CloseClientSocket(HashStorageSocket.get(i));
		GetData.remove(i);
		SendData.remove(i);   /*Close the socket connection and remove all entries */
	HashStorageSocket.remove(i);

}
	}
	DataInStream=null;
	DataPrintStream=null;
	client_Socket=null;
	
	
	Counter=0;
	long start_get = System.nanoTime();
	System.out.println("PUT DONE,GET STARTED");
	while(true){	
		 //DataInputStream(new BufferedInputStream(System.in));
		Choice="2";
	Hash_Value=Hash_Selected_Value.get(Hash_keys.get(Counter));//HashFunction(Key,Total_Servers);

	Counter++;     
	 if(HashStorageSocket.get(Hash_Value)==null){  /*Checks if connection to be created is new or already exisit*/
		  Server_IP=ArrayList_ServerList.get(Hash_Value);
			client_Socket = new Socket(Server_IP,Integer.parseInt(objReadConfig.GetServerPort(Hash_Value)));
      	HashStorageSocket.put(Hash_Value,client_Socket);
		DataPrintStream = new PrintStream(client_Socket.getOutputStream());	
		DataInStream = new DataInputStream(client_Socket.getInputStream()); 
		SendData.put(Hash_Value,DataPrintStream);
		GetData.put(Hash_Value,DataInStream);
			}
else{
	/*Purpose:If connection exsist gets socket values and sends data*/
client_Socket=HashStorageSocket.get(Hash_Value);
DataPrintStream=SendData.get(Hash_Value);
DataInStream=GetData.get(Hash_Value);}
				if(client_Socket!=null){Send_Server_Data(DataPrintStream,Hash_keys.get(Counter),Valuetoadd,Choice);}
Value_String=DataInStream.readLine();
//System.out.println(Value_String);
if(Counter%15000==0){System.out.println(Counter);}
	if(Counter==UpperValue-1) break;
	}
	
	/*****DELETE OPEREATION*********/
	long end_get = System.nanoTime();
	
	for(int i=0;i<Total_Servers;i++)
	{	if(HashStorageSocket.get(i)!=null){
		CloseClientSocket(HashStorageSocket.get(i));
		GetData.remove(i);
		SendData.remove(i);   /*Close the socket connection and remove all entries */
	HashStorageSocket.remove(i);

}
	}
	DataPrintStream=null;
	DataInStream=null;
	client_Socket=null;
	long start_del = System.nanoTime();
Counter=0;
Value_String=null;	
	while(true){	
		 //DataInputStream(new BufferedInputStream(System.in));
		Choice="3";
	Hash_Value=Hash_Selected_Value.get(Hash_keys.get(Counter));//HashFunction(Key,Total_Servers);

	Counter++;     
	 if(HashStorageSocket.get(Hash_Value)==null){  /*Checks if connection to be created is new or already exisit*/
		  Server_IP=ArrayList_ServerList.get(Hash_Value);
			client_Socket = new Socket(Server_IP,Integer.parseInt(objReadConfig.GetServerPort(Hash_Value)));
      	HashStorageSocket.put(Hash_Value,client_Socket);
		DataPrintStream = new PrintStream(client_Socket.getOutputStream());	
		DataInStream = new DataInputStream(client_Socket.getInputStream()); 
		SendData.put(Hash_Value,DataPrintStream);
		GetData.put(Hash_Value,DataInStream);
			}
else{
	/*Purpose:If connection exsist gets socket values and sends data*/
client_Socket=HashStorageSocket.get(Hash_Value);
DataPrintStream=SendData.get(Hash_Value);
DataInStream=GetData.get(Hash_Value);}
				if(client_Socket!=null){
				//	System.out.println(Hash_keys.get(Counter));
					Send_Server_Data(DataPrintStream,Hash_keys.get(Counter),Valuetoadd,Choice);}
Value_String=DataInStream.readLine();
//System.out.println(Value_String);
if(Counter%15000==0){System.out.println("/");}
	if(Counter==UpperValue-1) break;
	}
	long end_del = System.nanoTime();
	
double difference1 = (end - start_time)/1e6;

double difference = (end_get - start_get)/1e6;
double diff_del=(end_del-start_del)/1e6;
	System.out.println("PUT---->"+difference1/1000);	
	System.out.println("GET---->"+difference/1000);
	System.out.println("delete---->"+diff_del/1000);
	System.out.println("Peer-Closed");
	//	System.exit(0);
 } 
	catch (Exception e) {
	
		DisplayError_Client(e,Location);
      System.err.println("Cannot connect to HOST,check IP");
    }
}


/*Purpose Hash function,convets to byte array,adds them and divides by total number of servers*/
 public int HashFunction(String ServerString,int Total_Servers)
 {int Total=0;
byte[] valuesDefault = ServerString.getBytes();
 try{
	
	for(int i=0;i<ServerString.length();i++)
	Total =valuesDefault[i]+Total;
}
catch(Exception ex){}
finally{valuesDefault=null;}
return (Total%Total_Servers);
}
 
 public void SetKeyValuePair(int total_servers,int Upper)
 {
	 ServerReadConfig objServerRead=new ServerReadConfig();
 try{
	 int j=0;
	 String s1=objServerRead.GetHashString();
	 /*
DateFormat dateFormat = new SimpleDateFormat("MHms");
Date objdate = new Date();*/
for(int k=100000;k<(Upper+100000);k++,j++){
	
Hash_Selected_Value.put(s1+k,HashFunction(s1+k,total_servers));
Hash_keys.add(j,s1+k);
}
 }
catch(Exception ex){System.out.println("EXIT");ex.printStackTrace();} 
finally{objServerRead=null;}
	 
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
		//System.out.println("Enter Key Value:");
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
			//System.out.println(server);
	   }catch (IOException ex) {
		   System.out.println("Socket Set Conn");
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
		
		
		public String GetHashString()
		{
			return (objProp.getProperty("HashString").trim());
			
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
	
	public String Timeron()
	{return (objProp.getProperty("TimerDepend").trim());}	
	
	public int GetUpperValue()
		{
			
			return (Integer.parseInt(objProp.getProperty("UpperValue").trim()));
		}
	public String GetStartTime()
	{
		
	return (objProp.getProperty("StartTime").trim());	
	}
	
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




