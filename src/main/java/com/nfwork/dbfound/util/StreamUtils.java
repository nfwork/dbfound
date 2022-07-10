
package com.nfwork.dbfound.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public  class StreamUtils {

    public static String copyToString(InputStream in, Charset charset) throws IOException {
        if (in == null) {
            return "";
        } else {
            StringBuilder out = new StringBuilder();
            InputStreamReader reader = new InputStreamReader(in, charset);
            char[] buffer = new char[4096];

            int bytesRead;
            while((bytesRead = reader.read(buffer)) != -1) {
                out.append(buffer, 0, bytesRead);
            }

            return out.toString();
        }
    }

    public static void closeInputStream(InputStream in){
        if(in != null){
            try {
                in.close();
            }catch (Exception e){
                LogUtil.error(e.getMessage(),e);
            }
        }
    }
}
