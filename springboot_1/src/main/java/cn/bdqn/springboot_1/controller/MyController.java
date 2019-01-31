package cn.bdqn.springboot_1.controller;

import cn.bdqn.springboot_1.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class MyController {
	@Autowired
	DemoService demoService;


	@RequestMapping("/rollback1")
	public String rollback1(){ //1
		System.out.println("***");
		return "的广泛覆盖ffff1";
	}


}
