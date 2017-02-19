package com.dns.webservice;

import com.dns.application.Application;
import com.dns.model.UserEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;


/**
 * This is test class for {@link UserController}
 * Created by dhiren on 30/12/16.
 * @author dhiren
 * @since 30-12-2016
 * @see UserController
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void saveUser()
    {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("admin");
        userEntity.setFirstName("Admin");
        userEntity.setLastName("admin");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserEntity>userEntityHttpEntity = new HttpEntity<>(userEntity, httpHeaders);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("/sign-up",userEntityHttpEntity, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void getUserByUserName() throws Exception {
        ResponseEntity<UserEntity>userEntityResponseEntity = testRestTemplate.getForEntity("/user-by-username/admin", UserEntity.class);
        assertThat(userEntityResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userEntityResponseEntity.getBody().getFirstName()).isEqualTo("Admin");
        userEntityResponseEntity = testRestTemplate.getForEntity("/user-by-username/admin", UserEntity.class);
        assertThat(userEntityResponseEntity.getStatusCode().is2xxSuccessful());
        assertThat(userEntityResponseEntity.getBody() == null);
    }

    @Test
    public void getUserByUserId() throws Exception {
        ResponseEntity<UserEntity>userEntityResponseEntity = testRestTemplate.getForEntity("/user/1", UserEntity.class);
        assertThat(userEntityResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userEntityResponseEntity.getBody().getFirstName()).isEqualTo("Admin");
    }
}