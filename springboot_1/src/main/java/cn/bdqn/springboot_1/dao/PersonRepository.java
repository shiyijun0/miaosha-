package cn.bdqn.springboot_1.dao;

import cn.bdqn.springboot_1.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;



public interface PersonRepository extends JpaRepository<Person, Long> {
	

}
