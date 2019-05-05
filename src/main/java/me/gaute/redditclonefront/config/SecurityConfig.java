package me.gaute.redditclonefront.config;

import me.gaute.redditclonefront.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserService userService;

//
//    @Override
//    protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
//        authBuilder.inMemoryAuthentication().
//                withUser("ali@oslomet.no")
//                .password("{noop}test")
//                .roles("USER");
//    }


    @Override
    protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
        authBuilder.userDetailsService(userService);
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/", "/login", "/register", "/register/**", "signup", "/processRegistration", "/h2-console/**", "/home", "/r", "/r/**", "/search", "/searching", "/u", "/u/**").permitAll()
                .antMatchers("/myAccount", "/testImages", "/uploadImage", "/changePassword", "/passwordChange", "/deleteAccount", "/accountDelete", "/submit", "/createSubreddit", "/subscribe", "/unsubscribe", "/unfollow/**", "/downvote", "/downvote/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/users").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .permitAll();
    }




}
