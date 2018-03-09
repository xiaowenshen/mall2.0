package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by xiao on 2018/1/21.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);

}
