package com.nejukmaster.discordplugin.discordplugin.events;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import com.nejukmaster.discordplugin.discordplugin.Person;
import com.nejukmaster.discordplugin.discordplugin.Utils;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceRoom;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceRoomEnterEvent;
import com.nejukmaster.discordplugin.discordplugin.voice_room.calling.CallRoom;

import net.dv8tion.jda.api.entities.User;

public class MinecraftEvents implements Listener{
	
	public static Location pos1 = null;
	public static Location pos2 = null;
	public static ArrayList<VoiceRoom> voice_rooms = new ArrayList<>();
	public static ArrayList<CallRoom> call_rooms = new ArrayList<>();
	
	@EventHandler
	public void PlayerInteractedHook(final PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Action a = e.getAction();
		Block b = e.getClickedBlock();
		ItemStack is = p.getInventory().getItemInMainHand().clone();
		if(e.getHand() == EquipmentSlot.HAND) {
			if(is.getType().equals(Material.WOODEN_SHOVEL)&&p.hasPermission(main.discord_oper)) {
				if(a.equals(Action.RIGHT_CLICK_BLOCK)) {
					pos1 = b.getLocation();
					p.sendMessage("pos1이 "+pos1+"로 설정되었습니다.");
				}
				if(a.equals(Action.LEFT_CLICK_BLOCK)) {
					pos2 = b.getLocation();
					p.sendMessage("pos2가 "+pos2+"로 설정되었습니다.");
				} 
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void VoiceRoomEnterHook(final VoiceRoomEnterEvent e) {
		Player p = e.getPlayer();
		VoiceRoom[] r = e.getRooms();
		if(Utils.getPerson(p)!=null) {
			Person per = Utils.getPerson(p);
			if(r[1] != null && !per.isCalling) {
				per.setVoiceChannel(r[1].getChannel());
			}
			else
				per.setVoiceChannel(main.jda.getVoiceChannelsByName("MAIN HALL", true).get(0));
		}
	}
	
	@EventHandler
	public void PlayerChatHook(final AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String msg = e.getMessage();
		if(Utils.getPerson(p) != null && main.AsyncChat) {
			main.minecraft_chat.sendMessage("["+p.getName()+"]:"+msg).queue();
		}
	}

}
