package com.teach.javafx.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * CommonMethod 修复验证测试
 * 验证getDouble方法是否正确解析字符串
 */
class CommonMethodFixTest {

    @Test
    void testGetDouble_修复验证_字符串转换() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "85.5");
        
        Double result = CommonMethod.getDouble(data, "score");
        assertEquals(85.5, result, "应该正确解析字符串85.5为Double类型");
    }

    @Test
    void testGetDouble_修复验证_整数字符串() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "90");
        
        Double result = CommonMethod.getDouble(data, "score");
        assertEquals(90.0, result, "应该正确解析字符串90为Double类型");
    }

    @Test
    void testGetDouble_修复验证_小数字符串() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "92.8");
        
        Double result = CommonMethod.getDouble(data, "score");
        assertEquals(92.8, result, "应该正确解析字符串92.8为Double类型");
    }

    @Test
    void testGetDouble_修复验证_零分() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "0");
        
        Double result = CommonMethod.getDouble(data, "score");
        assertEquals(0.0, result, "应该正确解析字符串0为Double类型");
    }

    @Test
    void testGetDouble_修复验证_满分() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "100");
        
        Double result = CommonMethod.getDouble(data, "score");
        assertEquals(100.0, result, "应该正确解析字符串100为Double类型");
    }

    @Test
    void testGetDouble_Double对象直接返回() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", 95.5);
        
        Double result = CommonMethod.getDouble(data, "score");
        assertEquals(95.5, result, "Double对象应该直接返回");
    }

    @Test
    void testGetDouble_空值返回null() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", null);
        
        Double result = CommonMethod.getDouble(data, "score");
        assertNull(result, "空值应该返回null");
    }

    @Test
    void testGetDouble_不存在的键返回null() {
        Map<String, Object> data = new HashMap<>();
        
        Double result = CommonMethod.getDouble(data, "nonexistent");
        assertNull(result, "不存在的键应该返回null");
    }

    @Test
    void testGetDouble_无效字符串返回null() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "invalid");
        
        Double result = CommonMethod.getDouble(data, "score");
        assertNull(result, "无效字符串应该返回null");
    }

    @Test
    void testGetDouble0_修复验证_字符串转换() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "75.5");
        
        Double result = CommonMethod.getDouble0(data, "score");
        assertEquals(75.5, result, "getDouble0应该正确解析字符串75.5为Double类型");
    }

    @Test
    void testGetDouble0_无效字符串返回0() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", "invalid");
        
        Double result = CommonMethod.getDouble0(data, "score");
        assertEquals(0.0, result, "getDouble0遇到无效字符串应该返回0");
    }

    @Test
    void testGetDouble0_空值返回0() {
        Map<String, Object> data = new HashMap<>();
        data.put("score", null);
        
        Double result = CommonMethod.getDouble0(data, "score");
        assertEquals(0.0, result, "getDouble0遇到空值应该返回0");
    }

    @Test
    void testScoreEditScenario_模拟成绩编辑场景() {
        // 模拟ScoreEditController中的数据传递场景
        Map<String, Object> editData = new HashMap<>();
        editData.put("scoreId", 1);
        editData.put("studentId", 2);
        editData.put("courseId", 3);
        editData.put("scoreValue", "88.5"); // 前端传递的字符串
        
        // 模拟ScoreTableController中的数据处理
        Integer scoreId = CommonMethod.getInteger(editData, "scoreId");
        Integer studentId = CommonMethod.getInteger(editData, "studentId");
        Integer courseId = CommonMethod.getInteger(editData, "courseId");
        Double scoreValue = CommonMethod.getDouble(editData, "scoreValue");
        
        assertEquals(1, scoreId);
        assertEquals(2, studentId);
        assertEquals(3, courseId);
        assertEquals(88.5, scoreValue, "成绩值应该正确解析为88.5而不是0");
    }

    @Test
    void testScoreAddScenario_模拟成绩添加场景() {
        // 模拟添加新成绩的场景
        Map<String, Object> addData = new HashMap<>();
        addData.put("studentId", 5);
        addData.put("courseId", 7);
        addData.put("scoreValue", "92"); // 前端传递的字符串
        
        // 模拟后端处理
        Integer studentId = CommonMethod.getInteger(addData, "studentId");
        Integer courseId = CommonMethod.getInteger(addData, "courseId");
        Double scoreValue = CommonMethod.getDouble(addData, "scoreValue");
        
        assertEquals(5, studentId);
        assertEquals(7, courseId);
        assertEquals(92.0, scoreValue, "新增成绩值应该正确解析为92.0而不是0");
    }

    @Test
    void testEdgeValues_边界值测试() {
        Map<String, Object> data = new HashMap<>();
        
        // 测试边界分数
        data.put("score", "59.9");
        assertEquals(59.9, CommonMethod.getDouble(data, "score"));
        
        data.put("score", "60.0");
        assertEquals(60.0, CommonMethod.getDouble(data, "score"));
        
        data.put("score", "89.9");
        assertEquals(89.9, CommonMethod.getDouble(data, "score"));
        
        data.put("score", "90.0");
        assertEquals(90.0, CommonMethod.getDouble(data, "score"));
    }
}
