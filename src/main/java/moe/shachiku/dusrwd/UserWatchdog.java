package moe.shachiku.dusrwd;

import java.util.Arrays;
import java.util.EnumSet;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

//import discord4j.common.util.Snowflake;
//import discord4j.core.DiscordClient;
//import discord4j.core.GatewayDiscordClient;
//import discord4j.core.object.entity.User;
//import discord4j.core.object.entity.channel.TextChannel;

public class UserWatchdog {

	public static void main(String[] args) {
		String token = null, channelId = null;
		String[] target = null;
		boolean help = false;
		EnumSet<OnlineStatus> statusesNotice = EnumSet.noneOf(OnlineStatus.class);

		for(String arg : args) {
			if (arg.startsWith("--")) {
				switch(arg) {
				case "--offline":
					statusesNotice.add(OnlineStatus.OFFLINE);
					break;
				case "--online":
					statusesNotice.add(OnlineStatus.ONLINE);
					break;
				case "--afk":
					statusesNotice.add(OnlineStatus.IDLE);
					break;
				case "--dnd":
					statusesNotice.add(OnlineStatus.DO_NOT_DISTURB);
					break;
				case "--invisible":
					statusesNotice.add(OnlineStatus.INVISIBLE);
					break;
				case "--all":
					statusesNotice = EnumSet.of(OnlineStatus.OFFLINE, OnlineStatus.ONLINE, OnlineStatus.IDLE, OnlineStatus.DO_NOT_DISTURB, OnlineStatus.INVISIBLE);
					break;
				case "--help":
					help = true;
					break;
				default:
					printHelp("し、知らないオプションだよ！！("+arg+") ちゃんとこの中から選んでね！");
					System.exit(1);
				}

			} else {
				if (token == null) {
					token = arg;
				} else if (channelId == null) {
					channelId = arg;
				} else if (target == null) {
					target = arg.split(",");
				} else {
					printHelp("あれ？引数が多すぎな気がするんだけど...気のせい？ヘルプ見てみてー");
					System.exit(1);
				}

			}
		}

		if (help) {
			printHelp("使い方知りたい？仕方ないなー...教えてあげるね！w");
			return;
		}

		if (token == null || channelId == null || target == null) {
			printHelp("引数たりないよーqwq ちゃんと全部教えてほしいな！");
			System.exit(1);
		}

		//ユーザーIDの形式チェック
		for(String usr : target) {
			try {
				Long.parseLong(usr);
			} catch(NumberFormatException e) {
				System.err.println("監視対象のユーザーIDの形式がおかしいっぽい？20桁までの整数値になると思われ！("+usr+")");
				System.exit(1);
			}
		}

		//チャンネルIDの形式チェック
		try {
			Long.parseLong(channelId);
		} catch(NumberFormatException e) {
			System.err.println("通知チャンネルIDの形式がおかしいっぽい？20桁までの整数値になると思われ！");
			System.exit(1);
		}

		EnumSet<GatewayIntent> intents = GatewayIntent.getIntents(GatewayIntent.DEFAULT);
		intents.add(GatewayIntent.MESSAGE_CONTENT);
		intents.add(GatewayIntent.GUILD_PRESENCES);
		intents.add(GatewayIntent.GUILD_MEMBERS);

		JDABuilder.create(token, intents)
		.addEventListeners(new StatusUpdateEventListener(Arrays.asList(target), channelId, statusesNotice))
		.enableCache(CacheFlag.ONLINE_STATUS)
		.setMemberCachePolicy(MemberCachePolicy.ALL)
		.build();


/* discord4jを使ったプログラムは、なぜか一部の環境でUnknownHostExceptionが投げられたりで動かなかったので、さよなら～！ */
//		//discordへログイン
//		DiscordClient client = null;
//		GatewayDiscordClient gateway = null;
//		try {
//			client = DiscordClient.create(token);
//			gateway = client.login().block();
//		} catch(IllegalArgumentException e) {
//			System.err.println("あれ？ログイン中にエラーがΣ(ﾟдﾟlll)ｶﾞｰﾝ トークンがあってるかとか確認してみて！");
//			e.printStackTrace();
//			System.exit(2);
//		}
//
//		//観測対象ユーザーを取得
//		User targetUserTmp = null;
//		try {
//			targetUserTmp = gateway.getUserById(Snowflake.of(target)).block();
//		} catch(NumberFormatException e) {
//			System.err.println("監視対象のユーザーIDの形式がおかしいっぽい？20桁までの整数値になると思われ！");
//			e.printStackTrace();
//			System.exit(2);
//		} catch(RuntimeException e) {
//			System.err.println("監視対象のユーザー取得中になんかエラーが...！");
//			e.printStackTrace();
//			System.exit(2);
//		}
//		final User targetUser = targetUserTmp;//ラムダ式からfinalな変数しか呼べないので、ここでfinalな変数に移し替える
//
//		//通知チャンネルを取得
//		TextChannel channelTmp = null;
//		try {
//			channelTmp = (TextChannel)gateway.getChannelById(Snowflake.of(channelId)).block();
//		} catch(ClassCastException e) {
//			System.err.println("取得したチャンネルがテキストチャンネルじゃないっぽいんですけどー！(#^ω^)");
//			e.printStackTrace();
//			System.exit(2);
//		} catch(NumberFormatException e) {
//			System.err.println("通知チャンネルIDの形式がおかしいっぽい？20桁までの整数値になると思われ！");
//			e.printStackTrace();
//			System.exit(2);
//		} catch(RuntimeException e) {
//			System.err.println("通知チャンネルの取得中になんかエラーが...！");
//			e.printStackTrace();
//			System.exit(2);
//		}
//		final TextChannel channel = channelTmp;//ラムダ式からfinalな変数しか呼べないので、ここでfinalな変数に移し替える
//
//		//イベントリスナ登録
//		gateway.on(discord4j.core.event.domain.PresenceUpdateEvent.class).subscribe(event -> {
//			System.out.println(event.toString());
//		});
//
//		gateway.onDisconnect().block();


	}


	public static void printHelp(String msg) {
		System.out.println(msg);

		System.out.println(
				"使い方: <Discordのトークン> <チャンネルID> <観測対象のユーザーID> [--offline] [--online] [--afk] [--help]\n"
				+"オプション:\n"
				+"    --offline  : オフラインになったときに通知するよ！\n"
				+"    --online   : オンラインになったときに通知するよ！\n"
				+"    --afk      : 離席中になったときに通知するよ！！\n"
				+"    --dnd      : 取り込み中になったときに通知するよ！\n"
				+"    --invisible: オンライン状態を隠したときに通知するよ！(動かないっぽい...)←\n"
				+"    --all      : 上記すべてのオプションを適用するよ！( *¯ ꒳¯*)ﾄﾞﾔｧ\n"
				+"    --help   : このヘルプを表示するよ！"
				);
	}

}
