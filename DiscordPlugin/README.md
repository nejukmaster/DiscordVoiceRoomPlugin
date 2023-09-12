VoiceRoom Plugin
================
### 프로젝트 설명
디스코드 서버의 통화방과 마인크래프트 내 공간을 연동하여 통화방을 인게임 내에서 자유롭게 오고 갈 수 있도록 만드는 플러그인 입니다. 구체적인 사용법은 아래 포스팅에서 볼 수 있습니다.

https://cafe.naver.com/ArticleRead.nhn?clubid=22109952&articleid=1779516

### #1.디스코드 봇과 연동
디스코드에서는 JDA라는 API를 지원합니다. 이를 통해 자바에서 디스코드 봇을 제작할 수 있습니다. 저는 이 라이브러리를 사용하여 서버를 켬과 동시에 디스코드 봇을 활성화하여 부속 기능들을 제공할 것입니다. 이를 위해 메인 클래스를 작성합니다.

_[main.java](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/main.java_)_
```java
...
//메인 클래스에 JDA 패키지들을 임폴트해 줍니다.
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

...
public final class main extends JavaPlugin{
@Override
public void onEnable(){
getLogger().info("Discord Plugin Enable.");  //디스코드 플러그인이 로드되었을때 알림을 띄웁니다.
	server = this.getServer();  //현재 게임 서버를 얻습니다.
	initEvents();  //이벤트를 초기화 합니다. 이후 이벤트를 설명할때 같이 설명하겠습니다.
	initCmds();  //커멘드를 초기화 합니다. 이것도 이후 커멘드를 설명할때 같이 설명하겠습니다.
	Utils.mkDir(main_dir);  //설정 폴더가 없다면 생성합니다.
	Utils.createFile(config_file,"DiscordID:[비어있음]\nToken:[비어있음]\nAsyncChatting:true");  //config파일이 없다면 생성합니다.
	if(Utils.loadConfig().containsKey("DiscordID"))  //config 파일에서 디스코드 서버의 아이디를 가져옵니다.
		if(!Utils.loadConfig().get("DiscordID").equalsIgnoreCase("[비어있음]"))  
			guild_id = Long.parseLong(Utils.loadConfig().get("DiscordID"),10);
	if(Utils.loadConfig().containsKey("Token"))  //config 파일에서 디스코드 봇의 토큰을 가져옵니다.
		token = Utils.loadConfig().get("Token");
	if(Utils.loadConfig().containsKey("AsyncChatting")) {  //config 파일에서 디스코드 채팅과 인 게임 채팅을 연동할 지 설정합니다.
		if(Utils.loadConfig().get("AsyncChatting").equalsIgnoreCase("true"))
			AsyncChat = true;
		else
			AsyncChat = false;
	}
	Utils.mkDir(user_dir);  //유저 정보를 저장하는 폴더가 없다면 생성합니다.
	Utils.mkDir(voice_room_dir);  //Voice Room의 정보를 저장하는 폴더가 없다면 생성합니다.
	Utils.loadUsers();  //저장된 유저를 불러옵니다.
	VoiceUtils.loadVoiceRooms();  //저장된 Voice Room을 불러옵니다.
	JDABuilder jb = JDABuilder.createDefault(token);  //JDA를 통한 봇 제작의 시작은 JDABuilder를 통해 JDA 객체를 생성하는데에 있습니다. 이를 위한 Builder를 생성해줍니다. 또한 createDefault는 권장설정을 이용하여 Builder를 생성합니다.
	try {
		jda = jb.setAutoReconnect(true)  //AutoReconnect를 true로 설정하면 연결실패가 발생시 자동으로 재 연결을 시도합니다.
				.setStatus(OnlineStatus.DO_NOT_DISTURB)  //봇이 디스코드에서 표시될 상태(온라인/오프라인/방해금지...)를 설정합니다. DO_NOT_DISTURB로 설정하면 방해금지로 표기됩니다.
				.addEventListeners(new DiscordEvents())  //디스코드 봇의 이벤트 리스너를 넘겨줍니다. 이는 이후 이벤트를 설명할때 함께 설명하겠습니다.
				.build();  //봇 활성화
	}catch(LoginException e) {
		e.printStackTrace();
	}
}

...
@Override
public void onDisable(){
	getLogger().info("Discord Plugin Disable.");  //디스코드 플러그인이 비활성화 되었을때 알림을 보냅니다.
	for(VoiceChannel v : jda.getVoiceChannelsByName("call", true))  //Call 기능에서 사용한 임시 음성채팅방을 모두 삭제합니다. 실험적인 기능입니다.
		v.delete().complete();
	Utils.saveUsers();  //유저들을 저장합니다.
	VoiceUtils.saveVoiceRoom();  //Voice Room을 저장합니다.
}  
}
```
이러면 서버를 켜서 플러그인이 활성화과 됨과 동시에 봇의 상태가 바뀌는것을 확인할 수 있습니다.

(사진)
### Person클래스와 커멘드를 통한 로그인 기능
마인크래프트와 디스코드를 연동하여 게임 내 움직임을 디스코드에 반영해야 하기 때문에 마인크래프트의 플레이어와 플레이어의 디스코드 계정을 연동할 필요가 있습니다. 이를 위해 마인크래프트의 닉네임과 디스코드 아이디를 저장하는 간단한 클래스인 Person클래스를 제작합니다.

_[Person.java](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/Person.java)_
```java
...
public class Person {

String mc_nick;	//마인크래프트의 닉네임을 저장합니다.
String discord_id;	//디스코드 아이디를 저장합니다.
public boolean isCalling;	//Call을 받고있는지에대한 상태를 저장합니다. 실험적인 기능입니다.

//컨스트럭터 선언
public Person(Player p, User user) {
	this.mc_nick = p.getDisplayName();
	this.discord_id = user.getId();
	this.isCalling = false;
}

public Person(String nick, String id) {
	this.mc_nick = nick;
	this.discord_id = id;
}

//해당 플레이어의 음성채널을 옮기는 함수입니다.
public void setVoiceChannel(VoiceChannel vc) {
	Guild guild = main.main_category.getGuild();
	try {
		guild.moveVoiceMember(guild.getMember(main.jda.getUserById(this.discord_id)), vc).complete();
	}catch(IllegalStateException e) {
		
	}
}

//디스코드 User를 반환합니다.
public User getUser() {
	return main.jda.getUserById(this.discord_id);
}

//게임 내의 플레이어를 반환합니다.
public Player getPlayer() {
	return Bukkit.getPlayer(this.mc_nick);
}

//닉네임을 반환합니다.
public String getNick() {
	return this.mc_nick;
}

//디스코드 아이디를 반환합니다.
public String getID() {
	return this.discord_id;
}

//Call 상태를 바꿉니다.
public Person setCalling(boolean calling) {
	this.isCalling = calling;
	return this;
}

}
```
이로써 Person클래스를 통해 플레이어의 정보를 저장하여 인게임 플레이어와 디스코드 유저에 접근할 수 있게 되었습니다. 다음으로 게임내 커멘드를 사용해 유저를 등록하는 기능을 구현합니다.
_[mainCmds.java](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/mainCmds.java)_
```java

...
public class mainCmds implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {	//콘솔에서 보내지는 커멘드는 무시합니다.
			Player p = (Player)sender;
			if(args[0].equalsIgnoreCase("login")) {
				if(Utils.getUser(p)!=null) {	//유저가 이미 등록되어있을경우 인증을 진행하지 않습니다.
					p.sendMessage("이미 인증되셨습니다.");
				}
				else {
					main.loging_players.add(p);
					main.loging_keys.add(Utils.generateKey());
					p.sendMessage("\""+main.loging_keys.get(main.loging_players.indexOf(p))+"\"를 디스코드에서 입력해주세요.");
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
```
