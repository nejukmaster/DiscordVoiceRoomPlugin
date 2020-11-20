package com.nejukmaster.discordplugin.discordplugin.voice_room;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VoiceRoomEnterEvent extends Event implements Cancellable{
	
	private boolean isCancelled;
	private static HandlerList handlers = new HandlerList();
	
	Player player;
	VoiceRoom previous_room;
	VoiceRoom next_room;
	
	public VoiceRoomEnterEvent(Player p, VoiceRoom previous_room, VoiceRoom next_room) {
		this.previous_room = previous_room;
		this.next_room = next_room;
		this.player = p;
	}
	
	public VoiceRoom[] getRooms() {
		return new VoiceRoom[]{previous_room, next_room};
	}
	
	public Player getPlayer() {
		return player;
	}
	

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		// TODO Auto-generated method stub
		this.isCancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		// TODO Auto-generated method stub
		return handlers;
	}

}
