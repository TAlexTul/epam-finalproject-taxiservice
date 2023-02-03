package com.epam.alextuleninov.taxiservice.dao.user;

import com.epam.alextuleninov.taxiservice.connectionpool.DataSourceFields;
import com.epam.alextuleninov.taxiservice.dao.mappers.ResultSetMapper;
import com.epam.alextuleninov.taxiservice.data.order.OrderRequest;
import com.epam.alextuleninov.taxiservice.data.user.UserRequest;
import com.epam.alextuleninov.taxiservice.exceptions.datasource.UnexpectedDataAccessException;
import com.epam.alextuleninov.taxiservice.model.user.User;
import com.epam.alextuleninov.taxiservice.model.user.role.Role;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * DAO class for User.
 *
 * @author Oleksandr Tuleninov
 * @version 01
 */
public class JDBCUserDAO implements UserDAO {

    private final DataSource dataSource;
    private final ResultSetMapper<User> mapper;

    public JDBCUserDAO(DataSource dataSource, ResultSetMapper<User> mapper) {
        this.dataSource = dataSource;
        this.mapper = mapper;
    }

    /**
     * Create the user in the database.
     *
     * @param request       request with user parameters
     * @return              created user from database
     */
    @Override
    public User create(UserRequest request) {
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement createUser = connection.prepareStatement(
                    """
                            insert into users (first_name, last_name, email, password, role)
                            values (?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS)) {

                createUser.setString(1, request.firstName());
                createUser.setString(2, request.lastName());
                createUser.setString(3, request.email());
                createUser.setString(4, request.password());
                createUser.setString(5, Role.CLIENT.toString());

                createUser.executeUpdate();

                ResultSet generatedKeys = createUser.getGeneratedKeys();
                generatedKeys.next();
                long id = generatedKeys.getLong(1);

                connection.commit();

                return new User(
                        id,
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        null,
                        Role.CLIENT
                        );
            } catch (Exception e) {
                connection.rollback();
                throw new UnexpectedDataAccessException(e);
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        } catch (SQLException e) {
            throw new UnexpectedDataAccessException(e);
        }
    }

    /**
     * Find all users by client from the database.
     *
     * @return              all users from the database
     */
    @Override
    public Set<User> findAllClient() {
        Set<User> users = new TreeSet<>();

        try (Connection connection = dataSource.getConnection()) {
            try (var getUsers = connection.prepareStatement(
                    """
                            select * from users u
                            where u.role like 'CLIENT'
                            """
            )) {

                ResultSet resultSet = getUsers.executeQuery();

                while (resultSet.next()) {
                    users.add(mapper.map(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new UnexpectedDataAccessException(e);
        }
        return users;
    }

    /**
     * Find user by email from the database.
     *
     * @param request       request with order`s parameters
     * @return              user from database
     */
    @Override
    public Optional<User> findByEmail(OrderRequest request) {
        User user;

        try (Connection connection = dataSource.getConnection()) {
            try (var getUserByEmail = connection.prepareStatement(
                    """
                            select * from users as u where u.email like ?
                            """
            )) {
                getUserByEmail.setString(1, request.customer());

                ResultSet resultSet = getUserByEmail.executeQuery();
                if (resultSet.next()) {
                    user = mapper.map(resultSet);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new UnexpectedDataAccessException(e);
        }
        return Optional.of(user);
    }

    /**
     * Find user`s role  by email from the database.
     *
     * @param email         email by user
     * @return              user from database
     */
    @Override
    public String findRoleByEmail(String email) {
        String result = "";

        try (Connection connection = dataSource.getConnection()) {
            try (var getRoleByEmail = connection.prepareStatement(
                    """
                            select role from users u where u.email like ?
                            """
            )) {
                getRoleByEmail.setString(1, email);

                ResultSet resultSet = getRoleByEmail.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getString(DataSourceFields.USER_ROLE);
                }
            }
        } catch (SQLException e) {
            throw new UnexpectedDataAccessException(e);
        }
        return result;
    }

    /**
     * Check if user exists by email in the database.
     *
     * @param email         email by user
     * @return              true if user exists in database
     */
    @Override
    public boolean existsByEmail(String email) {
        String emailExists = null;

        try (Connection connection = dataSource.getConnection()) {
            try (var getUsersEmail = connection.prepareStatement(
                    """
                            select email from users u where u.email like ?
                            """
            )) {
                getUsersEmail.setString(1, email);

                ResultSet resultSet = getUsersEmail.executeQuery();
                while (resultSet.next()) {
                    emailExists = resultSet.getString(DataSourceFields.USER_EMAIL);
                }

                if (emailExists == null) {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new UnexpectedDataAccessException(e);
        }
        return true;
    }

    /**
     * Check if user exists by email and password in the database.
     *
     * @param email         email by user
     * @param password      password by user
     * @return              true if user exists in database
     */
    @Override
    public boolean existsByEmailPassword(String email, String password) {
        String emailExists = null;
        String passwordExists = null;

        try (Connection connection = dataSource.getConnection()) {
            try (var getAuthentication = connection.prepareStatement(
                    """
                            select email, password from users u where u.email like ? and u.password like ?
                            """
            )) {
                getAuthentication.setString(1, email);
                getAuthentication.setString(2, password);

                ResultSet resultSet = getAuthentication.executeQuery();
                while (resultSet.next()) {
                    emailExists = resultSet.getString(DataSourceFields.USER_EMAIL);
                    passwordExists = resultSet.getString(DataSourceFields.USER_PASSWORD);
                }

                if (emailExists == null || passwordExists == null) {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new UnexpectedDataAccessException(e);
        }
        return true;
    }
}