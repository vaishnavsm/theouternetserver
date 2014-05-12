package com.vaishnav.theouternetproject.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {
	
	boolean running = true;
	
	static final String[] REQUESTS = {"IREQ","stuffihave"};
	
	static final int PORT = 6653;
		
	ServerSocket servSock;
	Socket sock;
	
	ObjectInputStream input;
	ObjectOutputStream output;
	
	public Server(){
		//Initialize variables n stuff.
			}

	public void init() {
		//Get the server ready for listening
		try {
			servSock = new ServerSocket(PORT, 1000);
			InfoLocCache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		//Listen until it should close
		Thread commandListener = new Thread(new Runnable(){

			@Override
			public void run() {
				Scanner input = new Scanner(System.in);
				while(running){
				String msg;
				msg = input.next();
				if(!msg.isEmpty()){
					if(msg.toLowerCase().contains("exit")){
					running = false;
					LogStuff.running = false;
					}
				}
				Thread.yield();
				}
				input.close();
			}
		});
		commandListener.start();
				
		try {
			servSock.setSoTimeout(10000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		while(running){
			try{
		//	System.out.println("Server waiting for connections...");
			
			try{
			sock = servSock.accept();
			}catch(SocketTimeoutException ignorethisexception1){continue;}
			
			if(sock != null){
				System.out.println("Found Connection: "+sock.getInetAddress().getHostAddress());
				input = new ObjectInputStream(sock.getInputStream());
				output = new ObjectOutputStream(sock.getOutputStream());
				output.flush();
				
				//Interact with Client
				
				while(!sock.isClosed()){
					Object inp = null;
					try{
					inp = input.readObject();
					}catch(Exception e){heartbeat();continue;}
					if(inp != null){
					if(inp.toString() == inp){
						//The input is a string
						decipherRequest(inp.toString());
					}else{
						//The input is not a string
					}}
					
					Thread.yield();
					heartbeat();
					Thread.sleep(100);
					heartbeat();
				}
				
				sock = null;
			}
			Thread.yield();
			}catch(IOException ioEx1){ ioEx1.printStackTrace();} catch (InterruptedException e) {
				log("Thread Problems");
			}
		}
	}

	private void decipherRequest(String req) {
		try{
		String[] parts = req.split(";");
		int noParts = parts.length;
		for(int i = 0; i < noParts; i++){
			log(parts[i]);
			if(isServerRequest(parts[i])){
				log("It is a Server Request");
				log("It is a "+ parts[i]);
				//Cases for each request type
				
				if(parts[i].equals("IREQ")){
					String iid = parts[i+1];
					log(iid);
					String[] loc = InfoLocCache.get(iid);
					for(String l:loc) log(l);
					output.writeObject(Integer.toString(Constants.SERVER_IID_LOC_LIST));
					output.flush();
					log("Sent REQTYPE");
					output.writeObject(loc);
					output.flush();
					log("Sent STRING[]");
				}
				else if(parts[i].equals("stuffihave")){
					log("Ok, hope....");
					output.writeObject("listening");
					output.flush();
					Object obj = input.readObject();
					if(obj.getClass() == (new ArrayList<String>()).getClass()){
					List<String> newStuff = (ArrayList<String>)obj;
					InetAddress inet = sock.getInetAddress();
					InfoLocCache.addStuff(newStuff, inet.getHostAddress());
					}
				}
				
				
			}
		}
		}catch(IOException ioe){ioe.printStackTrace();} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private boolean isServerRequest(String string) {
			for(int _i = 0; _i < REQUESTS.length; _i++){
				if(REQUESTS[_i].equals(string)){
					return true;
				}
			}
		return false;
	}
	
	private void closeSock(){
		try {
			output.close();
		} catch (IOException e) {		}
		try {
			input.close();
			sock.close();
			} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void heartbeat(){
		try {
			output.write(-111222);
			output.flush();
			output.write(222111);
			output.flush();
		} catch (Exception e) {
			System.out.println("Client Closed");
			closeSock();
		}	
	}
	
	private void log(String x){
		System.out.println(x);
	}
	
	public void close() {
		//Close all connections.
		
	}
}
