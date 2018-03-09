package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by xiao on 2018/1/21.
 */
@Service("iFileService")
public class FileServiceImpl implements IFileService {

    //日志记录
    private Logger logger  = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf('.')+1);
        //使用UUID防止文件名重复，覆盖别人的文件
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件，上传的文件名：{}，上传的路径：{}，新文件名：{} ",fileName,path,uploadFileName);
        //新建文件
        File fileDir = new File(path);
        //判断文件是否存在，不存在就创建一个新的
        if(!fileDir.exists()){
            //使文件可以改，因为Tomcat发布服务后，文件的权限不一定是可以改的
            fileDir.setWritable(true);
            //使用dirs是为了解决上传的路径中，如果有文件夹的没有创建，其会自动创建文件夹
            fileDir.mkdirs();
        }
        File targetFile = new File(path,uploadFileName);
        try {
            file.transferTo(targetFile);
            //到此为止，文件已经上传服务器成功

            //下一步是把文件上传到FTP服务器,与FTP文件服务器对接
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //已将文件上传FTP

            //上传完之后，删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }

        return  targetFile.getName();

    }

}
