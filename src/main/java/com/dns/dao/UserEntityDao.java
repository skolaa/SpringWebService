package com.dns.dao;

import com.dns.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * This is {@link UserEntity} repository that extends {@link JpaRepository}
 * Created by dhiren on 29/12/16.
 * @author dhiren
 * @since 29-12-2016
 * @see UserEntity
 */

public interface UserEntityDao extends JpaRepository<UserEntity, Long> {

    /**
     * This method return {@link UserEntity} based on username
     * @param userName {@link String}
     * @return UserEntity
     */
    UserEntity findByUsername(String userName);

    /**
     * This method return list of user based on firstName and lastName
     * @param firstName {@link String}
     * @param lastName {@link String}
     * @return java.util.List of {@link UserEntity}
     */
    List<UserEntity> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * This method return list of user based on firstName
     * @param firstName {@link String}
     * @return java.util.List of {@link UserEntity}
     */
    List<UserEntity> findByFirstName(String firstName);

    /**
     * This method return list of user based on lastName
     * @param lastName {@link String}
     * @return java.util.List of {@link UserEntity}
     */
    List<UserEntity> findByLastName(String lastName);
}
