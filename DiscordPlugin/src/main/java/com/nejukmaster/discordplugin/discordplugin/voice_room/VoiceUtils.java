package com.nejukmaster.discordplugin.discordplugin.voice_room;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.nejukmaster.discordplugin.discordplugin.L_Location;
import com.nejukmaster.discordplugin.discordplugin.Person;
import com.nejukmaster.discordplugin.discordplugin.Utils;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.events.MinecraftEvents;
import com.nejukmaster.discordplugin.discordplugin.voice_room.calling.CallRoom;

public class VoiceUtils {
	
	public static VoiceRoom createVoiceRoom(String name) {
		if(main.main_category == null) {
			return null;
		}
		else {
			VoiceRoom vr = new VoiceRoom(main.main_category, name, MinecraftEvents.pos1, MinecraftEvents.pos2);
			return vr;
		}
		
	}
	
	public static CallRoom createCallRoom(Person p, String target) {
		if(main.main_category == null) {
			return null;
		}
		else {
			CallRoom call = new CallRoom(main.main_category, p, target);
			return call;
		}
		
	}
	
	public static void removeCallRoom(CallRoom call) {
		if(MinecraftEvents.call_rooms.indexOf(call)!=-1) {
			call.getChannel().delete().complete();
			MinecraftEvents.call_rooms.remove(call);
			return;
		}
		return;
	}
	
	public static VoiceRoom findVoiceRoom(Location location) {
		int x = (int)Math.floor(location.getX());
		int y = (int)Math.floor(location.getY());
		int z = (int)Math.floor(location.getZ());
		L_Location nl = new L_Location(location.getWorld(),x,y,z);
		for(VoiceRoom vr : MinecraftEvents.voice_rooms) {
			if(nl.isBetween(vr.start_pos, vr.end_pos)) {
				return vr;
			}
		}
		return null;
	}
	
	public static CallRoom findCallRoom(Person p) {
		for(CallRoom c : MinecraftEvents.call_rooms) {
			Person[] callers = c.getCallers();
			if(callers[0].equals(p) || callers[1].equals(p))
				return c;
		}
		return null;
	}
	
	public static void loadVoiceRooms() {
		ArrayList<File> files = new ArrayList<>(Arrays.asList(main.voice_room_dir.listFiles()));
		if(files.isEmpty()) return;
		for(File f : files) {
			World w = null;
			int x1 = 0;
			int y1 = 0;
			int z1 = 0;
			int x2 = 0;
			int y2 = 0;
			int z2 = 0;
			String name = null;
			String id = null;
			ArrayList<String> content = Utils.readFile(f);
			for(String s : content) {
				String[] _s = s.split(":");
				if(_s[0].equalsIgnoreCase("id"))
					id = _s[1];
				else if(_s[0].equalsIgnoreCase("startX"))
					x1 = Integer.parseInt(_s[1]);
				else if(_s[0].equalsIgnoreCase("startY"))
					y1 = Integer.parseInt(_s[1]);
				else if(_s[0].equalsIgnoreCase("startZ"))
					z1 = Integer.parseInt(_s[1]);
				else if(_s[0].equalsIgnoreCase("endX"))
					x2 = Integer.parseInt(_s[1]);
				else if(_s[0].equalsIgnoreCase("endY"))
					y2 = Integer.parseInt(_s[1]);
				else if(_s[0].equalsIgnoreCase("endZ"))
					z2 = Integer.parseInt(_s[1]);
				else if(_s[0].equalsIgnoreCase("World"))
					w = Bukkit.getWorld(_s[1]);
				else if(_s[0].equalsIgnoreCase("Name"))
					name = _s[1];
			}
			if(id != null || w != null)
				MinecraftEvents.voice_rooms.add(new VoiceRoom(name,id,new Location(w,x1,y1,z1), new Location(w,x2,y2,z2)));
		}
	}
	
	public static void saveVoiceRoom() {
		int count = 0;
		for(VoiceRoom vr : MinecraftEvents.voice_rooms) {
			File f = new File(main.voice_room_dir.getPath()+"/Room"+count+".vroom");
			Utils.createFile(f);
			Location[] l = vr.getLocation();
			Utils.writeFile(f, "id:"+vr.getID()+"\nstartX:"+l[0].getBlockX()+"\nstartY:"+l[0].getBlockY()
								+"\nstartZ:"+l[0].getBlockZ()+"\nendX:"+l[1].getBlockX()+"\nendY:"+l[1].getBlockY()
								+"\nendZ:"+l[1].getBlockZ()+"\nWorld:"+l[0].getWorld().getName()+"\nName:"+vr.getName());
			count ++;
		}
	}

}
