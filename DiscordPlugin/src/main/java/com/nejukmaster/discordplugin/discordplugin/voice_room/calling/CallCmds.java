package com.nejukmaster.discordplugin.discordplugin.voice_room.calling;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nejukmaster.discordplugin.discordplugin.Person;
import com.nejukmaster.discordplugin.discordplugin.Utils;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.events.MinecraftEvents;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceUtils;

import net.md_5.bungee.api.ChatColor;

public class CallCmds implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
				if(Utils.getPerson(p) == null) {
					p.sendMessage(ChatColor.RED+"권한이 없습니다!");
				}
				else {
					if(args.length < 1) {
						for(CallRoom c : MinecraftEvents.call_rooms) {
							if(c.getTarget().equals(p.getName())&&main.main_category.getGuild().getMember(Utils.getUser(p)).getVoiceState().getChannel() != null) {
								if(!main.main_category.getGuild().getMember(Utils.getUser(p)).getVoiceState().getChannel().getName().equalsIgnoreCase("call"))
									c.receiveCall(Utils.getPerson(p));
								else
									p.sendMessage(ChatColor.RED+"이미 통화중입니다!");
							}
							return false;
						}
						p.sendMessage("/call [player nickname]");
					}
					else if(args[0].equalsIgnoreCase("quit")) {
						CallRoom callroom = VoiceUtils.findCallRoom(Utils.getPerson(p));
						if(callroom == null)
							p.sendMessage(ChatColor.RED+"통화중이지 않습니다.");
						else {
							if(callroom.quitCall(Utils.getPerson(p)))
								VoiceUtils.removeCallRoom(callroom);
						}
					}
					else {
						for(Person per : main.users) {
							if(per.getNick().equals(args[0])&&!p.getName().equalsIgnoreCase(args[0])) {
								CallRoom c = VoiceUtils.createCallRoom(Utils.getPerson(p), per.getPlayer().getName());
								MinecraftEvents.call_rooms.add(c);
								per.getPlayer().sendMessage(p.getName()+"님으로부터 전화가 왔습니다.(받기 명령어 : /call)");
								return false;
							}
							p.sendMessage(ChatColor.RED+"플레이어를 찾을 수 없습니다!");
						}
					}
				}
			}
		// TODO Auto-generated method stub
		return false;
	}

}
