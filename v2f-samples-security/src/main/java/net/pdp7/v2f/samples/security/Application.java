package net.pdp7.v2f.samples.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import net.pdp7.v2f.core.DefaultConfiguration;

@SpringBootApplication
@ServletComponentScan
@EnableWebSecurity
@Import(DefaultConfiguration.class)
public class Application {

	protected Application() {
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) {
		try {
			auth
				.jdbcAuthentication()
				.dataSource(dataSource)
				.usersByUsernameQuery("select username, password, enabled from _users where username = ?")
				.authoritiesByUsernameQuery("select username, authority from _authorities where username = ?");
		} catch (Exception e) {
			throw new RuntimeException("Error configuring security", e);
		}
	}

	@Autowired
	public DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
