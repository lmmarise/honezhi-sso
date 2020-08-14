package com.cdk8s.tkey.server.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author tsb
 * @date 2020/8/14 18:01
 * @description
 */
public interface OauthClientDao extends JpaRepository<OauthClientDao, Integer> {
}
