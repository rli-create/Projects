package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.FileMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.File;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public List<File> getAllFilesForUser(Integer userid) {
        return this.fileMapper.getUserFiles(userid);
    }

    public File getFileByName(String filename) {
        return this.fileMapper.getFileByName(filename);
    }

    public boolean isFilenameAvailable(String filename) {
        return getFileByName(filename) == null;
    }

    public Integer addFile(File file) {
        return this.fileMapper.insert(file);
    }

    public Integer deleteFile(Integer fileid) {
        return this.fileMapper.delete(fileid);
    }
}
