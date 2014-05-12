package com.vaishnav.theouternetproject.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class LogStuff {
	
	FileWriter printer;
	ObjectOutputStream output;
	ServerSocket logsock;
	Socket sock;
	ObjectInputStream input;
	public static boolean running = true;
	public static final int PORT = 6813;

	public void run() {
		
		printer = null;
		
		Thread logThread = new Thread(new Runnable(){
			public void run(){
				
				//Accept Log from client.
				/*
				 * If the user already exists, then add the log to their file
				 * If they don't, add it to another new? file?
				 * Do it on the fly, no loading stuff, just printing them out.
				 * 
				 */
				try {
					logsock = new ServerSocket(PORT, 100);
					logsock.setSoTimeout(10000);
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				while(running){
					try {
						try 
						{
							sock = logsock.accept();
							System.out.println("Log recieved from "+sock.getInetAddress().getHostAddress());
						}catch(SocketTimeoutException e){
							continue;
						}
						
						input = new ObjectInputStream(sock.getInputStream());
						output = new ObjectOutputStream(sock.getOutputStream());
						output.flush();
						InetAddress inet = sock.getInetAddress();
						String s="";
						try{
							s=input.readObject().toString();
						}catch(Exception e){continue;}
						output.writeInt(1);
						output.flush();
						sock.close();
						
						if(!Files.exists(Paths.get(System.getProperty("user.dir")+"/logs"))) Files.createDirectory(Paths.get(System.getProperty("user.dir")+"/logs"));
						File log;
						if(Files.exists(Paths.get(System.getProperty("user.dir")+"/logs/"+inet.getHostAddress()+".log"))){
							log = new File(System.getProperty("user.dir")+"/logs/"+inet.getHostAddress()+".log");
						}else{
							log = Files.createFile(Paths.get(System.getProperty("user.dir")+"/logs/"+inet.getHostAddress()+".log")).toFile();
						}
						printer = new FileWriter(log, true);
						printer.write(s+"\n");
						printer.flush();
						printer.close();
						
						Thread.yield();
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
			}
		});
		logThread.start();
		
	}

}
