package org.example.work121.controller;

import org.example.work121.entity.SampleCirculation;
import org.example.work121.service.SampleCirculationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/sample-circulation")
public class SampleCirculationController {

    @Resource
    private SampleCirculationService sampleCirculationService;

    @PostMapping("/save")
    public Result save(@RequestBody SampleCirculation sampleCirculation) {
        try {
            SampleCirculation saved = sampleCirculationService.save(sampleCirculation);
            return Result.success(saved);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/get/{id}")
    public Result getById(@PathVariable String id) {
        try {
            SampleCirculation sampleCirculation = sampleCirculationService.getById(id);
            return Result.success(sampleCirculation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/by-sample-number")
    public Result getBySampleNumber(@RequestParam String sampleNumber) {
        try {
            SampleCirculation sampleCirculation = sampleCirculationService.getBySampleNumber(sampleNumber);
            return Result.success(sampleCirculation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result getAll() {
        try {
            List<SampleCirculation> list = sampleCirculationService.getAll();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        try {
            List<SampleCirculation> list = sampleCirculationService.getByKeyword(keyword);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable String id) {
        try {
            sampleCirculationService.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 统一返回结果类
    static class Result {
        private boolean success;
        private String message;
        private Object data;

        private Result(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static Result success() {
            return new Result(true, "操作成功", null);
        }

        public static Result success(Object data) {
            return new Result(true, "操作成功", data);
        }

        public static Result error(String message) {
            return new Result(false, message, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}