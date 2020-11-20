package com.nejukmaster.discordplugin.discordplugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nejukmaster.discordplugin.discordplugin.events.DiscordEvents;
import com.nejukmaster.discordplugin.discordplugin.events.MinecraftEvents;
import com.nejukmaster.discordplugin.discordplugin.events.RegisterCustomEvents;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceCmds;
import com.nejukmaster.discordplugin.discordplugin.voice_room.VoiceUtils;
import com.nejukmaster.discordplugin.discordplugin.voice_room.calling.CallCmds;
import com.nejukmaster.discordplugin.discordplugin.voice_room.calling.CallRoom;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.GuildAction;

public final class main extends JavaPlugin{         
	public static File main_dir = new File("plugins/DiscordPlugin");
	public static File config_file = new File("plugins/DiscordPlugin/config.yml");
	public static File user_dir = new File("plugins/DiscordPlugin/Users");
	public static File voice_room_dir = new File("plugins/DiscordPlugin/VoiceRooms");
	
	public static JDA jda;
	public static GuildAction minecraft_guild;
	public static String token;
	public static long guild_id;
	public static Category main_category = null;
	public static VoiceChannel main_hall = null;
	public static TextChannel minecraft_chat = null;
	public static boolean AsyncChat = true;
	
	public static ArrayList<Player> loging_players = new ArrayList<>();
	public static ArrayList<String> loging_keys = new ArrayList<>();
	
	public static ArrayList<Person> users = new ArrayList<>();
	
	public static Permission discord_oper = new Permission("DiscordOper");
	
	public static org.bukkit.Server server;
	
	@Override
	public void onEnable(){
		getLogger().info("Discord Plugin Enable.");
		server = this.getServer();
		initEvents();
		initCmds();
		Utils.mkDir(main_dir);
		Utils.createFile(config_file,"DiscordID:[비어있음]\nToken:[비어있음]\nAsyncChatting:true");
		if(Utils.loadConfig().containsKey("DiscordID"))
			if(!Utils.loadConfig().get("DiscordID").equalsIgnoreCase("[비어있음]"))
				guild_id = Long.parseLong(Utils.loadConfig().get("DiscordID"),10);
		if(Utils.loadConfig().containsKey("Token"))
			token = Utils.loadConfig().get("Token");
		if(Utils.loadConfig().containsKey("AsyncChatting")) {
			if(Utils.loadConfig().get("AsyncChatting").equalsIgnoreCase("true"))
				AsyncChat = true;
			else
				AsyncChat = false;
		}
		Utils.mkDir(user_dir);
		Utils.mkDir(voice_room_dir);
		Utils.loadUsers();
		VoiceUtils.loadVoiceRooms();
		JDABuilder jb = JDABuilder.createDefault(token);
		try {
			jda = jb.setAutoReconnect(true)
					.setStatus(OnlineStatus.DO_NOT_DISTURB)
					.addEventListeners(new DiscordEvents())
					.build();
		}catch(LoginException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Discord Plugin Disable.");
		for(VoiceChannel v : jda.getVoiceChannelByName("call", true))
			v.delete().complete();
		Utils.saveUsers();
		VoiceUtils.saveVoiceRoom();
	}
	
	public void initEvents() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MinecraftEvents(), this);
		pm.registerEvents(new RegisterCustomEvents(), this);
	}
	
	public void initCmds() {
		getCommand("voice").setExecutor(new VoiceCmds());
		getCommand("discord").setExecutor(new mainCmds());
		getCommand("call").setExecutor(new CallCmds());
	}

}
