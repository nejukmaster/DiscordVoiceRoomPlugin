package com.nejukmaster.discordplugin.discordplugin.api;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PluginReadyEvent extends Event implements Cancellable{
	

	private static HandlerList handlers = new HandlerList();
	
	public PluginReadyEvent() {
		
	}

	@Override
	public boolean isCancelled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCancelled(boolean cancel) {
		// TODO Auto-generated method stub
		
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
