/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package com.igomall.dao;

import com.igomall.entity.AuditLog;

/**
 * Dao - 审计日志
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
public interface AuditLogDao extends BaseDao<AuditLog, Long> {

	/**
	 * 删除所有
	 */
	void removeAll();

}