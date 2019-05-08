package com.pinyougou.manager.controller;

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.shop.controller *
 * @since 1.0
 */
@RestController
public class UploadController {

    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file){
        try {
            //1.获取字节
            byte[] bytes = file.getBytes();
            //2.获取原文件的扩展名
            String originalFilename = file.getOriginalFilename();//1234.jpg
           String extName= originalFilename.substring(originalFilename.lastIndexOf(".")+1);

            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs_client.conf");
            String path = fastDFSClient.uploadFile(bytes,extName);//    group1/M00/00/05/wKgZhVxqMiSAMgI0AANdC6JX9KA148.jpg

            //拼接path
            String realpath="http://192.168.25.133/"+path;

            return new Result(true,realpath);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
