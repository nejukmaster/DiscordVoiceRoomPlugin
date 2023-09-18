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
				else {	//등록되어있지 않을 경우 등록을 진행합니다.
					main.loging_players.add(p);
					main.loging_keys.add(Utils.generateKey());
					p.sendMessage("\""+main.loging_keys.get(main.loging_players.indexOf(p))+"\"를 디스코드에서 입력해주세요.");
				}
			}
			if(args[0].equalsIgnoreCase("chat")&&p.hasPermission(main.discord_oper))	//채팅 동기화 토글
				main.AsyncChat = !main.AsyncChat;
			if(args[0].equalsIgnoreCase("get")) {	//디버깅용 커멘드 입니다.
				p.sendMessage(main.users.toString());
			}
		}
		return false;
	}

}
```
이렇게 등록된 키는 디스코드 채팅에서 확인하여 가입을 승인하게됩니다.

### 디스코드 이벤트
JDA에서는 디스코드 서버 내에 벌어지는 이벤트(채팅, 통화방 출입....)에 대한 리스너를 제공합니다. 이는 jda.api.hooks.ListenerAdapter를 implement하여 구현합니다.

_[DiscordEvents.java](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/events/DiscordEvents.java)_
```java

...
public class DiscordEvents extends ListenerAdapter{

	//onMessageReceived는 공개/비공개 text channel에서 메세지가 수신되었을때 호출됩니다. onMessageReceived는 MessageReceivedEvent를 넘겨받습니다. 여기에는 이번 이벤트에 대한 다양한 정보가 저장되어있습니다.
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		User user = e.getAuthor();	//메세지를 보낸 유저입니다.
		TextChannel tc = e.getTextChannel();	//메세지를 보낸 채널의 id입니다.
		Message msg = e.getMessage();	//수신한 메세지를 얻어옵니다.
		if(user.isBot()) return;	//봇이 보낸 메세지일경우 무시합니다.
		if(tc.equals(main.minecraft_chat)) {	//디스코드 채팅 연동 기능입니다.
			if(Utils.getPerson(user) != null && main.AsyncChat) {
				Person p = Utils.getPerson(user);
				Bukkit.broadcastMessage("<"+p.getNick()+"> "+msg.getContentRaw());
			}
		}
		else {
			if(msg.getContentRaw().equalsIgnoreCase("!hello"))	//테스트용 커멘드 입니다.
				tc.sendMessage("Hello, "+ user.getAsMention()).queue();
			else if(msg.getContentRaw().equalsIgnoreCase("!id")) {
				System.out.println(tc.getGuild().getIdLong()+"");
			}
			if(main.loging_keys.indexOf(msg.getContentRaw())!=-1) {	//인증키가 일치할 경우 플레이어를 인증완료하고 등록합니다.
				Person p = new Person(main.loging_players.get(main.loging_keys.indexOf(msg.getContentRaw())),msg.getAuthor());
				main.loging_players.get(main.loging_keys.indexOf(msg.getContentRaw())).sendMessage("ÀÎÁõµÇ¾ú½À´Ï´Ù.");
				msg.delete().complete();
				main.users.add(p);
				main.loging_players.remove(main.loging_keys.indexOf(msg.getContentRaw()));
				main.loging_keys.remove(msg.getContentRaw());
				return;
			}
		}
	}
	
	@Override
	public void onReady(ReadyEvent e){	//봇이 준비되었을때 호출됩니다.
		System.out.println("Discord Ready!");
		Guild guild = main.jda.getGuildById(main.guild_id);
		if(guild == null) {
			System.out.println("Can not find Discord Server!");
			return;
		}
		List<Category> categories = main.jda.getCategoriesByName("minecraft", true);	//마인크래프트 카테고리가 없을경우 서버에 생성합니다.
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
		if(names.indexOf("MAIN HALL") == -1)	//MAIN HALL 보이스 채널이 없으면 생성합니다.
			main.main_hall = main.main_category.createVoiceChannel("MAIN HALL").complete();
		else
			main.main_hall = voice_channels.get(names.indexOf("MAIN HALL"));
		
		List<TextChannel> chat_channels = main.main_category.getTextChannels();
		names = new ArrayList<>();
		for(TextChannel tc : chat_channels)
			names.add(tc.getName());
		if(names.indexOf("minecraft-chat") == -1)	//마인크래프트 채팅 텍스트 채널이 없으면 생성합니다.
			main.minecraft_chat = main.main_category.createTextChannel("minecraft-chat").complete();
		else
			main.minecraft_chat = chat_channels.get(names.indexOf("minecraft-chat"));
	}

}
```

### 사용자 지정 이벤트와 이벤트 리스너
이번에는 마인크래프트 게임내에서 호출될 이벤트들을 등록할 겁니다. 이 작업은 먼저 org.bukkit.event.Listener 인터페이스를 implement한 Event클래스를 생성하여 메인클래스에서 현재 서버를 얻어와 서버의 PluginManager에  registerEvents로 등록하 이루어집니다. 하지만 그전에, 제작의 편의를 위하여 각 플레이어가 다른 VoiceRoom의 영역으로 넘어갔을때 호출될 커스텀 이벤트를 제작하도록 하겠습니다.

사용자 정의 이벤트는 org.bukkit.event.Event클래스를 extends한후 하위 메서드들을 구현하고, 이를 기존 이벤트에서 호출하여 제작합니다.

_[VoiceRoomEnterEvent](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/voice_room/VoiceRoomEnterEvent.java)_
```java
public class VoiceRoomEnterEvent extends Event implements Cancellable{
	
	private boolean isCancelled;
	private static HandlerList handlers = new HandlerList();
	
	Player player;
	VoiceRoom previous_room;
	VoiceRoom next_room;
	
	public VoiceRoomEnterEvent(Player p, VoiceRoom previous_room, VoiceRoom next_room) {	//컨스트럭터 입니다. 이벤트의 발동 조건이 충족되었을때, 이벤트를 생성하여 버킷의 플러그인매니져가 call을 할 것이기 때문에 필요합니다.
		this.previous_room = previous_room;
		this.next_room = next_room;
		this.player = p;
	}
	
	public VoiceRoom[] getRooms() {	//이전 VoiceRoom과 바뀐 VoiceRoom을 배열로 반환하는 메서드입니다.
		return new VoiceRoom[]{previous_room, next_room};
	}
	
	public Player getPlayer() {	//VoiceRoom이 바뀐 플레이어를 반환합니다.
		return player;
	}
	
	//하위 메서드 구현 부분입니다. 특별히 어떤 기능을 하도록 구현된것은 없습니다.
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
```
이렇게 만들어진 CustomEvent는 Listener에서 기존 이벤트에 호출되어야 합니다. 따라서 코드의 가독성 향상을 위해 본래 기능하는 Listener 이외에 사용자 정의 Event를 호출할 Listener를 하나 생성합니다.

_[RegisterCustomEvents](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/events/RegisterCustomEvents.java)_
```java
public class RegisterCustomEvents implements Listener{
	
	@EventHandler
	public void PlayerMoveHook(final PlayerMoveEvent e) {	//VoiceRoomEnterEvent는 플레이어의 움직임에 관련된 이벤트이므로 PlayerMoveHook을 사용하여 호출합니다.
		Player p = e.getPlayer();
		Location from = e.getFrom();	//플레이어의 이전위치를 가져옵니다.
		Location to = e.getTo();	//플레이어의 나중위치를 가져옵니다.
		if(VoiceUtils.findVoiceRoom(from) != VoiceUtils.findVoiceRoom(to)) {	//현재위치와 나중위치의 VoiceRoom이 다르다면 VoiceRoomEnterEvent를 호출합니다.
			VoiceRoomEnterEvent event = new VoiceRoomEnterEvent(e.getPlayer(), VoiceUtils.findVoiceRoom(from), VoiceUtils.findVoiceRoom(to));
			Bukkit.getPluginManager().callEvent(event);
		}
	}
}
```
이로써 우리는 Listener에서 VoiceRoomEnterEvent를 호출할 수 있게 되었습니다.

이후 우리는 실제로 인 게임상 기능을 담당할 이벤트를 작성합니다.

```java
...
public class MinecraftEvents implements Listener{
	
	public static Location pos1 = null;	//VoiceRoom을 설정할때 시점을 담는 변수입니다.
	public static Location pos2 = null;	//VoiceRoom을 설정할때 종점을 담는 변수입니다.
	public static ArrayList<VoiceRoom> voice_rooms = new ArrayList<>();	//현재 등록된 VoiceRoom을 담는 리스트 입니다.
	public static ArrayList<CallRoom> call_rooms = new ArrayList<>();	//현재 사용중인 통화방을 담는 리스트입니다. 실험적인 기능 입니다.
	
	@EventHandler
	public void PlayerInteractedHook(final PlayerInteractEvent e) {	//PlayerInteractedHook은 플레이어가 좌클릭/우클릭을 사용했을때 호출됩니다.
		Player p = e.getPlayer();
		Action a = e.getAction();

		Block b = e.getClickedBlock();	//클릭한 블럭의 정보를 가져옵니다.
		ItemStack is = p.getInventory().getItemInMainHand().clone();	//오른손에 있는 아이템의 정보를 가져옵니다.
		if(e.getHand() == EquipmentSlot.HAND) {	//상호작용한 손이 오른손일경우에만 작동합니다. 이처리를 하지 않으면 왼손이 상호작용 할때도 
			if(is.getType().equals(Material.WOODEN_SHOVEL)&&p.hasPermission(main.discord_oper)) {	//상호작용한 아이템이 나무 삽 일경우, 그리고 상호작용한 플레이어 관리자 권한을 가지고있을경우
				if(a.equals(Action.RIGHT_CLICK_BLOCK)) {	//우클릭을 사용했을때 사용한 좌표를 시점으로 설정합니다.
					pos1 = b.getLocation();
					p.sendMessage("pos1이 "+pos1+"로 설정되었습니다.");
				}
				if(a.equals(Action.LEFT_CLICK_BLOCK)) {		//좌클릭을 사용했을때 사용한 좌표를 종점으로 설정합니다.
					pos2 = b.getLocation();
					p.sendMessage("pos2가 "+pos2+"로 설정되었습니다.");
				} 
				e.setCancelled(true);	//이벤트를 캔슬합니다. 좌클릭으로인한 블럭파괴나 우클릭으로인한 블럭 변형등을 방지하기위한 조치입니다.
			}
		}
	}
	
	@EventHandler
	public void VoiceRoomEnterHook(final VoiceRoomEnterEvent e) {	//플레이어의 다른 VoiceRoom 영역으로 들어갔을때 호출됩니다.
		Player p = e.getPlayer();
		VoiceRoom[] r = e.getRooms();
		if(Utils.getPerson(p)!=null) {
			Person per = Utils.getPerson(p);
			if(r[1] != null && !per.isCalling) {	//통화중이 아니고, 이동한 VoiceRoom이 빈곳이 아닐경우 음성채팅방을 옮깁니다.
				per.setVoiceChannel(r[1].getChannel());
			}
			else	//그렇지 않을경우 MAIN HALL로 옮깁니다.
				per.setVoiceChannel(main.jda.getVoiceChannelsByName("MAIN HALL", true).get(0));
		}
	}
	
	@EventHandler
	public void PlayerChatHook(final AsyncPlayerChatEvent e) {	//PlayerChatHook은 플레이어가 채팅을 보냈을경우 호출됩니다.
		Player p = e.getPlayer();
		String msg = e.getMessage();
		if(Utils.getPerson(p) != null && main.AsyncChat) {
			main.minecraft_chat.sendMessage("["+p.getName()+"]:"+msg).queue();
		}
	}

}
```

### VoiceRoom과 VoiceCmds
이제 본격적으로 VoiceRoom클래스를 구성해보겠습니다. VoiceRoom이 포함해야할 정보는 표시될 이름/음성채널 ID/시점/종점 데이터이며 이를 모듈화한 간단한 클래스로 생성합니다.

_[VoiceRoom](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/voice_room/VoiceRoom.java)_
```java
...
public class VoiceRoom {
	
	String name;
	String id;
	Location start_pos;
	Location end_pos;
	
	public VoiceRoom(Category category, String name, Location start_pos, Location end_pos) {	//name으로 음성채널을 생성하여 VoiceRoom을 생성하는 컨스트럭터
		this.name = name;
		VoiceChannel room = category.createVoiceChannel(name).complete();
		this.id = room.getId();
		this.start_pos = start_pos;
		this.end_pos = end_pos;
	}
	
	public VoiceRoom(String name, String id, Location start_pos, Location end_pos) {	//이미 생성된 음성채널의 ID를 받아와 VoiceRoom을 생성하는 컨스트럭터
		this.name = name;
		this.id = id;
		this.start_pos = start_pos;
		this.end_pos = end_pos;
	}

	//각 변수에 대한 접근을 모듈화 합니다.
	public VoiceChannel getChannel() {
		VoiceChannel channel = main.jda.getVoiceChannelById(this.id);
		return channel;
	}
	
	public String getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Location[] getLocation() {
		return new Location[] {start_pos,end_pos};
	}
}
```
이 VoiceRoom을 생성할 명령어 역시 따로 작성해줍니다.

_[VoiceCmds](https://github.com/nejukmaster/DiscordVoiceRoomPlugin/blob/master/DiscordPlugin/src/main/java/com/nejukmaster/discordplugin/discordplugin/voice_room/VoiceCmds.java)_
```java
...
public class VoiceCmds implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if(sender instanceof Player) {
			Player p = (Player)sender;
			if(args[0].equalsIgnoreCase("debug"))	//디버깅용 커멘드 입니다.
				for(VoiceRoom vr : MinecraftEvents.voice_rooms)
					p.sendMessage(vr.getID() + ":" + vr.getName() + ":" + vr.getLocation()[0] + ":" + vr.getLocation()[1]);
			if(args[0].equalsIgnoreCase("create")&&p.hasPermission(main.discord_oper)) {	//VoiceRoom을 생성하는 커멘드입니다.
				if(args.length < 2) {	//커멘드를 제대로 입력하지 않았을 경우 커멘드의 도움말을 띄웁니다.
					p.sendMessage("/voice create [room name]");
				}
				else if(MinecraftEvents.pos1 == null || MinecraftEvents.pos2 == null) {	//시점,종점 좌표가 제대로 설정되지 않았을경우 경고를 띄웁니다.
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
```
### 이벤트와 커멘드 등록
앞서 제작한 모든 이벤트와 커멘드들은 메인클래스에서 등록하는 작업을 진행해줍니다. 이벤트는 org.bukkit.plugin.PluginManager의 registerEvents메서드를 통해, 커멘드는 getCommand로 PluginCommand를 얻어와 해당 PluginCommand의 setExecutor메서드로 등록합니다.

_main.java_
```java
public final class main extends JavaPlugin{

	...
	
	@Override
	public void onEnable(){
		...
		initEvents();
		initCmds();

		...
	}
	
	...
	public void initEvents() {	//이벤트 등록
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new MinecraftEvents(), this);
		pm.registerEvents(new RegisterCustomEvents(), this);
	}
	
	public void initCmds() {	//커멘드 등록
		getCommand("voice").setExecutor(new VoiceCmds());
		getCommand("discord").setExecutor(new mainCmds());
		getCommand("call").setExecutor(new CallCmds());
	}

}
```
