
package com.igomall.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.igomall.Pageable;
import com.igomall.entity.Department;
import com.igomall.service.DepartmentService;

/**
 * Controller - 管理员
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@RestController("apiDepartmentController")
@RequestMapping("/api/department")
public class DepartmentController extends BaseController {

	@Autowired
	private DepartmentService departmentService;


	/**
	 * 列表
	 */
	@GetMapping("/list")
	@JsonView(Department.ListView.class)
	public List<Department> list(Pageable pageable) {
		return departmentService.findRoots();
	}

}