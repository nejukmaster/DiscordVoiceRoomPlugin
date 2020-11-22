package com.nejukmaster.discordplugin.discordplugin.api;

import org.bukkit.Location;

import com.nejukmaster.discordplugin.discordplugin.L_Location;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.events.MinecraftEvents;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceRoom;

import net.dv8tion.jda.api.entities.VoiceChannel;

public class VoiceRoomAPI {
	
	public VoiceRoom getVoiceRoomById(String id) {
		for(VoiceRoom vr : MinecraftEvents.voice_rooms)
			if(vr.getID().equals(id))
				return vr;
		return null;
	}
	
	public VoiceRoom getVoiceRoomByChannel(VoiceChannel vc) {
		for(VoiceRoom vr : MinecraftEvents.voice_rooms)
			if(vr.getChannel().equals(vc))
				return vr;
		return null;
	}
	
	public static VoiceRoom findVoiceRoom(Location location) {
		int x = (int)Math.floor(location.getX());
		int y = (int)Math.floor(location.getY());
		int z = (int)Math.floor(location.getZ());
		L_Location nl = new L_Location(location.getWorld(),x,y,z);
		for(VoiceRoom vr : MinecraftEvents.voice_rooms) {
			if(nl.isBetween(vr.getLocation()[0], vr.getLocation()[1])) {
				return vr;
			}
		}
		return null;
	}
	
	public static void createVoiceRoom(String name) {
		if(main.main_category == null)
			return;
		else {
			VoiceRoom vr = new VoiceRoom(main.main_category, name, MinecraftEvents.pos1, MinecraftEvents.pos2);
			MinecraftEvents.voice_rooms.add(vr);
		}
		
	}

}
