package com.nfwork.dbfound.web.file;

import org.apache.commons.fileupload2.core.DiskFileItem;

import java.io.IOException;
import java.io.InputStream;

public class CommonFilePart implements FilePart{

    private final DiskFileItem fileItem;

    public CommonFilePart(DiskFileItem fileItem){
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
    public InputStream getContent() throws IOException {
        return fileItem.getInputStream();
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
