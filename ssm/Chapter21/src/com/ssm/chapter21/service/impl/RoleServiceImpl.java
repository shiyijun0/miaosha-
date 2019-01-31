package com.ssm.chapter21.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ssm.chapter21.dao.RoleDao;
import com.ssm.chapter21.pojo.Role;
import com.ssm.chapter21.service.RoleService;

/**** imports ****/
@Service
public class RoleServiceImpl implements RoleService {
	// ��ɫDAO������ִ��SQL
	@Autowired
	private RoleDao roleDao = null;

	/**
	 * ʹ��@Cacheable���建����� ����������ֵ���򷵻ػ������ݣ�������ʷ����õ����� ͨ��value���û����������ͨ��key�����
	 * 
	 * @param id
	 *            ��ɫ���
	 * @return ��ɫ
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@Cacheable(value = "redisCacheManager", key = "'redis_role_'+#id")
	public Role getRole(Long id) {
		return roleDao.getRole(id);
	}

	/**
	 * ʹ��@CachePut���ʾ������ζ���ִ�з�������󽫷����ķ���ֵ�ٱ��浽������
	 * ʹ���ڲ������ݵĵط������ʾ���浽���ݿ�󣬻�ͬ�ڲ��뵽Redis������
	 * 
	 * @param role
	 *            ��ɫ����
	 * @return ��ɫ���󣨻����������
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@CachePut(value = "redisCacheManager", key = "'redis_role_'+#result.id")
	public Role insertRole(Role role) {
		roleDao.insertRole(role);
		return role;
	}

	/**
	 * ʹ��@CachePut����ʾ�������ݿ����ݵ�ͬʱ��Ҳ��ͬ�����»���
	 * 
	 * @param role
	 *            ��ɫ����
	 * @return Ӱ������
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@CachePut(value = "redisCacheManager", key = "'redis_role_'+#role.id")
	public int updateRole(Role role) {
		return roleDao.updateRole(role);
	}

	/**
	 * ʹ��@CacheEvictɾ�������Ӧ��key
	 * 
	 * @param id
	 *            ��ɫ���
	 * @return ����ɾ����¼��
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	@CacheEvict(value = "redisCacheManager", key = "'redis_role_'+#id")
	public int deleteRole(Long id) {
		return roleDao.deleteRole(id);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public List<Role> findRoles(String roleName, String note) {
		return roleDao.findRoles(roleName, note);
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)  
	public int insertRoles(List<Role> roleList) {
	    for (Role role : roleList) {
	        //ͬһ��ķ��������Լ������������Ե���[���룺ʧЧ]����
	        this.insertRole(role);
	    }
	    return roleList.size();
	}
}