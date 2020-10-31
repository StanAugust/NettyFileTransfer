package client;

import java.io.RandomAccessFile;
import java.util.logging.Logger;

import common.FileConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName: ClientHandler   
 * @Description: 客户端的处理器，在在client.ClientInitializer initChannel中被调用   
 * @author Stan
 * @date: 2020年3月24日
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{

	private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
	
	private int byteRead;					//一次读取的字节
	private volatile int start = 0;			//文件当前读取位置
	private volatile int lastLength = 0;	//单次文件传输剩余长度
	private RandomAccessFile file;			
	private FileConfig fc;
	
	public ClientHandler(FileConfig fc) {
		if(fc.getFile().exists()) {
			if(!fc.getFile().isFile()) {
				logger.info("error:" + fc.getFile() + "is not a file!");
				return;
			}
		}
		this.fc = fc;
	}


	/**
	 * @Description:
	 * 		连接一激活就向服务器发送文件，发送文件的函数在这里修改
	 * 		若文件可一次传输完，则只调用本方法，否则调用一次本方法后，其余在channelRead中传输
	 * 	   
	 * @param ctx
	 * @throws Exception   
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception{
		
		file = new RandomAccessFile(fc.getFile(), "r");
		
		send0(ctx, start);
	}
	
	/**
	 * @Description: 服务器通知已接受到的字节数，客户端收到通知并传输剩余文件
	 * @param ctx
	 * @param msg
	 * @throws Exception   
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//收到服务器发来的字节数
		if(msg instanceof Integer) {
			start = (Integer)msg;
					
			if(start != -1) {	//文件没有传完
				logger.info("服务器已收到字节数：" + start);
				
				send0(ctx, start);
				
			}else {
				file.close();
				logger.info("服务器已接收全部文件");
				// 服务器接收完文件就与客户端断开连接
				ctx.close();
			}
		}
	}
	
	/**
	 * @throws Exception 
	 * @Description: 具体处理发送  
	 */
	private void send0(ChannelHandlerContext ctx, int start) throws Exception {
		// 文件没有传完
		if (start != -1) { 

			file.seek(start); // 把文件的记录指针定位到start字节的位置。也就是说本次将从start字节开始读数据

			int nowLength = (int) (file.length() - start); 			// 文件当前总剩余长度
			int transferLength = FileConfig.getTransferLength(); 	// 自定义的单次传输长度
			
			lastLength = nowLength<transferLength ? nowLength:transferLength;	// 选取较短一方作为单次文件传输剩余长度
			
			transfer(ctx, lastLength);
		}
	}
	
	/**
	 * @Description: 完成单次传输  
	 * @param ctx
	 * @param length	单次传输长度
	 * @throws Exception
	 */
	private void transfer(ChannelHandlerContext ctx, int length) throws Exception {
		byte[] buf = new byte[length];

		if ((byteRead = file.read(buf)) != -1 && length > 0) {
			fc.setEndPos(byteRead);
			fc.setFileBuf(buf);
		} else {
			fc.setEndPos(-1);	//结束位置-1，表示文件传输结束
			fc.setFileBuf(null);
			logger.info("文件已上传完毕");
		}
		
		ctx.writeAndFlush(fc);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
