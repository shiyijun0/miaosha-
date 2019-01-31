package cn.bdqn.springboot_1.dao;

import cn.bdqn.springboot_1.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;



public interface SysUserRepository extends JpaRepository<SysUser, Long>{
	
	SysUser findByUsername(String username);

}
