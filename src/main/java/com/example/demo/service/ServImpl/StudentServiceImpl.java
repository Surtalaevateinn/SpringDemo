package com.example.demo.service.ServImpl;

import com.example.demo.converter.StudentConverter;
import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentDto getStudentById(long id) {
//        Student student = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        Student student = studentRepository.findById(id).orElseThrow(() -> new IllegalStateException("id"+ id + "doesn't exist!"));
        return StudentConverter.convertStudent(student);
    }

    @Override
    public Long addNewStudent(StudentDto studentDto) {
        List<Student> studentList = studentRepository.findByEmail(studentDto.getEmail());
        if(!CollectionUtils.isEmpty(studentList)){
            throw new IllegalStateException("email"+studentDto.getEmail()+"is taken!");
        }
        Student student = studentRepository.save(StudentConverter.convertStudent(studentDto));
        return student.getId();
    }

    @Override
    public void deleteStudentById(long id) {
        studentRepository.findById(id).orElseThrow(() -> new IllegalStateException("id"+ id + "doesn't exist!"));
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public StudentDto updateStudentById(long id, String name, String email) {
        Student studentInDB = studentRepository.findById(id).orElseThrow(() -> new IllegalStateException("id"+ id + "doesn't exist!"));
        if(StringUtils.hasLength(name)&&!studentInDB.getName().equals(name)){
            studentInDB.setName(name);
        }
        if(StringUtils.hasLength(email)&&!studentInDB.getEmail().equals(email)){
            studentInDB.setEmail(email);
        }
        Student student = studentRepository.save(studentInDB);

        return StudentConverter.convertStudent(student);
    }
}
