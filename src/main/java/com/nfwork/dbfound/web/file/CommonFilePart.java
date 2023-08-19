package com.nfwork.dbfound.web.file;

import org.apache.commons.fileupload.FileItem;

import java.io.IOException;
import java.io.InputStream;

public class CommonFilePart implements FilePart{

    private final FileItem fileItem;

    public CommonFilePart(FileItem fileItem){
        this.fileItem = fileItem;
    }

    @Override
    public String getName() {
        return fileItem.getName();
    }

    @Override
    public String getContentType() {
        return fileItem.getContentType();
    }

    @Override
    public InputStream inputStream() throws IOException {
        return fileItem.getInputStream();
    }

    @Override
    public byte[] getContent() throws IOException{
        return fileItem.get();
    }

    @Override
    public String getSize() {
        return FileSizeCalculator.getFileSize(getLength());
    }

    @Override
    public long getLength() {
        return fileItem.getSize();
    }
}
