package com.cdk8s.tkey.server.jpa.dao;

import com.cdk8s.tkey.server.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author tsb
 * @date 2020/8/13 11:33
 */
public interface UserDao extends JpaRepository<User, Integer> {

	User findUserByUsername(String username);

	@Query(value = "select * from sys_user where id=(select max(id) from sys_user)", nativeQuery = true)
	User maxIdUser();

}
