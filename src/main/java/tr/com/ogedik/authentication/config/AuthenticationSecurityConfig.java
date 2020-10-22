package tr.com.ogedik.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tr.com.ogedik.authentication.AuthenticationEntrance;
import tr.com.ogedik.authentication.service.UserService;
import tr.com.ogedik.authentication.service.impl.AuthenticationRequestFilter;
import tr.com.ogedik.commons.constants.Services;

/**
 * This class extends the {@link WebSecurityConfigurerAdapter}. This is a convenience class that
 * allows customization to both WebSecurity and HttpSecurity.
 *
 * @author orkun.gedik
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthenticationSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired private AuthenticationEntrance authenticationEntrance;
  @Autowired private UserService userService;
  @Autowired private AuthenticationRequestFilter authenticationRequestFilter;

  /**
   * Configures AuthenticationManager so that it knows from where to load user for matching
   * credentials Use BCryptPasswordEncoder
   *
   * @param authenticationManager {@link AuthenticationManagerBuilder}
   * @throws Exception
   */
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder authenticationManager) {
    try {
      authenticationManager.userDetailsService(userService).passwordEncoder(passwordEncoder());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    // We don't need CSRF for this example
    httpSecurity
        .csrf()
        .disable()
        // dont authenticate this particular request
        .authorizeRequests()
        .antMatchers(
            HttpMethod.POST,
            Services.Path.AUTHENTICATE,
            Services.Path.VALIDATE,
            Services.Path.USERS)
        .permitAll()
        // all other requests need to be authenticated
        .anyRequest()
        .authenticated()
        .and()
        // make sure we use stateless session; session won't be used to
        // store user's state.
        .exceptionHandling()
        .authenticationEntryPoint(authenticationEntrance)
        .and()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    // Add a filter to validate the tokens with every request
    httpSecurity.addFilterBefore(
        authenticationRequestFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
