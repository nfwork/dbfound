package com.nfwork.dbfound.dto;

import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public class FileDownloadResponseObject extends QueryResponseObject<Object>{

    final Param file;

    final Map<String, Param> params;

    public FileDownloadResponseObject(Param file, Map<String, Param> params){
        this.file = file;
        this.params = params;
    }

    public Param getFile() {
        return file;
    }

    public Map<String, Param> getParams() {
        return params;
    }
}
