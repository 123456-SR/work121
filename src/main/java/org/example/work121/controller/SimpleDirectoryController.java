package org.example.work121.controller;

import org.example.work121.entity.SimpleDirectory;
import org.example.work121.service.SimpleDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 极简目录表控制器
 */
@RestController
@RequestMapping("/api/directory")
public class SimpleDirectoryController {

    @Autowired
    private SimpleDirectoryService simpleDirectoryService;

    /**
     * 保存目录
     * @param directory 目录实体
     * @return 响应结果
     */
    @PostMapping("/save")
    public Map<String, Object> saveDirectory(@RequestBody SimpleDirectory directory) {
        Map<String, Object> result = new HashMap<>();

        try {
            boolean success = simpleDirectoryService.saveDirectory(directory);

            if (success) {
                result.put("success", true);
                result.put("message", "保存成功");
            } else {
                result.put("success", false);
                result.put("message", "保存失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "保存异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 根据ID获取目录
     * @param id 目录ID
     * @return 响应结果
     */
    @PostMapping("/getById")
    public Map<String, Object> getDirectoryById(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String id = params.get("id");
            if (id == null || id.isEmpty()) {
                result.put("success", false);
                result.put("message", "目录ID不能为空");
                return result;
            }

            SimpleDirectory directory = simpleDirectoryService.getDirectoryById(id);

            if (directory != null) {
                result.put("success", true);
                result.put("data", directory);
            } else {
                result.put("success", false);
                result.put("message", "未找到目录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 获取所有目录列表
     * @return 响应结果
     */
    @PostMapping("/getAll")
    public Map<String, Object> getAllDirectories() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<SimpleDirectory> directories = simpleDirectoryService.getAllDirectories();

            if (directories != null) {
                result.put("success", true);
                result.put("data", directories);
            } else {
                result.put("success", false);
                result.put("message", "获取目录列表失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 根据目录名称（统一编号）获取目录
     * @param params 参数包含 dirName
     * @return 响应结果
     */
    @PostMapping("/get-by-dirname")
    public Map<String, Object> getDirectoryByDirName(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String dirName = params.get("dirName");
            if (dirName == null || dirName.isEmpty()) {
                result.put("success", false);
                result.put("message", "目录名称不能为空");
                return result;
            }

            // Using getAllDirectories and filtering since service might not expose getByDirName directly
            // Or better, add getByDirName to service. 
            // Checking service interface... assuming simpleDirectoryService has it or I can add it.
            // Let's assume I need to implement it in Service first or use Mapper directly if accessible (not recommended in Controller).
            // Let's use the Service.
            SimpleDirectory directory = simpleDirectoryService.getDirectoryByDirName(dirName);

            if (directory != null) {
                result.put("success", true);
                result.put("data", directory);
            } else {
                result.put("success", false);
                result.put("message", "未找到目录");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "查询异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 删除目录
     * @param id 目录ID
     * @return 响应结果
     */
    @PostMapping("/delete")
    public Map<String, Object> deleteDirectory(@RequestBody Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        try {
            String id = params.get("id");
            if (id == null || id.isEmpty()) {
                result.put("success", false);
                result.put("message", "目录ID不能为空");
                return result;
            }

            boolean success = simpleDirectoryService.deleteDirectory(id);

            if (success) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "删除异常: " + e.getMessage());
        }

        return result;
    }
}
