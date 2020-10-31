package client;

import common.FileConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @ClassName: ClientInitializer   
 * @Description: 装配流水线，在client.ClientEntrance startClient中被调用
 * @author Stan
 * @date: 2020年3月24日
 */
public class ClientInitializer extends ChannelInitializer<Channel>{
	
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		//编码器
		pipeline.addLast(new ObjectEncoder());
		//解码器
		pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));
		//自定义的处理器
		pipeline.addLast(new ClientHandler(new FileConfig()));
	}
}
