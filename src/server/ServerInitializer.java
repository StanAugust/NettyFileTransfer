package server;

import java.util.logging.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @ClassName: ServerInitializer   
 * @Description: 装配子通道流水线，在server.ServerEntrance startServer中调用   
 * @author Stan
 * @date: 2020年3月24日
 */
public class ServerInitializer  extends ChannelInitializer<Channel>{
	
	private static final Logger logger = Logger.getLogger(ServerInitializer.class.getName());

	@Override
	protected void initChannel(Channel ch) throws Exception {
		
		logger.info("客户端 " + ch.remoteAddress() + " 已连接");
		
		ChannelPipeline pipeline = ch.pipeline();
		
		//解码器
		pipeline.addLast(new ObjectEncoder());
		//编码器
		pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));	//最大长度
		//自定义的处理器
		pipeline.addLast(new ServerHandler());
	}

}
