package net.waqassiddiqi.app.crew.db;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.h2.tools.Server;

public class DatabaseServer {
	private Server server;
	private String ip = "127.0.0.1";
	private String port = "9090"; 
	private static DatabaseServer instance = null;
	private Logger log = Logger.getLogger(getClass().getName());
	
	private DatabaseServer() { 
		try {
			
			InetAddress IP = InetAddress.getLocalHost();
			this.ip = IP.getHostAddress();
			
		} catch (UnknownHostException e) {
			log.warn("Unable to get server IP address,  using default", e);
		}
	}
	
	public String getPort() {
		return this.port;
	}
	
	public String getIp() {
		return this.ip;
	}
	
	public static DatabaseServer getInstance() {
		if(instance == null) {			
			instance = new DatabaseServer();
		}
		
		return instance;
	}
	
	public Server start() {
		if(server == null) {
			
			try {

				log.info("Starting rest hour server..");

				server = Server.createTcpServer(new String[] { "-trace", "-tcpPort", port, "-tcpAllowOthers" });
				server.start();
				
				log.info("Rest hour server has started..");
				
			} catch(Exception e) {
				log.error("Unable to start rest hour server", e);
			}
		} else if(server.isRunning(true) == false) {
			try {
				
				log.info("Starting rest hour server..");
				
				server.start();
				
				log.info("Rest hour server has started..");
				
			} catch(Exception e) {
				log.error("Unable to start rest hour server", e);
			}
		}
		
		return server;
	}
	
	public boolean isRunning() {
		if(this.server == null)
			return false;
		
		return server.isRunning(true);
	}
	
	public void stop() {
		if(server != null) {
			
			log.info("Starting rest hour server..");
			
			server.stop();
			
			log.info("Rest hour server has stopped..");
			
		} else {
			log.info("Rest hour server is not running..");
		}
	}
	
	public Server getServer() {
		return server;
	}
	
	public void tcpServer() {

		try {
			
			log.info("Creating new rest hour server instance..");
			
			server = Server.createTcpServer(
					new String[] { "-trace", "-tcpPort", this.port, "-tcpAllowOthers" }).start();

			log.info("Rest hour server instance is created with status: " + server.getStatus());

		} catch (Exception e) {
			log.error("Rest hour server instance creation failed", e);
		}
	}
	
	public static void main(String[] args){

		DatabaseServer tcpServ = new DatabaseServer(); //create a new server object
        tcpServ.tcpServer(); //starts the tcp server
        
//        tcpServ.connectionToH2();
    }
	
	Connection conn;        //connection variable
	DatabaseMetaData dbmd;  /** Metadata variable which include methods such as the following:
	                         * 1) Database Product Name
	                         * 2) Database Product Version
	                         * 3) URL where the database files are located (in TCP mode)
	                        */
	Statement stm;          //statements variable
	ResultSet rst;          //result sets variable

//	public Connection connectionToH2() {
//
//	    
//	    String outputConn = null; //declare & initialize string which will hold important messages
//
//	    try {
//
//	        Class.forName("org.h2.Driver"); //Driver's name
//	        /** The String URL is pertained of the following:
//	         *  1) jdbc which java implements so that it can take advantage of the SQL features
//	         *  2) Which Database Engine will be used
//	         *  3) URL where the files will be stored (as this is a TCP connection)
//	         *  4) Schema: businessApp
//	         *  5) Auto server is true means that other computers can connect with the same databse at any time
//	         *  6) Port number of the server is also defined
//	         */
//
//	        String url = "jdbc:h2:tcp://" + SERVER_IP + ":" + SERVER_PORT + "/~/restdb;IFEXISTS=TRUE";
//	        System.out.println(url); //prints out the url the database files are located as well as the h2 features used (SSL)
//	        conn = DriverManager.getConnection(url, "sa", "sa"); //Driver Manager defines the username & password of the database
//	        System.out.println(conn.getCatalog()); //prints out the database schema
//	        
//	        conn.setAutoCommit(false); //set AutoCommit to false to control commit actions manually
//
//	        //outputs H2 version and the URL of the database files which H2 is reading from, for confirmation
//	        dbmd = conn.getMetaData(); //get MetaData to confirm connection
//
//	        outputConn = "Connection to "+dbmd.getDatabaseProductName()+" "+
//	                   dbmd.getDatabaseProductVersion()+ " with the URL " + dbmd.getURL()+" was successful.\n";
//	        System.out.println(outputConn);  //outputs the message on the system (NetBeans compiler)
//	        
//
//
//	    } catch (ClassNotFoundException ex){ //In case there is an error for creating the class for the Driver to be used
//	        System.out.println("Error creating class: " + ex.getMessage());
//	    } catch(SQLException ex){ //Any error associated with the Database Engine
//	        System.out.println("SQL error: " + ex.getMessage());
//	        
//	    }
//	    return conn; //As the method is not void, a connection variable must be returned
//	}
}
