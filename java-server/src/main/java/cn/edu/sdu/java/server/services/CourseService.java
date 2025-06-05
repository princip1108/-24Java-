package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {
    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }


    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> courseList = courseRepository.findAll();
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : courseList) {
            itemList.add(new OptionItem(
                    c.getCourseId(),
                    c.getCourseId().toString(),
                    c.getCourseCode() + "-" + c.getName()
            ));
        }
        return new OptionItemList(0, itemList);
    }


    public DataResponse getCourseList(DataRequest dataRequest) {
        String courseCode = dataRequest.getString("courseCode");
        String name = dataRequest.getString("name");
        String type = dataRequest.getString("type");
        List<Course> courseList = courseRepository.findByCriteria(courseCode, name, type);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Course c : courseList) {
            m = new HashMap<>();
            m.put("courseId", c.getCourseId());
            m.put("courseCode", c.getCourseCode());
            m.put("name", c.getName());
            m.put("credit", c.getCredit());
            m.put("type", c.getType());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse courseSave(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        String courseCode = dataRequest.getString("courseCode");
        String name = dataRequest.getString("name");
        Double credit = dataRequest.getDouble("credit");
        String type = dataRequest.getString("type");
        if (courseId == null) {
            Course existing = courseRepository.findByCourseCode(courseCode);
            if (existing != null) {
                return CommonMethod.getReturnMessageError("课序号已存在：" + courseCode);
            }
        }

        Course course;
        if (courseId != null) {
            Optional<Course> op = courseRepository.findById(courseId);
            if (op.isPresent()) {
                course = op.get();
            } else {
                return CommonMethod.getReturnMessageError("课程不存在：" + courseId);
            }
        } else {
            course = new Course();
        }
        course.setCourseCode(courseCode);
        course.setName(name);
        course.setCredit(credit);
        course.setType(type);
        courseRepository.save(course);
        return CommonMethod.getReturnMessageOK();
    }
    public DataResponse courseDelete(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId != null) {
            try {
                courseRepository.deleteById(courseId);
                return CommonMethod.getReturnMessageOK();
            } catch (Exception e) {
                return CommonMethod.getReturnMessageError("删除失败：" + e.getMessage());
            }
        }
        return CommonMethod.getReturnMessageError("未提供课程ID");
    }
}