/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package com.igomall.service;

import java.util.List;

import com.igomall.entity.Department;

/**
 * Service - 部门
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
public interface DepartmentService extends BaseService<Department, Long> {

	/**
	 * 查找顶级部门
	 * 
	 * @return 顶级部门
	 */
	List<Department> findRoots();

	/**
	 * 查找顶级部门
	 * 
	 * @param count
	 *            数量
	 * @return 顶级部门
	 */
	List<Department> findRoots(Integer count);

	/**
	 * 查找上级部门
	 * 
	 * @param department
	 *            部门
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 上级部门
	 */
	List<Department> findParents(Department department, boolean recursive, Integer count);

	/**
	 * 查找下级部门
	 * 
	 * @param department
	 *            部门
	 * @param recursive
	 *            是否递归
	 * @param count
	 *            数量
	 * @return 下级部门
	 */
	List<Department> findChildren(Department department, boolean recursive, Integer count);

}