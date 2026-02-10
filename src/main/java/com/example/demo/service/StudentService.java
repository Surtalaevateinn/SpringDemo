package com.example.demo.service;

import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Student;

public interface StudentService {
    StudentDto getStudentById(long id);

    Long addNewStudent(StudentDto studentDto);
}
