package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;

public interface ActionController extends BaseControl{

	default ResponseObject execute(Context context) throws Exception{
		return null;
	}
}
