
package com.igomall.service.impl;

import java.util.List;

import javax.inject.Inject;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.igomall.dao.DepartmentDao;
import com.igomall.entity.Department;
import com.igomall.service.DepartmentService;

/**
 * Service - 部门
 * 
 * @author IGOMALL  Team
 * @version 1.0
 */
@Service
public class DepartmentServiceImpl extends BaseServiceImpl<Department, Long> implements DepartmentService {

	@Inject
	private DepartmentDao departmentDao;

	@Transactional(readOnly = true)
	public List<Department> findRoots() {
		return departmentDao.findRoots(null);
	}

	@Transactional(readOnly = true)
	public List<Department> findRoots(Integer count) {
		return departmentDao.findRoots(count);
	}

	@Transactional(readOnly = true)
	public List<Department> findParents(Department department, boolean recursive, Integer count) {
		return departmentDao.findParents(department, recursive, count);
	}

	@Transactional(readOnly = true)
	public List<Department> findChildren(Department department, boolean recursive, Integer count) {
		return departmentDao.findChildren(department, recursive, count);
	}

	@Override
	@Transactional
	@CacheEvict(value = "department", allEntries = true)
	public Department save(Department department) {
		Assert.notNull(department);

		setValue(department);
		return super.save(department);
	}

	@Override
	@Transactional
	@CacheEvict(value = "department", allEntries = true)
	public Department update(Department department) {
		Assert.notNull(department);

		setValue(department);
		for (Department children : departmentDao.findChildren(department, true, null)) {
			setValue(children);
		}
		return super.update(department);
	}

	@Override
	@Transactional
	@CacheEvict(value = "department", allEntries = true)
	public Department update(Department department, String... ignoreProperties) {
		return super.update(department, ignoreProperties);
	}

	@Override
	@Transactional
	@CacheEvict(value = "department", allEntries = true)
	public void delete(Long id) {
		super.delete(id);
	}

	@Override
	@Transactional
	@CacheEvict(value = "department", allEntries = true)
	public void delete(Long... ids) {
		super.delete(ids);
	}

	@Override
	@Transactional
	@CacheEvict(value = "department", allEntries = true)
	public void delete(Department department) {
		super.delete(department);
	}

	/**
	 * 设置值
	 * 
	 * @param department
	 *            部门
	 */
	private void setValue(Department department) {
		if (department == null) {
			return;
		}
		Department parent = department.getParent();
		if (parent != null) {
			department.setFullName(parent.getFullName() + department.getName());
			department.setTreePath(parent.getTreePath() + parent.getId() + Department.TREE_PATH_SEPARATOR);
		} else {
			department.setFullName(department.getName());
			department.setTreePath(Department.TREE_PATH_SEPARATOR);
		}
		department.setGrade(department.getParentIds().length);
	}

}