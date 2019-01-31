package cn.bdqn.springboot_1.controller;

import cn.bdqn.springboot_1.entity.Msg;
import cn.bdqn.springboot_1.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller//1
public class HelloController {
	
	@RequestMapping("/index")//2
	@ResponseBody
	public  String hello(){

		return "index";
	}

	@Autowired
	DemoService demoService;


	@RequestMapping("/rollback11")
	@ResponseBody
	public String rollback1(){ //1
		System.out.println("***");
		return "的广泛覆盖ffff1";
	}

	@RequestMapping("/")
	public String index(Model model){
		Msg msg =  new Msg("测试标题","测试内容","额外信息，只对管理员显示");
		model.addAttribute("msg", msg);
		return "home";
	}
}
