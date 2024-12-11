package com.mss.checkin.util;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataServiceLocator {

    @Autowired
    AWSSSMClient awsssmClient;


    @Bean
    @Primary
    public DataSource getCheckInDataSource() {



        String dbUserUrl = awsssmClient.getSSMParameterValue("/check-in/db-host");
        String dbUserName = awsssmClient.getSSMParameterValue("/check-in/db-username");
        String dbUserPassword = awsssmClient.getSSMParameterValue("/check-in/db-password");


        	//System.out.println("dbUserUrl--"+dbUserUrl);
        	//System.out.println("dbUserName--"+dbUserName);
        	//System.out.println("dbUserPassword--"+dbUserPassword);


        dbUserUrl = "jdbc:mysql://"+dbUserUrl+":3306/mydb?useSSL=false";
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

       // dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(dbUserUrl);
        dataSource.setUsername(dbUserName);
        dataSource.setPassword(dbUserPassword);
        return dataSource;
    }
    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate(@Qualifier("getCheckInDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(@Qualifier("getCheckInDataSource") DataSource ds) {
        return new NamedParameterJdbcTemplate(ds);
    }

}
