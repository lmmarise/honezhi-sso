package com.honezhi.sso.server.jpa.dao;

import com.honezhi.sso.server.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author tsb
 * @date 2020/8/13 11:33
 */
public interface UserDao extends JpaRepository<User, Integer> {

    User findUserByUsername(String username);

    @Query(value = "SELECT * FROM sys_user WHERE id=(SELECT max(id) FROM sys_user)", nativeQuery = true)
    User maxIdUser();

}
