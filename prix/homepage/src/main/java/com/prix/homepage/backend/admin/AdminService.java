package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminMapper adminMapper;

    // UserService 생성자
    @Autowired
    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public void uploadFile(UploadForm uploadForm) {
        adminMapper.insertFile(uploadForm.getName(), uploadForm.getFile().getOriginalFilename());
    }
}
