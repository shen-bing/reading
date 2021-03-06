package readinglist.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

import readinglist.domain.Reader;
import readinglist.repository.ReaderRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig
extends WebSecurityConfigurerAdapter
{
    @Autowired
    private ReaderRepository readerRepository;

    @Override
    protected void configure(HttpSecurity http)
    throws Exception
    {
        http.authorizeRequests()
                .antMatchers("/readingList").access("hasRole('READER')")
                .antMatchers("/**").permitAll()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/readingList")
                .failureUrl("/login?error=true");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
    throws Exception
    {
        auth.userDetailsService(new UserDetailsService()
        {
            @Override
            public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException
            {
                Optional<Reader> userDetails = readerRepository.findById(username);

                if ( userDetails.isPresent() )
                {
                    return userDetails.get();
                }

                throw new UsernameNotFoundException("User '" + username + "' not found.'");
            }
        });
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
