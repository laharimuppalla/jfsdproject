package com.klef.jfsd.springboot.service;

import org.springframework.stereotype.Repository;

import com.klef.jfsd.springboot.model.Student;

@Repository
public interface StudentService 
{
	public String addstudent(Student s);
	public Student checkstudentlogin(String email,String pwd);
}
