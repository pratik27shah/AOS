
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
public class DHT_Server extends Thread{
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
String[] Return_Codes=(new String[] {"OK","Accepted ","OK"});
PrintStream DataPrintStream;
String Client_Send_Data=null;
String Name;
int FileNumbers=0;
Properties objProp = new Properties();
InputStream InputConfigStream = null;
int Client_Number=0;
 String delimiters = "\\$";
 /*
 HashTable=(FileName(key),PeerName(value))
 HashTable_IP=(Peer Name(key),IP(value))
 */
 public ArrayList<String> File_Names=new ArrayList<String>();
 public  static final HashMap<Integer,String> Peer_Details=new HashMap<Integer,String>();
public static final HashMap<String,ArrayList> HashTable = new HashMap<String,ArrayList>();
public static final HashMap<String,String> HashTable_IP = new HashMap<String,String>();
public static final HashMap<String,ArrayList> HashFileTable = new HashMap<String,ArrayList>();
public static final HashMap<String,String> HashPortNumberTable = new HashMap<String,String>();
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
					
						ReadConfig objReadConfig=new ReadConfig();
					System.out.println("Server Address-->"+objReadConfig.getServerPort());
    			    server = new ServerSocket(((objReadConfig.getServerPort())), 20);
			} 			catch (Exception ex) { }

  	 while(true){
				try {
				objConnection=server.accept();
				//System.out.println(Client_Number);
			//	String Name="Peer- "+objConnection.getPort()+objConnection.getInetAddress();
				String Name="Peer- "+objConnection.getInetAddress().toString().substring(1)+"$"+objConnection.getPort();
				System.out.println("Connected---->"+Name+" "+objConnection);
HashTable_IP.put(Name,objConnection.getInetAddress().toString().substring(1)+"$"+objConnection.getPort());
				
			//System.out.println(Peer_Details);
				
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
		HashPortNumberTable.put(Name,DataInput.readLine());
		Peer_Details.put((HashTable_IP.size())-1,Name);
		Client_Number++;
		//System.out.println(Peer_Details+"  "+(HashTable_IP.size()));
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
					//	DataPrintStream.println("Peer List");
						//ObjectOutputStream objectOutput = new ObjectOutputStream(objConnection.getOutputStream());
						Search_Peers_WhereFileExsist(FileName,DataPrintStream);
						Flag=0;
						}catch(Exception ex){System.out.println("error");}
					}

		if(Client_Send_Data.substring(0,4).equals("list")){
							try{
							DataPrintStream.println(" " +"File List Received Successfully");
							String FileName=Client_Send_Data.substring(4);	
							GetFileList(FileName,Name,0);
			
				}catch(Exception ex){}
			}
			
			
	if(Client_Send_Data.substring(0,4).equals("Repl")){
							try{
//System.out.println(Peer_Details);				
				DataPrintStream.println(" " +"File List Received Successfully");
							String FileName=Client_Send_Data.substring(4);	
							GetFileList(FileName,Name,1);
			
				}catch(Exception ex){}
			}
			
		
if(Client_Send_Data.substring(0,4).equals("Copy")){
							try{
							//DataPrintStream.println(" " +"File List Received Successfully");

//System.out.println("File REPLICAS");
							//String FileName=Client_Send_Data.substring(4);	
							SendPeerList_for_FileReplication(DataPrintStream);
			
				}catch(Exception ex){}
			}		
			
CreateServerLoggerFile(Client_Send_Data.toLowerCase(),Name,SetUpClient(Client_Send_Data.toLowerCase(),Flag));
	/*if(Flag==1){DataPrintStream.println(" " + Client_Send_Data+" Successfully");}*/

/*Purpose Sends Ack message to client*/
}       
catch (IOException ex) {HashTable_IP.remove(Name);break;}            
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
public void GetFileList(String  str,String PeerName,int Replica)
{

 
	ArrayList<String> FileList = new ArrayList<String>();
	//FileList=(ArrayList<String>);

	try{	
	 
 String[] tokensVal = str.split(delimiters);
//System.out.println(str);
   int j=-1,k=0;
   for(String token : tokensVal) {
FileList.add(++j,token);//.replaceAll(delimiters,""));	    
 
   k++;
   } 
 
	if(Replica==0)
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
	
	HashTable.put(String.valueOf(FileList.get(i)),IP);
	IP=null;
	}
	//for(i=;i<k;i++){System.out.println(HashTable.get());}
}
catch(Exception ex){FileList=null;}
finally{FileList=null;}
	
}


public void SendPeerList_for_FileReplication(PrintStream objectOutput)
{
	ReadConfig objReadConfig=new ReadConfig();
String SendData_IP="-";
DateFormat dateFormat = new SimpleDateFormat("ss");
int i=0,Replica_flag=1,TotalReplicas=0;

	try{
	int Select_Replica=0;//,Max_Peers=2;//objReadConfig.Total_Peers();
	Random rn = new Random();	
	int Replication_factor=objReadConfig.GetReplicationFactor();
while(i<HashTable_IP.size())
//for(int i=0;i<Replication_factor;i++)
	{
if(Replication_factor<1) break;
if(Replica_flag==1)
	{	Date objdate = new Date();

		Select_Replica=Integer.parseInt(dateFormat.format(objdate))%HashTable_IP.size();
	}
else 	{Select_Replica=Select_Replica+1;}
	//System.out.println(Select_Replica+"   "+PeerAddress);
if((Name.equals("Peer- "+HashTable_IP.get(Peer_Details.get(Select_Replica)))))	{
//if((HashTable_IP.get(Peer_Details.get(Select_Replica))).equals(Name)){
//else {
Select_Replica=Select_Replica+1;
Replica_flag=0;



}
	 {
TotalReplicas++;
SendData_IP=((HashTable_IP.get(Peer_Details.get(Select_Replica))).split(delimiters)[0])+"/"+HashPortNumberTable.get((Peer_Details.get(Select_Replica)))+"$"+SendData_IP ;
//System.out.println(SendData_IP);
	}
if(TotalReplicas>=Replication_factor) break;
	//System.out.println();
	}
objectOutput.println(SendData_IP);
		
	}	
		
		
	
	catch(Exception ex){ex.printStackTrace();}
	finally {objReadConfig=null;}
}

/*Purpose:Find Files peers*/
public void  Search_Peers_WhereFileExsist(String SearchFileName,PrintStream objectOutput)
{
	ArrayList<String> PeerAddress=new ArrayList<String>();
	ArrayList<String> SendPeerAddress=new ArrayList<String>();
	String SendData_IP="-";
	try{	
	int j=0,i,flag=0,total; 	
	
	if(HashTable.get(SearchFileName)!=null )
	
	{PeerAddress=HashTable.get(SearchFileName);	}
		
	if(PeerAddress.size()>0){System.out.println("File Location Found in Server");}
		else{System.out.println("No Peer with this File Found");objectOutput.println(SendData_IP);}
		for(j=0;j<PeerAddress.size();j++){
			System.out.println("Peer- "+HashTable_IP.get(PeerAddress.get(j))+Name);
if(!(Name.equals("Peer- "+HashTable_IP.get(PeerAddress.get(j)))))
		{	
	if(HashTable_IP.get(PeerAddress.get(j))!=null )
SendData_IP=(HashTable_IP.get(PeerAddress.get(j)).split(delimiters)[0])+"/"+HashPortNumberTable.get(PeerAddress.get(j))+"$"+SendData_IP;
		}
	}
objectOutput.println(SendData_IP);	
	
	  // System.out.println("hi");
	
	}
catch(Exception ex ){ex.printStackTrace();}
finally{PeerAddress=null;SendPeerAddress=null;SendData_IP=null;}
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
		
		public int getServerPort()
		{
			
//			System.out.println(objProp.getProperty("serverport"+ServerName())+ServerName());
	return (Integer.parseInt(objProp.getProperty("serverport"+ServerName()).trim()));		
		}
		
	public int Total_Peers()
	{return (Integer.parseInt(objProp.getProperty("Total_Peers").trim()));}	
				
	
public int GetReplicationFactor(){return (Integer.parseInt(objProp.getProperty("Replication_Factor").trim()));}

public int ServerName()
{
	Properties objPropServer = new Properties();
FileInputStream InputConfigStream_Server;
	try{
		
InputConfigStream_Server = new FileInputStream("ServerNumber.properties");
			objPropServer.load(InputConfigStream_Server);
//System.out.println(Integer.parseInt(objProp.getProperty("ServerNumber").trim()));
//System.out.println(Integer.parseInt(objPropServer.getProperty("ServerNumber").trim()));
return (Integer.parseInt(objPropServer.getProperty("ServerNumber").trim()));		
		}
	
	catch(Exception ex){ex.printStackTrace();return 1;}
finally{objPropServer=null;}	
}
	

	}






	
	/**/
