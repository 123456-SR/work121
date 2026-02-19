package org.example.work121.service;

import org.example.work121.entity.JcCoreWtInfo;

public interface JcCoreWtInfoService {

    JcCoreWtInfo getByWtNum(String wtNum);

    /**
     * 根据登记人姓名查询委托信息列表
     * @param names 登记人姓名列表
     * @param wtNum 统一编号（模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 委托信息分页列表
     */
    com.github.pagehelper.PageInfo<JcCoreWtInfo> getByRegName(java.util.List<String> names, String wtNum, int pageNum, int pageSize);

    /**
     * 根据ID查询委托信息
     * @param id 委托ID
     * @return 委托信息
     */
    JcCoreWtInfo getById(String id);

    /**
     * 根据检测类别和登记人查询委托信息列表
     * @param categories 检测类别列表
     * @param names 登记人姓名列表
     * @param wtNum 统一编号（模糊查询）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 委托信息分页列表
     */
    com.github.pagehelper.PageInfo<JcCoreWtInfo> getByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum, int pageNum, int pageSize);

    java.util.List<JcCoreWtInfo> selectByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum);

    /**
     * 仅按记录表三种角色（填写人、记录检验人、记录审核人）过滤的检测记录列表
     */
    com.github.pagehelper.PageInfo<JcCoreWtInfo> getRecordsByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum, int pageNum, int pageSize);

    java.util.List<JcCoreWtInfo> selectRecordsByCategory(java.util.List<String> categories, java.util.List<String> names, String wtNum);

    java.util.List<JcCoreWtInfo> selectByRegName(java.util.List<String> names, String wtNum);
    
    java.util.List<JcCoreWtInfo> debugSelectAll();

    /**
     * 保存委托信息
     * @param info 委托信息
     * @return 是否成功
     */
    boolean save(JcCoreWtInfo info);

    /**
     * 删除委托信息
     * @param id 委托ID
     * @return 是否成功
     */
    boolean delete(String id);
}
