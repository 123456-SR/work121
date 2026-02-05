package org.example.work121.service.impl;

import org.example.work121.entity.SimpleDirectory;
import org.example.work121.mapper.SimpleDirectoryMapper;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 极简目录表服务实现类
 */
@Service
public class SimpleDirectoryServiceImpl implements SimpleDirectoryService {

    @Autowired
    private SimpleDirectoryMapper simpleDirectoryMapper;

    @Override
    public boolean saveDirectory(SimpleDirectory directory) {
        try {
            // 检查是否已存在该目录
            SimpleDirectory existingRecord = null;
            if (directory.getDirId() != null) {
                existingRecord = simpleDirectoryMapper.selectByDirId(directory.getDirId());
            }

            if (existingRecord != null) {
                // 更新现有记录
                directory.setId(existingRecord.getId()); // Ensure ID is set for update
                // 保留创建信息
                directory.setCreateBy(existingRecord.getCreateBy());
                directory.setCreateTime(existingRecord.getCreateTime());
                
                // 设置更新信息
                directory.setUpdateBy(directory.getUpdateBy() != null ? directory.getUpdateBy() : "admin"); // Or get from context
                directory.setUpdateTime(new java.util.Date());
                
                int result = simpleDirectoryMapper.update(directory);
                return result > 0;
            } else {
                // 插入新记录
                if (directory.getId() == null || directory.getId().isEmpty()) {
                    directory.setId(UUID.randomUUID().toString());
                }
                if (directory.getDirId() == null || directory.getDirId().isEmpty()) {
                    directory.setDirId(UUID.randomUUID().toString());
                }
                
                // 设置创建信息
                if (directory.getCreateBy() == null) {
                    directory.setCreateBy("admin");
                }
                if (directory.getCreateTime() == null) {
                    directory.setCreateTime(new java.util.Date());
                }
                if (directory.getStatus() == null) {
                    directory.setStatus(1);
                }

                int result = simpleDirectoryMapper.insert(directory);
                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SimpleDirectory getDirectoryById(String id) {
        try {
            return simpleDirectoryMapper.selectById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public SimpleDirectory getDirectoryByDirId(String dirId) {
        try {
            return simpleDirectoryMapper.selectByDirId(dirId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<SimpleDirectory> getAllDirectories() {
        try {
            return simpleDirectoryMapper.selectAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean deleteDirectory(String id) {
        try {
            int result = simpleDirectoryMapper.deleteById(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
