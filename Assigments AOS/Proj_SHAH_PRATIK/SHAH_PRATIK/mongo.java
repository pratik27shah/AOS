/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mongotry;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.util.*;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;




public class mongo {


  @Deprecated public static void main(String[] args) {
final	Timer objTimerStart=new Timer();
try{ /*Starts the client*/

		//new Thread(new Server_Connection()).start();

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
objTimerStart.cancel();
    System.out.println("Started Client");

obClient_Connection.StartClient(); }}
else
{
objTimerStart.cancel();

	System.out.println("Started Client");
obClient_Connection.StartClient();

}


}
		catch(Exception ex){ex.printStackTrace();}}
	} ,0, 10000);
}	  catch(Exception ex){}
     finally{}//obClient_Connection=null;}

}}

/*
Purpose:Implements the client connection method
*/
class Client_Connection //implements Runnable
{

	/***************************Variable declaration***************************************************************/
MongoClient mongo = null;
   DataInputStream DataInStream=null ;
    PrintStream DataOutStream = null;
     BasicDBObject searchQuery = null;
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
public static final Hashtable<Integer,BasicDBObject> MongodbStorage=new Hashtable<Integer,BasicDBObject>();
public static final Hashtable<Integer,DBCollection> GetCollection=new Hashtable<Integer,DBCollection>();
public static final Hashtable<Integer,MongoClient> HashStorageMongo = new Hashtable<Integer,MongoClient>();
public static final Hashtable<Integer,ObjectOutputStream> HashStorageObjectOutputStream = new Hashtable<Integer,ObjectOutputStream>();
BasicDBObject odb;
public static final Hashtable<Integer,BasicDBObject> HashBasicObject = new Hashtable<Integer,BasicDBObject>();
public static final Hashtable<String,Integer> Hash_Selected_Value=new Hashtable<String,Integer>();
public static final ArrayList<String> Hash_keys=new ArrayList<String>();

	String Value_String=null;
 DBCursor cursor;

	/***********************************************************************************************/

 /*Purpose:Starts the client by reading configuration from config.properties and starts server thread of client*/
  @Deprecated public  void StartClient(){
	Location="Client_Connection/StartClient";
	try {
DateFormat dateFormat = new SimpleDateFormat("ddyyHH:mm:ss:");
Date objdate = new Date();
 DBCollection hashMapCollection;
	Valuetoadd =UUID.randomUUID().toString()+"/" + UUID.randomUUID().toString()+dateFormat.format(objdate).toString();
	Get_Server_IP_Hash objGet_Server_IP_Hash=new Get_Server_IP_Hash();
		ArrayList_ServerList=objGet_Server_IP_Hash.ServerIPHash();
		ReadConfig objReadConfig=new ReadConfig();
		int  Counter=0;
		int UpperValue=objReadConfig.GetUpperValue();
		int Total_Servers=Integer.parseInt(objReadConfig.GetTotalServers());
		try{}  /*starts the server*/
		catch(Exception ex){}
		SetKeyValuePair(Total_Servers);
		long start_time = System.nanoTime();
		String Choice;

                for(int k=0;k<Total_Servers;k++)
                {
                Server_IP=ArrayList_ServerList.get(Hash_Value);
                 mongo = new MongoClient(Server_IP);
                 DB db = mongo.getDB("DataBase_1");
                  odb=new BasicDBObject();
                  odb.append(Hash_keys.get((Counter)), Valuetoadd);
              hashMapCollection = db.getCollection("hashMapCollection");
               cursor = hashMapCollection.find();
                hashMapCollection.insert(odb);
                    HashStorageMongo.put(Hash_Value,mongo);
                MongodbStorage.put(Hash_Value,odb);
                    GetCollection.put(Hash_Value,hashMapCollection);
                }
long start_put = System.nanoTime();

	while(true){
		 //DataInputStream(new BufferedInputStream(System.in));
		Choice="1";
	Hash_Value=Hash_Selected_Value.get(Hash_keys.get(Counter));//HashFunction(Key,Total_Servers);
	Counter=Counter+1;
        BasicDBObject odb=MongodbStorage.get(Hash_Value);
        hashMapCollection=GetCollection.get(Hash_Value);//
        odb.put(Hash_keys.get((Counter)), Valuetoadd);
        hashMapCollection.save(odb);

        if(Counter%(UpperValue/10)==0)
                 System.out.println(Counter);

        if(Counter>=UpperValue-1){System.out.println(Counter);
        break;}
	}

	long end = System.nanoTime();
Counter=0;
//	long end = System.nanoTime();
	System.out.println("PUT DONE,GET STARTED");
int i=1;
//odb=null;
BasicDBObject dbObjFetch;
        long start_get = System.nanoTime();
	while(true){
	Hash_Value=Hash_Selected_Value.get(Hash_keys.get(Counter));//HashFunction(Key,Total_Servers);
        Counter=Counter+1;
        mongo=HashStorageMongo.get(Hash_Value);
 //odb=MongodbStorage.get(Hash_Value);
hashMapCollection=GetCollection.get(Hash_Value);//
cursor = hashMapCollection.find();
dbObjFetch = (BasicDBObject) cursor.next();
   Valuetoadd=dbObjFetch.get(Hash_keys.get((Counter))).toString();//(//(searchQuery.get(Hash_keys.get((Counter)))).toString();
//System.out.println(Counter);
          if(Counter>=UpperValue-1) break;
            }

//    Send_Server_Data(DataPrintStream,Hash_keys.get(Counter),Valuetoadd,Choice);
            
long end_get = System.nanoTime();
	
Counter=0;
System.out.println("delete");
//  odb=null;
/**delete*/
        while(true){
		 //DataInputStream(new BufferedInputStream(System.in));
		Choice="3";
	Hash_Value=Hash_Selected_Value.get(Hash_keys.get(Counter));//HashFunction(Key,Total_Servers);
	Counter=Counter+1;
        mongo=HashStorageMongo.get(Hash_Value);

hashMapCollection=GetCollection.get(Hash_Value);
cursor = hashMapCollection.find();
odb = (BasicDBObject) cursor.next();
odb.remove(Hash_keys.get((Counter)));
            hashMapCollection.save(odb);
 if(Counter%(UpperValue/10)==0)
   System.out.println(Counter);

    if(Counter>=UpperValue-1) break;
            }


        /***/
long end_del = System.nanoTime();

double difference1 = (end - start_put)/1e6;
double difference = (end_get - start_get)/1e6;
double diff_del=(end_del-end_get)/1e6;
//double diff_del=(end_del-start_del)/1e6;
System.out.println("PUT---->"+difference1/1000);
	System.out.println("GET---->"+difference/1000);
	System.out.println("delete---->"+diff_del/1000);
	System.out.println("Peer-Closed");
        ErrorHandler e1=new ErrorHandler();
        e1.LogReadings(difference1/1000, difference/1000, diff_del/1000);
	//	System.exit(0);
         }
	catch (Exception e) {

		DisplayError_Client(e,Location);
      System.err.println("Cannot connect to HOST,check IP");
      //System.exit(0);
    }
}

//int Total=0;
/*Purpose Hash function,convets to byte array,adds them and divides by total number of servers*/
 public int HashFunction(String ServerString,int Total_Servers)
 {
int Total=0;
  byte[] valuesDefault = ServerString.getBytes();
  try{
	Total=0;
	for(int i=0;i<ServerString.length();i++)
	Total =valuesDefault[i]+Total;

}
catch(Exception ex){ex.printStackTrace();}
finally{valuesDefault=null;}
  return (Total%Total_Servers);
}

 public void SetKeyValuePair(int total_servers)
 {
	 ServerReadConfig objServerRead=new ServerReadConfig();
 try{
	 int j=0;
	 String s1=objServerRead.GetHashString();
	 /*
DateFormat dateFormat = new SimpleDateFormat("MHms");
Date objdate = new Date();*/
for(int k=100000;k<200000;k=k+1,j++){

Hash_Selected_Value.put(s1+k,HashFunction(s1+k,total_servers));
Hash_keys.add(j,s1+k);
//System.out.println(k);
}
     //    System.exit(0);
 }
catch(Exception ex){System.out.println("EXIT");ex.printStackTrace();}
finally{objServerRead=null;}

 }


/*Sends the specified key,Value pairs to the client depending on the operation*/
public void  Send_Server_Data()
{/*
String[] Client_Key_Value_Operation_Pair=new String[1024];
try{objectOutput.println(Operation+"<"+Key+"<"+Value);}
*/
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

 	
}
catch(Exception ex) {}
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
				InputConfigStream = new FileInputStream("E:/java/jdk1.6.0_02/bin/Amazon/1/ServerNumber.properties");
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
				InputConfigStream = new FileInputStream("E:/java/jdk1.6.0_02/bin/Amazon/1/config.properties");
			objProp.load(InputConfigStream);
			}
		catch(IOException ex){ex.printStackTrace();}
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

     public void LogReadings(double  put,double  get,double del){
try{
FileWriter objWriter = new FileWriter("Output.txt", true);
PrintWriter  ps = new PrintWriter (objWriter);
DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
Date objdate = new Date();
objWriter.write("\n["+ dateFormat.format(objdate)+" ]\nput-->"+put+"  get--->"+get+"  delete-->"+del+"\n\n\n");
objWriter.close();
objdate=null;
objWriter=null;}
catch(IOException e){ }
}
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
}

 }






/**
 *
 * @author PRATIK SHAH
 
public class Main {

    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        // TODO code application logic here
      //  MongoCl
   MongoClient mongo = new MongoClient("192.168.56.1", 27017);
     DB db = mongo.getDB("K12");
     	System.out.println(db.getStats());
   BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("PRATIK" ,"shah");
         //collection;
  BasicDBObject dbObjectInsert = new BasicDBObject();
        for(int i=0;i<100000;i++){

            dbObjectInsert.put(i+"1", "abc");
        }
            DBCollection  collection = db.getCollection("posts");
            collection.insert(dbObjectInsert);
        
        int j = 0;

        for(int i=0;i<100000;i++){

           System.out.println(dbObjectInsert.get(i+"1"));
        }
      //  collection = db.getCollection("posts");
            DBCursor cursor = collection.find();

            /*while (cursor.hasNext()) {
                System.out.println(j+"---->"+cursor.next());
            j++;
            }
  collection.remove(dbObjectInsert);

        //remove all document
        collection.remove(new BasicDBObject());
            /*
	if(cursor.count() == 1)
	{
	System.out.println("key value pairs exist");
	}
 else System.out.println("No found");
	/*else
	{

		BasicDBObject doc = new BasicDBObject("key", "");
				append("value",");

		hashMapCollection.insert(doc);
	//	out.println(“Value inserted at key location successfully");
	}


	searchQuery.put("sad","pratik");

    }

}
*/