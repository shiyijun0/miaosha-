package com.ssm.chapter21.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ssm.chapter21.config.RedisConfig;
import com.ssm.chapter21.config.RootConfig;
import com.ssm.chapter21.pojo.Role;
import com.ssm.chapter21.service.RoleService;

public class Chapter21Main {

	public static void main(String[] args) {
		//ʹ��ע��Spring IoC����
		ApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class, RedisConfig.class);
		//��ȡ��ɫ������
		RoleService roleService = ctx.getBean(RoleService.class);
		Role role = new Role();
		role.setRoleName("role_name_1");
		role.setNote("role_note_1");
		//�����ɫ
		roleService.insertRole(role);
		//��ȡ��ɫ
		Role getRole = roleService.getRole(role.getId());
		getRole.setNote("role_note_1_update");
		//���½�ɫ
		roleService.updateRole(getRole);
		//ɾ����ɫ
		roleService.deleteRole(getRole.getId());
	}

}
