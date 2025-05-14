package com.handsome.handler;

import com.google.gson.JsonObject;
import com.handsome.common.GameManager;
import com.handsome.common.JsonUtils;
import com.handsome.common.SimplePrinter;
import com.handsome.model.Msg;
import com.handsome.model.Player;
import com.handsome.model.enums.PlayerRole;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebSocketTransferHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {

		Msg msg = JsonUtils.fromJson(frame.text(), Msg.class);
		try {
			String cmd = msg.getCmd();
			String data = msg.getData();
			int callIndex = msg.getCallindex();

			switch (cmd) {
				case "wxlogin":
					handleWxLogin(ctx.channel(), data, callIndex);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			sendErrorResponse(ctx.channel(), "Error processing message");
		}
	}

	private void handleWxLogin(Channel channel, String rawData, int callIndex) {
		JsonObject data = JsonUtils.parseString(rawData);

		Long uniqueId = data.get("uniqueID").getAsLong();
		String accountID = data.get("accountID").getAsString();
		String nickName = data.get("nickName").getAsString();
		String avatarUrl = data.get("avatarUrl").getAsString();

		if (GameManager.getPlayer(uniqueId) != null) {
			// 玩家已存在，直接返回
			return;
		}

		// 创建新玩家
		Player player = new Player();
		player.setAccountID(accountID);
		player.setNickName(nickName);
		player.setAvatarUrl(avatarUrl);
		player.setGoldCnt(1000); // 新玩家初始金币
		player.setChannel(channel);
		player.setCallIndex(callIndex);

		// 暂时先不适用db，使用内存
		GameManager.createPlayer(player, uniqueId);
	}

	private void sendErrorResponse(Channel channel, String message) {
		JsonObject response = new JsonObject();
		response.addProperty("type", "error");
		response.addProperty("message", message);
		channel.writeAndFlush(new TextWebSocketFrame(response.toString()));
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (cause instanceof java.io.IOException) {
			clientOfflineEvent(ctx.channel());
		} else {
			SimplePrinter.serverLog("ERROR：" + cause.getMessage());
			cause.printStackTrace();
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				try {
					clientOfflineEvent(ctx.channel());
					ctx.channel().close();
				} catch (Exception e) {
				}
			}
		} else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
			SimplePrinter.serverLog("客户端连接成功：" + ctx.channel().remoteAddress());
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

	private void clientOfflineEvent(Channel channel) {
	}
}
