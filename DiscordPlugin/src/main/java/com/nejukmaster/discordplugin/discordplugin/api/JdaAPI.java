package com.nejukmaster.discordplugin.discordplugin.api;

import com.nejukmaster.discordplugin.discordplugin.main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class JdaAPI {
	
	public JDA getJDA() {
		return main.jda;
	}
	
	public Guild getGuild() {
		return main.jda.getGuildById(main.guild_id);
	}
	
	public Category getCategory() {
		return main.main_category;
	}
	
	public VoiceChannel getMainHall() {
		return main.main_hall;
	}
	
	public TextChannel getMinecraftChat() {
		return main.minecraft_chat;
	}

}
