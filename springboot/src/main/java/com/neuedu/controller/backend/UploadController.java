package com.neuedu.controller.backend;

import com.neuedu.common.ResponseCode;
import com.neuedu.common.ServerResponse;
import com.neuedu.vo.ImageVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Controller
@RequestMapping("/manage")
public class UploadController {
    //获取yml文件中的地址配置信息
    @Value("${springboot.imageHost}")
    private String imageHost;
    @GetMapping("/upload")
    public String upload(){
        return "upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ServerResponse upload(@RequestParam("uploadfile") MultipartFile uploadfile){
        if(uploadfile == null || uploadfile.getOriginalFilename().equals("")){
            return ServerResponse.serverResponseByError(ResponseCode.ERROR,"图片必须上传！");
        }
        //获取上传图片的名称
        String originalFilename = uploadfile.getOriginalFilename();
        //获取文件扩展名
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //生成新的文件名
        String newFileName = UUID.randomUUID().toString()+substring;
        File mkdir = new File("E:/upload");
        if(!mkdir.exists()){
            mkdir.mkdir();
        }

        File newFile = new File(mkdir,newFileName);

        try {
            uploadfile.transferTo(newFile);
            ImageVO imageVO = new ImageVO(newFileName,imageHost+newFileName);
            return ServerResponse.serverResponseBySuccess(imageVO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ServerResponse.serverResponseByError();
    }
}
