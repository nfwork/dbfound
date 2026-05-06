package com.nfwork.dbfound.core;

import java.sql.Connection;

import com.nfwork.dbfound.db.ConnectionProvide;

final class ConnectionResource {

	final Connection connection;
	final ConnectionProvide provide;

	ConnectionResource(ConnectionProvide provide, Connection connection) {
		this.connection = connection;
		this.provide = provide;
	}
}
