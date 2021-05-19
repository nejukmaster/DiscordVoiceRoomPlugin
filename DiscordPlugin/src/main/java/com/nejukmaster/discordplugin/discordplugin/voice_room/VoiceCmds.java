package com.nejukmaster.discordplugin.discordplugin.voice_room;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nejukmaster.discordplugin.discordplugin.Person;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.events.MinecraftEvents;

import net.md_5.bungee.api.ChatColor;

public class VoiceCmds implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(args[0].equalsIgnoreCase("debug"))
				for(VoiceRoom vr : MinecraftEvents.voice_rooms)
					p.sendMessage(vr.getID() + ":" + vr.getName() + ":" + vr.getLocation()[0] + ":" + vr.getLocation()[1]);
			if(args[0].equalsIgnoreCase("create")&&p.hasPermission(main.discord_oper)) {
				if(args.length < 2) {
					p.sendMessage("/voice create [room name]");
				}
				else if(MinecraftEvents.pos1 == null || MinecraftEvents.pos2 == null) {
					p.sendMessage("pos1, 혹은 pos2가 미설정 상태입니다.");
				}
				else {
					MinecraftEvents.voice_rooms.add(VoiceUtils.createVoiceRoom(args[1]));
					p.sendMessage(args[1]+"(이)가 생성되었습니다.");
				}
			}
		}
		return false;
	}

}
