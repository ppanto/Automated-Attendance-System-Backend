package com.panto.attendance.service;

import com.panto.attendance.model.ApplicationUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;


@Component
@AllArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private ApplicationUserService applicationUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        ApplicationUser user = applicationUserService.get(username);
        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                new ArrayList<>()
                //Arrays.asList(new SimpleGrantedAuthority("hr"))
        );
    }
}
