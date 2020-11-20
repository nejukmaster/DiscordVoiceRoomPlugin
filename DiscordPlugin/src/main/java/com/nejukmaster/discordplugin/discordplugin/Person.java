package com.nejukmaster.discordplugin.discordplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Person {
	
	String mc_nick;
	String discord_id;
	public boolean isCalling;
	
	public Person(Player p, User user) {
		this.mc_nick = p.getDisplayName();
		this.discord_id = user.getId();
		this.isCalling = false;
	}
	
	public Person(String nick, String id) {
		this.mc_nick = nick;
		this.discord_id = id;
	}
	
	public void setVoiceChannel(VoiceChannel vc) {
		Guild guild = main.main_category.getGuild();
		try {
			guild.moveVoiceMember(guild.getMember(main.jda.getUserById(this.discord_id)), vc).complete();
		}catch(IllegalStateException e) {
			
		}
	}
	
	public User getUser() {
		return main.jda.getUserById(this.discord_id);
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(this.mc_nick);
	}
	
	public String getNick() {
		return this.mc_nick;
	}
	
	public String getID() {
		return this.discord_id;
	}
	
	public Person setCalling(boolean calling) {
		this.isCalling = calling;
		return this;
	}
	
}
