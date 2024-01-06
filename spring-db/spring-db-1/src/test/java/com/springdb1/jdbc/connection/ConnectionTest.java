package com.springdb1.jdbc.connection;


import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.springdb1.jdbc.connection.ConnectionConst.*;

@Slf4j // build.gradle에 Test용 lombok dependency를 따로 추가해야 사용 가능하다.
public class ConnectionTest {

    // 1. DriverManager 사용해서 Connection 얻기
    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", con1, con1.getClass()); // connection=conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class org.h2.jdbc.JdbcConnection
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    // 2. DriverManagerDataSource 사용
    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource - DriverManager와 마찬가지로 항상 새로운 커넥션을 획득 (패키지를 보면 알겠지만 스프링에서 제공하는 것이다)
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD); // ★DriverManager와 다른 점은 DataSource 구현체이다. -> Connection 얻는 방법(구현체)을 바꿔야 할 때 편리하다
        useDataSource(dataSource);
    }

    // 3. ConnectionPool 사용
    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링 : HikariProxyConnection(Proxy) -> JdbcConnection(Target)
        HikariDataSource dataSource = new HikariDataSource(); // cf. 스프링 JDBC 의존성을 추가하면 Hikari가 자동으로 추가된다.
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10); // 최대 CP 숫자를 정함
        dataSource.setPoolName("YongPool"); // Pool 이름을 정함

        useDataSource(dataSource);
        Thread.sleep(1000); // 커넥션 풀에서 커넥션 생성 시간 대기

        // 만약 CP 개수보다 더 많은 커넥션을 연결하려고 하면 어떻게 될까??
        // [img 폴더 참조] ★가능한 커넥션 개수(10)보다 더 많은 커넥션(11)을 요청한 것임. 계속 커넥션을 얻기 위해 요청(?)을 날리고 있지만, 30초가 지나버려서 timeout이 되면서 예외가 발생해버림.
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        Connection con3 = dataSource.getConnection();
        Connection con4 = dataSource.getConnection();
        Connection con5 = dataSource.getConnection();
        Connection con6 = dataSource.getConnection();
        Connection con7 = dataSource.getConnection();
        Connection con8 = dataSource.getConnection();
        Connection con9 = dataSource.getConnection();
        Connection con10 = dataSource.getConnection();
        //Connection con11 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }
}
