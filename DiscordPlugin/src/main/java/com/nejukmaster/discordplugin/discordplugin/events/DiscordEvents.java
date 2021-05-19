package com.nejukmaster.discordplugin.discordplugin.events;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.nejukmaster.discordplugin.discordplugin.Person;
import com.nejukmaster.discordplugin.discordplugin.Utils;
import com.nejukmaster.discordplugin.discordplugin.main;
import com.nejukmaster.discordplugin.discordplugin.api.PluginReadyEvent;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordEvents extends ListenerAdapter{
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		User user = e.getAuthor();
		TextChannel tc = e.getTextChannel();
		Message msg = e.getMessage();
		if(user.isBot()) return;
		if(tc.equals(main.minecraft_chat)) {
			if(Utils.getPerson(user) != null && main.AsyncChat) {
				Person p = Utils.getPerson(user);
				Bukkit.broadcastMessage("<"+p.getNick()+"> "+msg.getContentRaw());
			}
		}
		else {
			if(msg.getContentRaw().equalsIgnoreCase("!hello"))
				tc.sendMessage("Hello, "+ user.getAsMention()).queue();
			else if(msg.getContentRaw().equalsIgnoreCase("!id")) {
				System.out.println(tc.getGuild().getIdLong()+"");
			}
			if(main.loging_keys.indexOf(msg.getContentRaw())!=-1) {
				Person p = new Person(main.loging_players.get(main.loging_keys.indexOf(msg.getContentRaw())),msg.getAuthor());
				main.loging_players.get(main.loging_keys.indexOf(msg.getContentRaw())).sendMessage("인증되었습니다.");
				msg.delete().complete();
				main.users.add(p);
				main.loging_players.remove(main.loging_keys.indexOf(msg.getContentRaw()));
				main.loging_keys.remove(msg.getContentRaw());
				return;
			}
		}
	}
	
	@Override
	public void onReady(ReadyEvent e){
		System.out.println("Discord Ready!");
		Guild guild = main.jda.getGuildById(main.guild_id);
		if(guild == null) {
			System.out.println("Can not find Discord Server!");
			return;
		}
		List<Category> categories = main.jda.getCategoriesByName("minecraft", true);
		if(categories.size() < 1) {
			main.main_category = guild.createCategory("minecraft").complete();
		}
		else {
			main.main_category = categories.get(0);
		}
		
		List<VoiceChannel> voice_channels = main.main_category.getVoiceChannels();
		ArrayList<String> names = new ArrayList<>();
		for(VoiceChannel vc : voice_channels) 
			names.add(vc.getName());
		if(names.indexOf("MAIN HALL") == -1)
			main.main_hall = main.main_category.createVoiceChannel("MAIN HALL").complete();
		else
			main.main_hall = voice_channels.get(names.indexOf("MAIN HALL"));
		
		List<TextChannel> chat_channels = main.main_category.getTextChannels();
		names = new ArrayList<>();
		for(TextChannel tc : chat_channels)
			names.add(tc.getName());
		if(names.indexOf("minecraft-chat") == -1)
			main.minecraft_chat = main.main_category.createTextChannel("minecraft-chat").complete();
		else
			main.minecraft_chat = chat_channels.get(names.indexOf("minecraft-chat"));
	}

}
