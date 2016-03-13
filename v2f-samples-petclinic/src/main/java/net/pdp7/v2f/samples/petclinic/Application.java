package net.pdp7.v2f.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

import net.pdp7.v2f.core.DefaultConfiguration;

@SpringBootApplication
@ServletComponentScan
@Import(DefaultConfiguration.class)
public class Application {

	protected Application() {
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
