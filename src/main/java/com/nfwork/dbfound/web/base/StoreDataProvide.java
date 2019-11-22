package com.nfwork.dbfound.web.base;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;

public interface StoreDataProvide extends BaseControl{

	public QueryResponseObject execute(Context context) throws Exception;
	
}
