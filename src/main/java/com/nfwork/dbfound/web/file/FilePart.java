package com.nfwork.dbfound.web.file;

import java.io.IOException;
import java.io.InputStream;

public interface FilePart {

    String getName();

    String getContentType();

    InputStream inputStream() throws IOException;

    byte[] getContent() throws IOException;

    String getSize();

    long getLength();
}
