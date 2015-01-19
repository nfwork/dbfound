package com.nfwork.dbfound.web.file;

import java.io.File;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FilenameUtils;

@SuppressWarnings("rawtypes")
public class Utf8BomRemover extends DirectoryWalker {
	public static void main(String[] args) throws IOException {
		// 删除指定文件夹下（含子文件夹）所有java文件的BOM，若构造器中参数为null则删除所有文件头部BOM
		new Utf8BomRemover("java").start(new File("C:\\Users\\Administrator\\git\\dbfound\\src\\"));
	}

	private String extension = null;

	public Utf8BomRemover(String extension) {
		super();
		this.extension = extension;
	}

	/** 启动对某个文件夹的筛选 */
	@SuppressWarnings("unchecked")
	public void start(File rootDir) throws IOException {
		walk(rootDir, null);
	}

	protected void handleFile(File file, int depth, Collection results) throws IOException {
		if (extension == null || extension.equalsIgnoreCase(FilenameUtils.getExtension(file.toString()))) {
			// 调用具体业务逻辑，其实这里不仅可以实现删除BOM，还可以做很多想干的事情。
			remove(file);
		}
	}

	/** 移除UTF-8的BOM */
	private void remove(File file) throws IOException {
		byte[] bs = FileUtils.readFileToByteArray(file);
		if (bs[0] == -17 && bs[1] == -69 && bs[2] == -65) {
			byte[] nbs = new byte[bs.length - 3];
			System.arraycopy(bs, 3, nbs, 0, nbs.length);
			FileUtils.writeByteArrayToFile(file, nbs);
			System.out.println("Remove BOM: " + file);
		}
	}
}
