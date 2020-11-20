package com.nejukmaster.discordplugin.discordplugin.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceRoomEnterEvent;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceUtils;

public class RegisterCustomEvents implements Listener{
	
	@EventHandler
	public void PlayerMoveHook(final PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(VoiceUtils.findVoiceRoom(from) != VoiceUtils.findVoiceRoom(to)) {
			VoiceRoomEnterEvent event = new VoiceRoomEnterEvent(e.getPlayer(), VoiceUtils.findVoiceRoom(from), VoiceUtils.findVoiceRoom(to));
			Bukkit.getPluginManager().callEvent(event);
		}
	}

}
