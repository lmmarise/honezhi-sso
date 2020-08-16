package com.honezhi.sso.server.jpa.dao;

import com.honezhi.sso.server.jpa.entity.OauthClient;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tsb
 * @date 2020/8/14 18:01
 * @description
 */
public interface OauthClientDao extends JpaRepository<OauthClient, Integer> {
}
