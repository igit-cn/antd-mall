/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package com.igomall.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.igomall.Page;
import com.igomall.Pageable;
import com.igomall.entity.Admin;
import com.igomall.service.AdminService;

/**
 * Controller - 管理员
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@RestController("apiAdminController")
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

	@Autowired
	private AdminService adminService;


	/**
	 * 列表
	 */
	@GetMapping("/list")
	@JsonView(Admin.ListView.class)
	public Page<Admin> list(Pageable pageable) {
		return adminService.findPage(pageable);
	}

}