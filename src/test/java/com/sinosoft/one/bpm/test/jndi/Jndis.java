package com.sinosoft.one.bpm.test.jndi;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import oracle.jdbc.pool.OracleDataSource;

public class Jndis {
	public static void bind() {
		// TODO Auto-generated method stub //设置环境参数
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.PROVIDER_URL, "file:E:/jndi");

		try {
			Context ctx = new InitialContext(env);
			// NamingEnumeration<NameClassPair> ne=ctx.list(""); //
			// NameClassPair nc=null; // while(ne.hasMore()){
			// nc=(NameClassPair)ne.next();
			// System.out.println(nc.getName());
			// }
			oracle.jdbc.xa.client.OracleXADataSource ods = new oracle.jdbc.xa.client.OracleXADataSource();// 新建oracle数据源对象
															// //设置参数
			ods.setDriverType("thin");
			ods.setUser("dbictest");
			ods.setDatabaseName("orcl");
			ods.setPassword("dbictest");
			ods.setNetworkProtocol("tcp");
			ods.setPortNumber(1521);
			ods.setServerName("127.0.0.1");
			ctx.bind("myDataSource", ods);// 关键的一步(绑定后会在E:/soft/下面建立一个名字为.bindings的文件)
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void lookup() {
		// TODO Auto-generated method stub //设置环境参数
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.PROVIDER_URL, "file:E:/jndi");
		Context ctx = null;
		OracleDataSource ods = null;
		Statement st = null;

		try {
			ctx = new InitialContext(env);
			ods = (OracleDataSource) ctx.lookup("mydata");// 关键的一步
		} catch (NamingException e) {
			e.printStackTrace();
		}
		try {
			Connection con = ods.getConnection();// 根据上面从JNDI服务器上得到的数据源建立数据库连接
			st = con.createStatement();
			ResultSet rs = st.executeQuery("select id,name from yupf_test");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
