package com.klef.jfsd.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.klef.jfsd.springboot.model.Student;
import com.klef.jfsd.springboot.repository.StudentRepository;

@Service
public class StudentServiceImpl implements StudentService
{

	@Autowired
	private StudentRepository studentRepository;
	@Override
	public String addstudent(Student s) 
	{
		studentRepository.save(s);
		return "Student Added Successfully";
	}
	@Override
	public Student checkstudentlogin(String email, String pwd)
	{
		
		return studentRepository.checkstudentlogin(email, pwd);
	}

}
