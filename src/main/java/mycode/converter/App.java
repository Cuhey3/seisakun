package mycode.converter;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class App {

    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss.SSS");

    public static void main(String[] args) throws Exception {
        System.out.println(sdf.format(new Date()) + " [SYSTEM] システムを起動しています…");
        new SpringApplication(App.class).run();
        Thread.sleep(Long.MAX_VALUE);
    }

    @Bean
    public DriverManagerDataSource source() {
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName("sun.jdbc.odbc.JdbcOdbcDriver");
        source.setUrl("jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=program\\zip.mdb;");
        source.setUsername("");
        source.setPassword("");
        return source;
    }
    /*    @Bean(name = "sqlite")
     public DriverManagerDataSource sqlite() {
     DriverManagerDataSource source = new DriverManagerDataSource();
     source.setDriverClassName("org.sqlite.JDBC");
     source.setUrl("jdbc:sqlite:program\\zenkoku.sqlite3");
     return source;
     }*/
}
