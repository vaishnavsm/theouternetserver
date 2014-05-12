package com.vaishnav.theouternetproject.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class InfoLocCache {
	
	private static HashMap<String, List<String>> locCache;
	
	public static void init(){
		locCache = new HashMap<String, List<String>>();
		Scanner cacheInput = null;
		try {
			cacheInput = new Scanner(new File("LocationCache.lcche"));
			while(cacheInput.hasNext()){
				String temp = cacheInput.nextLine();
				String[] takeIIDOut = temp.split(">");
				if(takeIIDOut.length>2){log("2 IDs? Database Corrupt XD");break;}
				String IID = takeIIDOut[0];
				//Divide the many addresses to an ArrayList.
				if(takeIIDOut.length>1){
				List<String> addresses = Arrays.asList(takeIIDOut[1].split(";"));
				locCache.put(IID, addresses);
				}else{
					locCache.put(IID, null);
				}
				
			}
			cacheInput.close();
		} catch (FileNotFoundException e) {
			log("LocationCache.lcche Not Found!");
		}
		
	}
	
	public static void windUp(){
		PrintStream writeStream=null;
		try{
			writeStream = new PrintStream(new File("LocationCache.lcche"));
				Object[] keysRaw = locCache.keySet().toArray();
				String[] keys = new String[keysRaw.length];
				for(int c = 0; c<keysRaw.length; c++){
					keys[c] = keysRaw[c].toString();
				}
				for(String key : keys){
					String printline = key+">";
					if(locCache.get(key)!=null){
					Object[] valuesRaw = locCache.get(key).toArray();
					String[] values = new String[valuesRaw.length];
					for(int c = 0; c<valuesRaw.length; c++){
						values[c] = valuesRaw[c].toString();
					}
					for(String value:values){
						printline+=value;
						printline+=";";
					}
					printline = printline.substring(0,printline.length()-1);
					}
					writeStream.println(printline);
					writeStream.flush();
			}
				writeStream.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static String[] get(String iid) {
		List<String> local = new ArrayList<String>();
		local = locCache.get(iid);
		if(local == null || local.size() == 0){return new String[]{"NOTAVAILABLE"};}
		Object[] obj = local.toArray();
		String[] ret = new String[obj.length];
		for(int i = 0; i<obj.length; i++) ret[i] = obj[i].toString();
		return ret;
	}
	
	private static void log(String x){
		System.out.println(x);
	}

	public static void addStuff(List<String> newStuff, String host) {
		for(String s : newStuff){
			if(locCache.containsKey(s)){
				List<String> hosts = new ArrayList<String>();
				hosts.addAll(locCache.get(s));
				if(!hosts.contains(host)){
					log("New Host added");
					hosts.add(host);
					locCache.remove(s);
					locCache.put(s, hosts);
				}else{
					log("Already there");
				}
			}else{
				List<String> _temp = new ArrayList<String>();
				_temp.add(host);
				locCache.put(s, _temp);
				log("new key added");
			}
		}
		List<String> keys = new ArrayList<String>();
		keys.addAll(locCache.keySet());
		for(int _i = 0; _i<keys.size(); _i++){
			if(!newStuff.contains(keys.get(_i))&&locCache.get(keys.get(_i)).contains(host)){
				System.out.println("False entry in db");
				locCache.remove(keys.get(_i));
			}
		}
	windUp(); //To save changes	
	}

}
