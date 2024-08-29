package com.prix.homepage.backend.admin;

import com.prix.homepage.backend.admin.entity.Database;
import com.prix.homepage.backend.admin.dto.UploadForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminMapper adminMapper;

    @Autowired
    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public void uploadFile(UploadForm uploadForm) {
        adminMapper.insertFile(uploadForm.getName(), uploadForm.getFile().getOriginalFilename());
    }

    public Database[] selectAllFile() {
        return adminMapper.selectAllFile();
    }
}
