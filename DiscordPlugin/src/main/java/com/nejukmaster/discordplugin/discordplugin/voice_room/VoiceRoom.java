package com.nejukmaster.discordplugin.discordplugin.voice_room;

import org.bukkit.Location;

import com.nejukmaster.discordplugin.discordplugin.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class VoiceRoom {
	
	String name;
	String id;
	Location start_pos;
	Location end_pos;
	
	public VoiceRoom(Category category, String name, Location start_pos, Location end_pos) {
		this.name = name;
		VoiceChannel room = category.createVoiceChannel(name).complete();
		this.id = room.getId();
		this.start_pos = start_pos;
		this.end_pos = end_pos;
	}
	
	public VoiceRoom(String name, String id, Location start_pos, Location end_pos) {
		this.name = name;
		this.id = id;
		this.start_pos = start_pos;
		this.end_pos = end_pos;
	}
	
	public VoiceChannel getChannel() {
		VoiceChannel channel = main.jda.getVoiceChannelById(this.id);
		return channel;
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Location[] getLocation() {
		return new Location[] {start_pos,end_pos};
	}

}
