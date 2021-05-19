package com.nejukmaster.discordplugin.discordplugin.voice_room.calling;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.nejukmaster.discordplugin.discordplugin.Person;
import com.nejukmaster.discordplugin.discordplugin.RepeatingTask;
import com.nejukmaster.discordplugin.discordplugin.Utils;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceRoom;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceUtils;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class CallRoom extends VoiceRoom{
	
	Person call;
	Person receive;
	String receive_nick;
	int wait_time;
	RepeatingTask repeatingTask;

	public CallRoom(Category category, Person call, String receive_nick) {
		super(category, "call", null, null);
		this.receive = null;
		this.receive_nick = receive_nick;
		this.call = call;
		call.setVoiceChannel(this.getChannel());
		call.setCalling(true);
		repeatingTask = new RepeatingTask(main.getPlugin(main.class), 0, 20) {
            @Override
            public void run() {
            	if(getCallers()[1] == null&&wait_time >= 0) {
            		wait_time++;
                	if(wait_time >= 10) {
                		wait_time = -1;
                		dismatched();
                    	canncel();
                	}
            	}
            }
        };
		// TODO Auto-generated constructor stub
	}
	
	public void dismatched() {
		if(this.call == null) return;
		if(VoiceUtils.findVoiceRoom(this.call.getPlayer().getLocation()) != null)
			Utils.getPerson(this.call.getPlayer()).setCalling(false).setVoiceChannel(VoiceUtils.findVoiceRoom(this.call.getPlayer().getLocation()).getChannel());
		else
			Utils.getPerson(this.call.getPlayer()).setCalling(false).setVoiceChannel(main.main_hall);
		this.call.getPlayer().sendMessage(this.receive_nick+"님과 연결되지 않았습니다.");
		this.call.setCalling(false);
		this.call = null;
		VoiceUtils.removeCallRoom(this);
	}
	
	public void receiveCall(Person receive) {
		this.receive = receive;
		this.receive.setVoiceChannel(this.getChannel());
		this.receive.setCalling(true);
		this.repeatingTask.canncel();
	}
	
	public Person[] getCallers() {
		Person[] callers = {this.call,this.receive};
		return callers;
	}
	
	public String getTarget() {
		return this.receive_nick;
	}
	
	public boolean quitCall(Person p) {
		if(VoiceUtils.findVoiceRoom(p.getPlayer().getLocation()) != null)
			Utils.getPerson(p.getPlayer()).setCalling(false).setVoiceChannel(VoiceUtils.findVoiceRoom(p.getPlayer().getLocation()).getChannel());
		else
			Utils.getPerson(p.getPlayer()).setCalling(false).setVoiceChannel(main.main_hall);
		p.setCalling(false);
		if(p.equals(this.call))
			this.call = null;
		else if(p.equals(this.receive))
			this.receive = null;
		if(call == null&&receive == null)
			return true;
		else
			return false;
	}

}
