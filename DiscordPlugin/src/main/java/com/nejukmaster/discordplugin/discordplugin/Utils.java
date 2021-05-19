package com.nejukmaster.discordplugin.discordplugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.entity.Player;

import net.dv8tion.jda.api.entities.User;

public class Utils {
	
	public static String generateKey() {
		return (int)Math.floor(Math.random()*10)+""+(int)Math.floor(Math.random()*10)+""+(int)Math.floor(Math.random()*10)+""+(int)Math.floor(Math.random()*10);
	}
	
	public static User getUser(Player p) {
		for(Person u : main.users) {
			if(u.getPlayer() != null)
				if(u.getPlayer().equals(p))
					return u.getUser();
		}
		return null;
	}
	
	public static Player getPlayer(User u) {
		for(Person p : main.users) {
			if(p.getUser() != null)
				if(p.getUser().equals(u))
					return p.getPlayer();
		}
		return null;
	}
	
	public static Person getPerson(Player p) {
		for(Person per : main.users) {
			if(per.getPlayer() != null)
				if(per.getPlayer().equals(p))
					return per;
		}
		return null;
	}
	
	public static Person getPerson(User u) {
		for(Person per : main.users) {
			if(per.getUser() != null)
				if(per.getUser().equals(u))
					return per;
		}
		return null;
	}
	
	public static ArrayList<String> readFile(File file) {
		Scanner reader = null;
		ArrayList<String> content = new ArrayList<>();
		try {
			reader = new Scanner(file);
			while(reader.hasNextLine()) {
				String s = reader.nextLine();
				if(!s.isEmpty())
					content.add(s);
			}
		}catch(IOException e) {
            e.printStackTrace();
        }
		return content;
	}
	
	public static void writeFile(File file, String towrite) {
		FileWriter writer = null;
		try {
            writer = new FileWriter(file, false);
            writer.write(towrite);
            writer.flush();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
	}
	
	public static void createFile(File file) {
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e1) {
			e1.printStackTrace();
			}
		}
	}
	
	public static void createFile(File file, String towrite) {
		if(!file.exists()){
			try {
				file.createNewFile();
				writeFile(file, towrite);
			} catch (IOException e1) {
			e1.printStackTrace();
			}
		}
	}
	
	public static void mkDir(File dir) {
		if (!dir.exists()) {
			try{
			    dir.mkdir();
		        } 
		        catch(Exception e){
			    e.getStackTrace();
			}
		}
	}
	
	public static HashMap<String, String> loadConfig() {
		HashMap<String, String> result = new HashMap<>();
		ArrayList<String> content = readFile(main.config_file);
		for(String s : content) {
			String[] _s = s.split(":");
			result.put(_s[0], _s[1]);
		}
		return result;
	}
	
	public static void loadUsers() {
		ArrayList<File> files = new ArrayList<>(Arrays.asList(main.user_dir.listFiles()));
		if(files.isEmpty()) return;
		for(File f : files) {
			String nick = null;
			String id = null;
			ArrayList<String> content = readFile(f);
			for(String s : content) {
				String[] _s = s.split(":");
				if(_s[0].equalsIgnoreCase("nick"))
					nick = _s[1];
				else if(_s[0].equalsIgnoreCase("id"))
					id = _s[1];
			}
			if(nick != null || id != null)
				main.users.add(new Person(nick,id));
		}
	}
	
	public static void saveUsers() {
		for(Person p : main.users) {
			File f = new File(main.user_dir.getPath()+"/"+p.getNick()+".user");
			createFile(f);
			writeFile(f, "nick:"+p.getNick()+"\nid:"+p.getID());
		}
	}

}
