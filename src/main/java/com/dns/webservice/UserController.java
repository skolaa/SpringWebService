package com.dns.webservice;

import com.dns.dao.UserEntityDao;
import com.dns.model.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
 */

@RestController
@CrossOrigin
@RequestMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserEntityDao userEntityDao;

    @Autowired
    public UserController(UserEntityDao userEntityDao) {
        this.userEntityDao = userEntityDao;
    }

    /**
     * This method handle after successfully login user will get his own details
     * @return ResponseEntity of {@link UserEntity}
     */
    @RequestMapping(value = "user-by-username/{username}", method = RequestMethod.GET)
    public ResponseEntity<UserEntity> getUserByUserName(@PathVariable("username")String userName)
    {
        try
        {
                LOGGER.info("user request by username");
                UserEntity userEntity = userEntityDao.findByUsername(userName);
                return ResponseEntity.ok(userEntity);
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
                LOGGER.info("get user by id");
                return ResponseEntity.ok(userEntityDao.findOne(id));
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * This method handle admin request that get user details User id
     * @param id {@link Long} userId
     * @return ResponseEntity of {@link UserEntity}
     */
    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<UserEntity> updateUser(@PathVariable("id")Long id, @RequestBody UserEntity userEntity)
    {
        try
        {
            LOGGER.info("update user request");
            UserEntity fetchedUser = userEntityDao.findOne(id);
            if (fetchedUser == null)
                return ResponseEntity.notFound().build();
            fetchedUser.setFirstName(userEntity.getFirstName());
            fetchedUser.setLastName(userEntity.getLastName());
            fetchedUser.setUsername(userEntity.getUsername());
            return ResponseEntity.ok(userEntityDao.save(fetchedUser));
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
                LOGGER.info("delete user request");
                userEntityDao.delete(id);
                return ResponseEntity.ok(null);
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
    @RequestMapping(value = "user-by-firstName-lastName", method = RequestMethod.GET)
    public ResponseEntity<List<UserEntity>> searchUserByFirstNameAndLastName(@RequestParam("firstName")String firstName,
                                                                             @RequestParam("lastName")String lastName)
    {
        try
        {
                LOGGER.info("fetch user by firstName and lastName ");
                List<UserEntity>userEntities = userEntityDao.findByFirstNameAndLastName(firstName, lastName);
                return ResponseEntity.ok(userEntities);
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
                LOGGER.info("fetch user by firstName");
                List<UserEntity>userEntities = userEntityDao.findByFirstName(firstName);
                return ResponseEntity.ok(userEntities);
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
                LOGGER.info("fetch user by lastName");
                List<UserEntity>userEntities = userEntityDao.findByLastName(lastName);
                return ResponseEntity.ok(userEntities);
        }
        catch (Exception e)
        {
            LOGGER.error("error in UserEntity fetching", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
