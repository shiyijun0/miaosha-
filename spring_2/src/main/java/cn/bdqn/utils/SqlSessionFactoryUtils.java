package cn.bdqn.utils;

import cn.bdqn.mapper.RoleMapper;
import cn.bdqn.mapper.RoleMapper2;
import cn.bdqn.pojo.Role;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SqlSessionFactoryUtils {

	private final static Class<SqlSessionFactoryUtils> LOCK = SqlSessionFactoryUtils.class;

	private static SqlSessionFactory sqlSessionFactory = null;

	private SqlSessionFactoryUtils() {
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		synchronized (LOCK) {
			if (sqlSessionFactory != null) {
				return sqlSessionFactory;
			}
			String resource = "mybatis-config.xml";
			InputStream inputStream;
			try {
				/*inputStream = Resources.getResourceAsStream(resource);
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);*/


				//采用程序传递加密参数,启用它之前，修改用户密码为密文，可参考main方法......
				InputStream in = Resources.getResourceAsStream("jdbc.properties");
				Properties props = new Properties();
				props.load(in);
				String username = props.getProperty("database.username");
				String password = props.getProperty("database.password");
				// 解密用户和密码，并在属性中重置
				try {
					props.put("database.username", EncrypDES.getInstance().decrypt(username));
					props.put("database.password", EncrypDES.getInstance().decrypt(password));
				}catch (Exception e){

				}

//				props.put("database.username", CodeUtils.decode(username));
//				props.put("database.password", CodeUtils.decode(password));
				inputStream = Resources.getResourceAsStream(resource);
				// 使用程序传递的方式覆盖原有的properties属性参数
				sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props);

			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return sqlSessionFactory;
		}
	}


	//代码生成SqlSessionFactory
	public static SqlSessionFactory getSqlSessionFactory2() {
		synchronized (LOCK) {
			//数据库连接池信息
			PooledDataSource dataSource = new PooledDataSource();
			dataSource.setDriver("com.mysql.jdbc.Driver");
			dataSource.setUsername("root");
			dataSource.setPassword("123456");
			dataSource.setUrl("jdbc:mysql://localhost:3306/test");
			dataSource.setDefaultAutoCommit(false);
			//采用MyBatis的JDBC事务方式
			TransactionFactory transactionFactory = new JdbcTransactionFactory();
			Environment environment = new Environment("development", transactionFactory, dataSource);
			//创建Configuration对象
			Configuration configuration = new Configuration(environment);
			//注册一个MyBatis上下文别名
			configuration.getTypeAliasRegistry().registerAlias("role", Role.class);
			//加入一个映射器
			configuration.addMapper(RoleMapper.class);
			configuration.addMapper(RoleMapper2.class);
			//使用SqlSessionFactoryBuilder构建SqlSessionFactory
			sqlSessionFactory =
					new SqlSessionFactoryBuilder().build(configuration);
			return sqlSessionFactory;
		}
	}

	public static SqlSession openSqlSession() {
		if (sqlSessionFactory == null) {
			getSqlSessionFactory();
		}
		return sqlSessionFactory.openSession();
	}
}