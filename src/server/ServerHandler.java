package server;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

import common.FileConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName: ServerHandler   
 * @Description: 服务器端的处理器，在server.ServerInitializer initChannel中调用 
 * @author Stan
 * @date: 2020年3月24日
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{
	
	private static final Logger logger = Logger.getLogger(ServerHandler.class.getName());
	
	private int byteRead;
	private int start = 0;
	
	/**
	 * @Description: 服务器接收到消息后进入这个方法，接收文件的函数在这里修改
	 * @param ctx
	 * @param msg
	 * @throws Exception   
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		//如果传递过来的是文件或文件分片
		if(msg instanceof FileConfig) {			
			FileConfig fc = (FileConfig)msg;
			
			byte[] fileBuf = fc.getFileBuf();	//接收到的文件字节数组
			byteRead = fc.getEndPos();			//记录当前文件传输结束的位置
			
			if(byteRead == -1) {	// 约定的结束的标志
				fileEnd(ctx);
				
			}else {
				
				if(byteRead > 0) {
					// TODO 文件接收路径需要指定
					RandomAccessFile file = new RandomAccessFile(new File("test2.txt"), "rw");	
					file.seek(start);	//把文件的记录指针定位到start字节的位置。也就是说程序本次将从start字节开始写数据
					file.write(fileBuf);//把传输过来的数据写进文件里
					
					start += byteRead;	//确保文件下次能从当前结束的地方继续读取
					
					ctx.writeAndFlush(start);	//向客户端通知下次从第start字节开始传输
					file.close();
					
					logger.info("服务器已接收字节数：" + start + "，客户端地址：" + ctx.channel().remoteAddress());
					
				}else {	
					exceptionCaught(ctx, new Throwable("可读字节小于0"));
				}
			}
		}
	}
	
	/**
	 * @Description: 文件接收完毕  
	 * @param ctx
	 */
	private void fileEnd(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(-1);
		logger.info("服务器接收文件完毕"
				+ "\n文件来源："+ ctx.channel().remoteAddress() 
				+ "\n文件大小：" + start + " 字节");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
	
}
