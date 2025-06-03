package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScoreService {
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public ScoreService(ScoreRepository scoreRepository,
                        StudentRepository studentRepository,
                        CourseRepository courseRepository) {
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    // 获取学生选项列表（用于下拉框）
    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> studentList = studentRepository.findStudentListByNumName("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : studentList) {
            itemList.add(new OptionItem(
                    s.getPersonId(),
                    s.getPersonId().toString(),
                    s.getPerson().getNum() + "-" + s.getPerson().getName()
            ));
        }
        return new OptionItemList(0, itemList);
    }

    // 获取课程选项列表（用于下拉框）
    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> courseList = courseRepository.findByNameContaining("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : courseList) {
            itemList.add(new OptionItem(
                    c.getCourseId(),
                    c.getCourseId().toString(),
                    c.getCourseId() + "-" + c.getName()
            ));
        }
        return new OptionItemList(0, itemList);
    }

    // 获取成绩列表
    public DataResponse getScoreList(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");

        List<Score> scoreList;

        // 判断查询条件 - 0表示"请选择"，null或0都视为未选择
        boolean hasStudentId = studentId != null && studentId > 0;
        boolean hasCourseId = courseId != null && courseId > 0;

        if (hasStudentId && hasCourseId) {
            // 同时按学生和课程查询 - 查询特定学生在特定课程的成绩
            scoreList = scoreRepository.findByStudentIdAndCourseId(studentId, courseId);
        } else if (hasStudentId) {
            // 只按学生查询 - 显示该学生的所有成绩
            scoreList = scoreRepository.findByStudentPersonId(studentId);
        } else if (hasCourseId) {
            // 只按课程查询 - 显示该课程的所有选课学生成绩
            scoreList = scoreRepository.findByCourseCourseId(courseId);
        } else {
            // 学生和课程都未选择时显示所有数据
            scoreList = scoreRepository.findAll();
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> m;
        for (Score s : scoreList) {
            m = new HashMap<>();
            m.put("scoreId", s.getScoreId());

            // 安全地获取学生信息，避免空指针异常
            if (s.getStudent() != null) {
                m.put("studentId", s.getStudent().getPersonId());
                if (s.getStudent().getPerson() != null) {
                    m.put("studentNum", s.getStudent().getPerson().getNum());
                    m.put("studentName", s.getStudent().getPerson().getName());
                } else {
                    m.put("studentNum", "");
                    m.put("studentName", "未知学生");
                }
            } else {
                m.put("studentId", null);
                m.put("studentNum", "");
                m.put("studentName", "未知学生");
            }

            // 安全地获取课程信息，避免空指针异常
            if (s.getCourse() != null) {
                m.put("courseId", s.getCourse().getCourseId());
                m.put("courseName", s.getCourse().getName());
            } else {
                m.put("courseId", null);
                m.put("courseName", "未知课程");
            }

            m.put("scoreValue", s.getScoreValue());
            m.put("grade", s.getGrade()); // 自动计算的等级
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    // 保存成绩（新增或更新）
    public DataResponse scoreSave(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Integer courseId = dataRequest.getInteger("courseId");
        Double scoreValue = dataRequest.getDouble("scoreValue");
        Integer scoreId = dataRequest.getInteger("scoreId");

        Optional<Score> op;
        Score score = null;

        // 检查是否已存在相同学生和课程的成绩记录
        if (studentId != null && courseId != null) {
            List<Score> existingScores = scoreRepository.findByStudentIdAndCourseId(studentId, courseId);
            if (!existingScores.isEmpty() &&
                    (scoreId == null || !existingScores.get(0).getScoreId().equals(scoreId))) {
                return CommonMethod.getReturnMessageError("该学生在此课程的成绩已存在");
            }
        }

        // 查找或创建成绩记录
        if (scoreId != null) {
            op = scoreRepository.findById(scoreId);
            if (op.isPresent()) {
                score = op.get();
            }
        }

        if (score == null) {
            score = new Score();
        }

        // 设置学生和课程
        if (studentId != null) {
            Optional<Student> studentOp = studentRepository.findById(studentId);
            studentOp.ifPresent(score::setStudent);
        }

        if (courseId != null) {
            Optional<Course> courseOp = courseRepository.findById(courseId);
            courseOp.ifPresent(score::setCourse);
        }

        // 设置分数（会自动计算等级）
        score.setScoreValue(scoreValue);

        // 保存到数据库
        scoreRepository.save(score);
        return CommonMethod.getReturnMessageOK();
    }

    // 删除成绩
    public DataResponse scoreDelete(DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        if (scoreId != null) {
            Optional<Score> op = scoreRepository.findById(scoreId);
            if (op.isPresent()) {
                scoreRepository.delete(op.get());
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    // 更新成绩
    public DataResponse scoreUpdate(DataRequest dataRequest) {
        // 更新操作可以直接使用scoreSave方法
        return scoreSave(dataRequest);
    }
}