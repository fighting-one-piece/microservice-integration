package org.platform.modules.oauth;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class ServiceTest {

	@Test
	public void testPasswordEncoder() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodePassword = passwordEncoder.encode("client_secret_1");
		System.err.println(encodePassword);
		System.err.println(passwordEncoder.matches("client_secret_1", encodePassword));
		encodePassword = passwordEncoder.encode("password1");
		System.err.println(encodePassword);
	}
	
}
