package common;

import java.io.File;
import java.io.Serializable;

/**
 * @ClassName: FileConfig   
 * @Description: 文件的相关参数   
 * @author: Stan
 * @date: 2020年3月23日 下午11:27:10      
 * @Copyright:
 */

public class FileConfig implements Serializable{
	
	
	/**
	 * @Fields serialVersionUID : 默认序列号
	 */
	private static final long serialVersionUID = 1L;
	
	private static int transferLength = 1024;	//自定义单次传输长度
	
	private String path = "test.txt";			//路径
	private File file = new File(path);			//文件
	private int startPos;						//起始位置
	private int endPos;							//结束位置
	private byte[] fileBuf;						//文件字节数组
	
	/*----------------------getter---------------------*/
	public static int getTransferLength() {
		return transferLength;
	}
	public String getPath() {
		return path;
	}
	public File getFile() {
		return file;
	}
	public int getStartPos() {
		return startPos;
	}
	public int getEndPos() {
		return endPos;
	}
	public byte[] getFileBuf() {
		return fileBuf;
	}
	
	/*----------------------setter---------------------*/
	public void setPath(String path) {
		this.path = path;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	public void setFileBuf(byte[] fileBuf) {
		this.fileBuf = fileBuf;
	}	
	
}
