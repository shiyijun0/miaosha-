package cn.bdqn.mapper;


import cn.bdqn.pojo.Role;

import java.util.List;
public interface RoleMapper {
	public int insertRole(Role role);
	public int deleteRole(Long id);
	public int updateRole(Role role);
	public Role getRole(Long id);
	public List<Role> findRoles(String roleName);
}