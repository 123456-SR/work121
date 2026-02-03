package org.example.work121.service;

import org.example.work121.entity.JcCoreWtInfo;

public interface JcCoreWtInfoService {

    JcCoreWtInfo getByWtNum(String wtNum);

    /**
     * 根据登记人姓名查询委托信息列表
     * @param regName 登记人姓名
     * @return 委托信息列表
     */
    java.util.List<JcCoreWtInfo> getByRegName(String regName);

    /**
     * 根据ID查询委托信息
     * @param id 委托ID
     * @return 委托信息
     */
    JcCoreWtInfo getById(String id);

}
