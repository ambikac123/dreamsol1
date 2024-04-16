package com.dreamsol.services.impl;

import com.dreamsol.entities.User;
import com.dreamsol.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    @Autowired private UserRepository userRepository;

    // loadUserByUsername() method is called by spring security framework while attempting to login
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user = userRepository.findByUserEmail(username);
        if(Objects.isNull(user)) {
            throw new UsernameNotFoundException("User with username: [" + username + "] not found!");
        }
        return new UserDetailsImpl(user);
    }
}
