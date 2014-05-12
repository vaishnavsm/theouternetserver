package com.vaishnav.theouternetproject.server;

public class ServerMain {

	public static void main(String[] args) throws InterruptedException {
		Server serv = new Server();
		LogStuff lg = new LogStuff();
		lg.run();
		serv.init();
		serv.run();
	}

}
