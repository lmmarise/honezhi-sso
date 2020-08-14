package com.cdk8s.tkey.server.jpa.entity;

import lombok.Data;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

/**
 * @author tsb
 * @date 2020/8/14 17:03
 * @description
 */
@Data
@Entity(name = "oauth_client")
@Table(name = "oauth_client")
public class OauthClient {

	@Id
	private Long id;

	@Column(name = "client_name", length = 35, nullable = false)
	private int clientName;

	@Column(name = "client_id", length = 35, nullable = false)
	private String clientId;

	@Column(name = "client_secret", length = 35, nullable = false)
	private String clientSecret;

	@Column(name = "client_url", length = 200, nullable = true, columnDefinition = "varchar(200) comment '账号匹配的网站，支持正则符号'")
	private String clientUrl;

	@Column(name = "client_desc", length = 50, nullable = true)
	private String clientDesc;

	@Column(name = "logo_url", length = 200, nullable = false, columnDefinition = "varchar(200) comment 'logo 的链接地址'")
	private String logoUrl;

	@Column(name = "ranking", nullable = false, columnDefinition = "tinyint comment '排序，默认值100，值越小越靠前(rank是保留字)'")
	private short ranking;

	@Column(name = "remark", length = 255, nullable = true, columnDefinition = "varchar(255) comment '备注'")
	private String remark;

	@Column(name = "state_enum", length = 255,nullable = false, columnDefinition = "tinyint comment '是否启动, 1正常，2禁用'")
	private short stateEnum;

	@Column(name = "delete_enum",nullable = false, columnDefinition = "tinyint comment '是否删除, 1正常，2删除'")
	private short deleteEnum;

	@Column(name = "create_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "create_user_id", nullable = false)
	private int createUserId;

	@Column(name = "update_date", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP comment '修改时间'")
	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Column(name = "update_user_id", nullable = false)
	private int updateUserId;

	@Column(name = "delete_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date deleteDate;

	@Column(name = "delete_user_id", nullable = true, columnDefinition = "bigint comment '删除人'")
	private int deleteUserId;

}
