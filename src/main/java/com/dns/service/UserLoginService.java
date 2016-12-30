package com.dns.service;

import com.dns.dao.UserEntityDao;
import com.dns.model.Role;
import com.dns.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * This is User login Service for {@link UserEntity} that implements {@link UserDetailsService}
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see UserDetailsService
 * @see UserEntityDao
 * @see User
 */

@Service
public class UserLoginService implements UserDetailsService {

    @Autowired
    private UserEntityDao userEntityDao;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityDao.findByUsername(s);
        if (userEntity == null)
            throw new UsernameNotFoundException("User not found");
        return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList()));
    }
}
