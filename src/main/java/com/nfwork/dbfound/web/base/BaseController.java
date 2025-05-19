package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;

public interface BaseController {

	ResponseObject execute(Context context) throws Exception;
}
