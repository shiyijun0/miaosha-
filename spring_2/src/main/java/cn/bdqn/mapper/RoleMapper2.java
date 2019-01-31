package cn.bdqn.mapper;

import cn.bdqn.pojo.Role;
import org.apache.ibatis.annotations.Select;



public interface RoleMapper2 {
	
	@Select("select id, role_name as roleName, note from t_role where id=#{id}")
	public Role getRole(Long id);
}
