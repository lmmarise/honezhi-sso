package com.honezhi.sso.server.jpa.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tsb
 * @date 2020/8/13 11:04
 */
@Data
@Entity(name = "sys_user")
@Table(name = "sys_user", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", length = 64)
	private String username;

	@Column(name = "password", length = 128)
	private String password;

	@Column(name = "expired")
	private int expired;

	@Column(name = "disabled")
	private int disabled;

	@Column(name = "user_email", nullable = true, length = 320)
	private String userEmail;

	@Column(name = "create_time")
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Column(name = "update_time", insertable = false, updatable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '用户修改时间'")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTime;

}
