package com.ssm.chapter21.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**** imports ****/
@Configuration
// ����Spring ɨ��İ�
@ComponentScan("com.*")
// ʹ����������������
@EnableTransactionManagement
// ʵ�ֽӿ�TransactionManagementConfigurer��������������ע����������
public class RootConfig implements TransactionManagementConfigurer {

	private DataSource dataSource = null;

	/**
	 * �������ݿ�
	 * 
	 * @return �������ӳ�
	 */
	@Bean(name = "dataSource")
	public DataSource initDataSource() {
		if (dataSource != null) {
			return dataSource;
		}
		Properties props = new Properties();
		props.setProperty("driverClassName", "com.mysql.jdbc.Driver");
		props.setProperty("url", "jdbc:mysql://localhost:3306/chapter21");
		props.setProperty("username", "root");
		props.setProperty("password", "123456");
		try {
			dataSource = BasicDataSourceFactory.createDataSource(props);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSource;
	}

	/**
	 * * ����SqlSessionFactoryBean
	 * 
	 * @return SqlSessionFactoryBean
	 */
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean initSqlSessionFactory() {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(initDataSource());
		// ����MyBatis�����ļ�
		Resource resource = new ClassPathResource("mybatis/mybatis-config.xml");
		sqlSessionFactory.setConfigLocation(resource);
		return sqlSessionFactory;
	}

	/**
	 * * ͨ���Զ�ɨ�裬����MyBatis Mapper�ӿ�
	 *
	 * @return Mapperɨ����
	 */
	@Bean
	public MapperScannerConfigurer initMapperScannerConfigurer() {
		MapperScannerConfigurer msc = new MapperScannerConfigurer();
		// ɨ���
		msc.setBasePackage("com.*");
		msc.setSqlSessionFactoryBeanName("sqlSessionFactory");
		// ����ע��ɨ��
		msc.setAnnotationClass(Repository.class);
		return msc;
	}

	/**
	 * ʵ�ֽӿڷ�����ע��ע�����񣬵�@Transactional ʹ�õ�ʱ��������ݿ�����
	 */
	@Override
	@Bean(name = "annotationDrivenTransactionManager")
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(initDataSource());
		return transactionManager;
	}

}