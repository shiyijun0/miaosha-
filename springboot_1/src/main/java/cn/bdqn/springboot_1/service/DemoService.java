package cn.bdqn.springboot_1.service;


import cn.bdqn.springboot_1.entity.Person;

public interface DemoService {
	public Person save(Person person);
	
	public void remove(Long id);
	
	public Person findOne(Person person);

}
