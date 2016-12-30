package com.dns.webservice;

import com.dns.dao.UserEntityDao;
import com.dns.model.Role;
import com.dns.model.UserEntity;
import com.dns.websecurity.RestAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

/**
 * This is webservice controller for this application
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see UserEntityDao
 * @see UserEntity
 * @see PasswordEncoder
 */

@RestController
@CrossOrigin
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserEntityDao userEntityDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserEntityDao userEntityDao, PasswordEncoder passwordEncoder) {
        this.userEntityDao = userEntityDao;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This method handle after successfully login user will get his own details
     * @return ResponseEntity of {@link UserEntity}
     */
    @RequestMapping(value = "user", method = RequestMethod.GET)
    public ResponseEntity<UserEntity> getUser()
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken)) {
                String userName = (String) auth.getPrincipal();
                LOGGER.info("authenticated user request");
                UserEntity userEntity = userEntityDao.findByUsername(userName);
                userEntity.setPassword(null);
                return ResponseEntity.ok(userEntity);
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle sign up request of {@link UserEntity}
     * @param userEntity {@link UserEntity} as RequestBody
     * @return HttpStatus
     */
    @RequestMapping(value = "sign-up", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserEntity userEntity)
    {
        try
        {
            LOGGER.info("User save request");
            if(userEntity.getRoles().isEmpty())
                userEntity.getRoles().add(Role.USER);
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            userEntityDao.save(userEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        }
        catch (Exception e)
        {
            LOGGER.error("Error in save", e);
            return ResponseEntity.badRequest().body(null);
        }
    }


    /**
     * This method handle admin request that get user details User id
     * @param id {@link Long} userId
     * @return ResponseEntity of {@link UserEntity}
     */
    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserEntity> getUserByUserId(@PathVariable("id")Long id)
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                LOGGER.info("admin request");
                return ResponseEntity.ok(userEntityDao.findOne(id));
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle admin request that get all {@link UserEntity}
     * @return ResponseEntity of {@link List} of {@link UserEntity}
     */
    @RequestMapping(value = "all-user", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> getAllUser()
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                LOGGER.info("admin request");
                return ResponseEntity.ok(userEntityDao.findAll());
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle admin request that is delete user by user id
     * @return HttpStatus
     */
    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id")Long id)
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                LOGGER.info("admin request");
                userEntityDao.delete(id);
                return ResponseEntity.ok(null);
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle search {@link UserEntity} based on firstName and lastName
     * @param firstName {@link String}
     * @param lastName {@link String}
     * @return ResponseEntity of {@link List} of {@link UserEntity}
     */
    @RequestMapping(value = "user-by-firstname-lastname", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> searchUserByFirstNameAndLastName(@RequestParam("firstName")String firstName, @RequestParam("lastName")String lastName)
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                LOGGER.info("admin request for fetch user by firstName and lastName ");
                List<UserEntity>userEntities = userEntityDao.findByFirstNameAndLastName(firstName, lastName);
                userEntities.forEach(userEntity -> userEntity.setPassword(null));
                return ResponseEntity.ok(userEntities);
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle search {@link UserEntity} based on firstName
     * @param firstName {@link String}
     * @return ResponseEntity of {@link List} of {@link UserEntity}
     */
    @RequestMapping(value = "user-by-firstname", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> searchUserByFirstName(@RequestParam("firstName")String firstName)
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                LOGGER.info("admin request for fetch user by firstName");
                List<UserEntity>userEntities = userEntityDao.findByFirstName(firstName);
                userEntities.forEach(userEntity -> userEntity.setPassword(null));
                return ResponseEntity.ok(userEntities);
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle search {@link UserEntity} based on lastName
     * @param lastName {@link String}
     * @return ResponseEntity of {@link List} of {@link UserEntity}
     */
    @RequestMapping(value = "user-by-lastname", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> searchUserByLastName(@RequestParam("lastName")String lastName)
    {
        try
        {
            Authentication auth = SecurityContextHolder.getContext()
                    .getAuthentication();
            if (!(auth instanceof AnonymousAuthenticationToken) && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                LOGGER.info("admin request for fetch user by lastName");
                List<UserEntity>userEntities = userEntityDao.findByLastName(lastName);
                userEntities.forEach(userEntity -> userEntity.setPassword(null));
                return ResponseEntity.ok(userEntities);
            } else {
                LOGGER.error("Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
