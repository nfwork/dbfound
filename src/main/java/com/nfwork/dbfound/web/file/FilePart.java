package com.nfwork.dbfound.web.file;

import java.io.IOException;
import java.io.InputStream;

public interface FilePart {

    String getName();

    String getContentType();

    InputStream getContent() throws IOException;

    String getSize();

    long getLength();
}
