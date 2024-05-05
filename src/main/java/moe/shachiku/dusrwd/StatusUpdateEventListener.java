package moe.shachiku.dusrwd;

import java.util.EnumSet;
import java.util.List;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StatusUpdateEventListener extends ListenerAdapter {

	private static final String ONLINE_MSG    = "<name>さんがオンラインになったよ！:sun:";
	private static final String OFFLINE_MSG   = "<name>さんがオフラインになったよ！:zzz:";
	private static final String AFK_MSG       = "<name>さんは離席中だよ！:cake:";
	private static final String DND_MSG       = "<name>さんは取り込み中だよ！:pencil:";
	private static final String INVISIBLE_MSG = "<name>さんはオンライン状態を隠してるよ！！:eyes:";

	private final EnumSet<OnlineStatus> statusesNotice;
	private final List<String> targetUsers;
	private final String channelId;
	private TextChannel channel;


	public StatusUpdateEventListener(List<String> targetUsers, String noticeChId, EnumSet<OnlineStatus> statusesNotice) {
		this.targetUsers = targetUsers;
		this.statusesNotice = statusesNotice;
		this.channelId = noticeChId;
	}


	@Override
	public void onReady(ReadyEvent event) {
		channel = event.getJDA().getChannelById(TextChannel.class, channelId);
	}


	@Override
	public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
		if (channel == null)
			return;//チャンネル取得前(準備中)はスキップ

		if (targetUsers.contains(event.getUser().getId()) && statusesNotice.contains(event.getNewOnlineStatus())) {
			String userName = event.getUser().getEffectiveName();
			switch(event.getNewOnlineStatus()) {
			case ONLINE:
				channel.sendMessage(ONLINE_MSG.replace("<name>", userName)).complete();
				break;
			case OFFLINE:
				channel.sendMessage(OFFLINE_MSG.replace("<name>", userName)).complete();
				break;
			case IDLE:
				channel.sendMessage(AFK_MSG.replace("<name>", userName)).complete();
				break;
			case DO_NOT_DISTURB:
				channel.sendMessage(DND_MSG.replace("<name>", userName)).complete();
				break;
			case INVISIBLE:
				channel.sendMessage(INVISIBLE_MSG.replace("<name>", userName)).complete();
				break;
			default:
				break;
			}
		}
	}

}
