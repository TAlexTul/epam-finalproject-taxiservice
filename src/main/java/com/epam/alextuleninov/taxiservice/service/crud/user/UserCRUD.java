package com.epam.alextuleninov.taxiservice.service.crud.user;

import com.epam.alextuleninov.taxiservice.data.user.UserRequest;

import java.util.List;

/**
 * Interface CRUD for User.
 *
 * @author Oleksandr Tuleninov
 * @version 01
 */
public interface UserCRUD {

    /**
     * Create the user in the database.
     *
     * @param request       request with user`s parameters
     * @return              created user from database
     */
    boolean register(UserRequest request);

    /**
     * Find all logins of clients.
     *
     * @return              list with logins
     */
    List<String> findAllLoginsClient();

    /**
     * Check if user exists by email in the database.
     *
     * @param email         email by user
     * @return              true if user exists in database
     */
    boolean existsByEmail(String email);


    boolean authentication(String email, String password);

    /**
     * Find user`s role  by email from the database.
     *
     * @param email         email by user
     * @return              user from database
     */
    String findRoleByEmail(String email);
}