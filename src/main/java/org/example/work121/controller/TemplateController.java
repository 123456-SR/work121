package org.example.work121.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/template")
@CrossOrigin(origins = "*")
public class TemplateController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (files == null || files.length == 0) {
                result.put("success", false);
                result.put("message", "请选择要上传的模板文件");
                return result;
            }

            Path templateDir = Paths.get(System.getProperty("user.dir"), "表");
            Files.createDirectories(templateDir);

            List<String> savedFiles = new ArrayList<>();
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;
                String original = f.getOriginalFilename();
                String fileName = sanitizeFileName(original);
                if (fileName.isEmpty()) continue;

                String ext = "";
                int idx = fileName.lastIndexOf('.');
                if (idx >= 0 && idx < fileName.length() - 1) {
                    ext = fileName.substring(idx + 1).toLowerCase(Locale.ROOT).trim();
                }
                if (!isAllowedTemplateExt(ext)) {
                    result.put("success", false);
                    result.put("message", "不支持的模板格式: " + fileName);
                    return result;
                }

                Path target = templateDir.resolve(fileName);
                Files.copy(f.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                savedFiles.add(fileName);
            }

            if (savedFiles.isEmpty()) {
                result.put("success", false);
                result.put("message", "未上传有效文件");
                return result;
            }

            result.put("success", true);
            result.put("message", "导入成功");
            result.put("dir", templateDir.toString());
            result.put("files", savedFiles);
            return result;
        } catch (Exception e) {
            logger.error("导入模板失败", e);
            result.put("success", false);
            result.put("message", "导入模板失败: " + e.getMessage());
            return result;
        }
    }

    private boolean isAllowedTemplateExt(String ext) {
        if (ext == null) return false;
        String t = ext.trim().toLowerCase(Locale.ROOT);
        return "xls".equals(t) || "xlsx".equals(t) || "docx".equals(t);
    }

    private String sanitizeFileName(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.isEmpty()) return "";
        t = t.replace("\\", "/");
        int idx = t.lastIndexOf('/');
        if (idx >= 0 && idx < t.length() - 1) t = t.substring(idx + 1);
        return t.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}

