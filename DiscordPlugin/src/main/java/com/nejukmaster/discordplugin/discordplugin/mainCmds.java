package com.nejukmaster.discordplugin.discordplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class mainCmds implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(args[0].equalsIgnoreCase("login")) {
				if(Utils.getUser(p)!=null) {
					p.sendMessage("�̹� �����Ǽ̽��ϴ�.");
				}
				else {
					main.loging_players.add(p);
					main.loging_keys.add(Utils.generateKey());
					p.sendMessage("\""+main.loging_keys.get(main.loging_players.indexOf(p))+"\"�� ���ڵ忡�� �Է����ּ���.");
				}
			}
			if(args[0].equalsIgnoreCase("chat")&&p.hasPermission(main.discord_oper))
				main.AsyncChat = !main.AsyncChat;
			if(args[0].equalsIgnoreCase("get")) {
				p.sendMessage(main.users.toString());
			}
		}
		return false;
	}

}
