package cn.bdqn.springboot_1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller//1
public class HelloController {
	
	@RequestMapping("/index")//2
	@ResponseBody
	public  String hello(){

		return "index";
	}



}
