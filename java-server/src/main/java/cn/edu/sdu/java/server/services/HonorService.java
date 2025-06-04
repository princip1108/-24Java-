package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HonorService {
    private final HonorRepository honorRepository;
    private final StudentRepository studentRepository;

    public HonorService(HonorRepository honorRepository, StudentRepository studentRepository) {
        this.honorRepository = honorRepository;
        this.studentRepository = studentRepository;
    }

    public OptionItemList getStudentItemOptionList(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem(s.getPersonId(), s.getPersonId()+"",
                    s.getPerson().getNum() + "-" + s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getHonorList(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String honorLevel = dataRequest.getString("honorLevel");

        List<Honor> sList = honorRepository.findByStudentAndLevel(
                personId != null ? personId : 0,
                honorLevel != null ? honorLevel : ""
        );

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Honor s : sList) {
            Map<String, Object> m = new HashMap<>();
            m.put("honorId", s.getHonorId());
            m.put("personId", s.getStudent().getPersonId() + "");
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("studentName", s.getStudent().getPerson().getName());
            m.put("honorName", s.getHonorName());
            m.put("honorLevel", s.getHonorLevel());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse honorSave(DataRequest dataRequest) {
        Integer personId = dataRequest.getInteger("personId");
        String honorName = dataRequest.getString("honorName");
        String honorLevel = dataRequest.getString("honorLevel");
        Integer honorId = dataRequest.getInteger("honorId");

        Honor s = null;
        if (honorId != null) {
            s = honorRepository.findById(honorId).orElse(null);
        }

        if (s == null) {
            s = new Honor();
            s.setStudent(studentRepository.findById(personId).orElse(null));
        }

        if (s.getStudent() == null) {
            return CommonMethod.getReturnMessageError("学生不存在");
        }

        s.setHonorName(honorName);
        s.setHonorLevel(honorLevel);
        honorRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse honorDelete(DataRequest dataRequest) {
        Integer honorId = dataRequest.getInteger("honorId");
        if (honorId != null) {
            honorRepository.findById(honorId).ifPresent(honorRepository::delete);
        }
        return CommonMethod.getReturnMessageOK();
    }
}