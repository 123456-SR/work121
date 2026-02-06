package org.example.work121.controller;

import org.example.work121.entity.*;
import org.example.work121.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/form-data")
public class FormDataController {

    @Autowired
    private EntrustmentMapper entrustmentMapper;

    @Autowired
    private ReboundMethodRecordMapper reboundMethodRecordMapper;

    @Autowired
    private LightDynamicPenetrationRecordMapper lightDynamicPenetrationRecordMapper;

    @Autowired
    private NuclearDensityRecordMapper nuclearDensityRecordMapper;

    @Autowired
    private SandReplacementRecordMapper sandReplacementRecordMapper;

    @Autowired
    private WaterReplacementRecordMapper waterReplacementRecordMapper;

    @Autowired
    private CuttingRingRecordMapper cuttingRingRecordMapper;

    @Autowired
    private BeckmanBeamRecordMapper beckmanBeamRecordMapper;

    /**
     * 根据表单类型和ID获取对应数据表的数据
     * @param formType 表单类型
     * @param id 数据ID
     * @return 对应的数据
     */
    @GetMapping("/get-by-type-and-id")
    public Map<String, Object> getFormDataByTypeAndId(
            @RequestParam("formType") String formType,
            @RequestParam("id") String id) {

        Map<String, Object> result = new HashMap<>();

        try {
            Object data = null;

            // 根据表单类型查询对应的数据表
            switch (formType) {
                case "ENTRUSTMENT_LIST":
                    data = entrustmentMapper.selectById(id);
                    break;
                case "REBOUND_METHOD_RECORD":
                    data = reboundMethodRecordMapper.selectById(id);
                    break;
                case "LIGHT_DYNAMIC_PENETRATION_RECORD":
                    data = lightDynamicPenetrationRecordMapper.selectById(id);
                    break;
                case "NUCLEAR_DENSITY_RECORD":
                    data = nuclearDensityRecordMapper.selectById(id);
                    break;
                case "SAND_REPLACEMENT_RECORD":
                    data = sandReplacementRecordMapper.selectById(id);
                    break;
                case "WATER_REPLACEMENT_RECORD":
                    data = waterReplacementRecordMapper.selectById(id);
                    break;
                case "CUTTING_RING_RECORD":
                    data = cuttingRingRecordMapper.selectById(id);
                    break;
                case "BECKMAN_BEAM_RECORD":
                    data = beckmanBeamRecordMapper.selectById(id);
                    break;
                default:
                    result.put("success", false);
                    result.put("message", "不支持的表单类型");
                    return result;
            }

            if (data != null) {
                result.put("success", true);
                result.put("data", data);
            } else {
                result.put("success", false);
                result.put("message", "未找到对应数据");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询数据失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
