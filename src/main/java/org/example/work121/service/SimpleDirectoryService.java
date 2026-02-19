package org.example.work121.service;

import org.example.work121.entity.SimpleDirectory;
import java.util.List;

/**
 * 极简目录表服务接口
 */
public interface SimpleDirectoryService {

    /**
     * 保存目录
     * @param directory 目录实体
     * @return 是否保存成功
     */
    boolean saveDirectory(SimpleDirectory directory);

    /**
     * 根据ID获取目录
     * @param id 目录ID
     * @return 目录实体
     */
    SimpleDirectory getDirectoryById(String id);

    /**
     * 根据目录ID获取目录
     * @param dirId 目录唯一标识
     * @return 目录实体
     */
    SimpleDirectory getDirectoryByDirId(String dirId);

    /**
     * 获取所有目录列表
     * @return 目录列表
     */
    List<SimpleDirectory> getAllDirectories();

    /**
     * 删除目录
     * @param id 目录ID
     * @return 是否删除成功
     */
    boolean deleteDirectory(String id);

    /**
     * 根据目录名称（统一编号）获取目录
     * @param dirName 目录名称
     * @return 目录实体
     */
    SimpleDirectory getDirectoryByDirName(String dirName);

    /**
     * 根据统一编号同步委托单数据到其他表单
     * @param wtNum 统一编号
     */
    void syncEntrustmentDataByWtNum(String wtNum);
}
