package com.nfwork.dbfound.dto;

import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public class FileDownloadResponseObject extends ResponseObject{

    final Param fileParam;

    final Map<String, Param> params;

    public FileDownloadResponseObject(Param fileParam, Map<String, Param> params){
        this.fileParam = fileParam;
        this.params = params;
        this.setSuccess(true);
    }

    public Param getFileParam() {
        return fileParam;
    }

    public Map<String, Param> getParams() {
        return params;
    }
}
