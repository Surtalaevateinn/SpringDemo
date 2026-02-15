package com.example.demo.service.ServImpl;

import com.example.demo.converter.StudentConverter;
import com.example.demo.dto.StudentDto;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.StudentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import org.springframework.util.StringUtils;

import java.util.List;

import java.util.concurrent.TimeUnit;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired

    private StudentRepository studentRepository;

// 1. Inject Redis operation tools

    @Autowired

    private StringRedisTemplate redisTemplate;

// 2. Inject JSON conversion tools (Spring Boot integrates Jackson by default)

    @Autowired

    private ObjectMapper objectMapper;

// Define the prefix of the cache key for easy management

    private static final String CACHE_KEY_PREFIX = "student:";

    @Override

    public StudentDto getStudentById(long id) {

        String key = CACHE_KEY_PREFIX + id;

// --- Step A: First, check Redis ---

        String json = redisTemplate.opsForValue().get(key);

        if (StringUtils.hasLength(json)) {

            try {

// Cache Hit: Directly deserialize and return, without querying the database

                System.out.println("🔥 Cache Hit! ID: " + id + " Read from Redis");

                return objectMapper.readValue(json, StudentDto.class);

            } catch (JsonProcessingException e) {

                e.printStackTrace();

            }
        }

// --- Step B: Cache Miss, query the database ---

        System.out.println("🐢 Cache Miss! ID: " + id + " Read from MySQL");

        Student student = studentRepository.findById(id)

                .orElseThrow(() -> new IllegalStateException("id " + id + " doesn't exist!"));

// Convert to DTO

        StudentDto studentDto = StudentConverter.convertStudent(student);

// --- Step C: Backfill Redis ---

        try {

            String newJson = objectMapper.writeValueAsString(studentDto);

// Write to Redis and set an expiration time of 1 hour (to prevent dirty data from persisting)

            redisTemplate.opsForValue().set(key, newJson, 1, TimeUnit.HOURS);

        } catch (JsonProcessingException e) {

            e.printStackTrace();

        }

        return studentDto;

    }

    @Override

    public Long addNewStudent(StudentDto studentDto) {

        List<Student> studentList = studentRepository.findByEmail(studentDto.getEmail()); if(!CollectionUtils.isEmpty(studentList)){
            throw new IllegalStateException("email "+studentDto.getEmail()+" is taken!");

        }
        Student student = studentRepository.save(StudentConverter.convertStudent(studentDto));

// Adding data usually doesn't require caching because the corresponding ID key isn't cached yet

        return student.getId();

    }

    @Override

    public void deleteStudentById(long id) {

// 1. First, check if it exists (your original logic)

        studentRepository.findById(id).orElseThrow(() -> new IllegalStateException("id "+ id + " doesn't exist!"));

// 2. Operate on the database

        studentRepository.deleteById(id);

// 3. [Crucial] Delete cache (Cache Invalidation)

// To prevent data from being deleted from the database but still remaining in the cache, causing users to find non-existent data:

        String key = CACHE_KEY_PREFIX + id;

        redisTemplate.delete(key);

        System.out.println("🗑️ Cache Deleted! ID: " + id);

    }

    @Override

    @Transactional

    public StudentDto updateStudentById(long id, String name, String email) {

// 1. Existing business logic

        Student studentInDB = studentRepository.findById(id).orElseThrow(() -> new IllegalStateException("id "+ id + " doesn't exist!"));

        if(StringUtils.hasLength(name) && !studentInDB.getName().equals(name)){

            studentInDB.setName(name);

        }
        if(StringUtils.hasLength(email) && !studentInDB.getEmail().equals(email)){

            studentInDB.setEmail(email);

        }
        Student student = studentRepository.save(studentInDB);

// 2. [Key] Delete Cache

// We choose to delete directly instead of updating the cache. Because "deleting" is simpler and less prone to errors.

// The next query will trigger a Cache Miss, automatically loading the latest data.

        String key = CACHE_KEY_PREFIX + id;

        redisTemplate.delete(key);

        System.out.println("🔄 Cache Evicted (Update)! ID: " + id);

        return StudentConverter.convertStudent(student);

    }
}