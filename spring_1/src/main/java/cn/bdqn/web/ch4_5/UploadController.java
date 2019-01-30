package cn.bdqn.web.ch4_5;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class UploadController {
	
	@RequestMapping(value = "/upload",method = RequestMethod.POST)
	public @ResponseBody String upload(MultipartFile file) {//1
		/*File file1=new File("F:\\zhihui\\github\\miaosha-\\spring_1\\src\\main\\resources\\upload");
		if(!file1.exists()){
			//file1.createNewFile()
			file1.mkdir();
		}*/
			try {
				FileUtils.writeByteArrayToFile(new File("F:\\zhihui\\github\\miaosha-\\spring_1\\src\\main\\resources\\upload\\"+file.getOriginalFilename()),
						file.getBytes()); //2
				return "ok";
			} catch (IOException e) {
				e.printStackTrace();
				return "wrong";
			}
			
		
	}

}
