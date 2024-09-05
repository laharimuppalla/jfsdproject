  package com.klef.jfsd.springboot.controller;


import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.klef.jfsd.springboot.model.Admin;
import com.klef.jfsd.springboot.model.Course;
import com.klef.jfsd.springboot.model.Faculty;
import com.klef.jfsd.springboot.model.FacultyCourseMapping;
import com.klef.jfsd.springboot.model.Student;
import com.klef.jfsd.springboot.service.AdminService;
import com.klef.jfsd.springboot.service.EmailService;
import com.klef.jfsd.springboot.service.StudentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ClientController 
{
	@Autowired
	private StudentService studentService;
	@Autowired
	private AdminService adminService;
	 @Autowired
	 private EmailService emailService;


	@GetMapping("/")
	public String index()
	{
		
		return "index";
	}
	
	@GetMapping("studentreg")
	public ModelAndView studentreg()
	{
		ModelAndView mv = new ModelAndView();
		mv.addObject("studentreg");
		return mv;
	}
	
	@GetMapping("insertstudent")
	public ModelAndView insertstudent(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView();
		
		String msg = null;
		try
		{
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String branch = request.getParameter("branch");
			String yr = request.getParameter("year");
			int year = Integer.parseInt(yr);
			String sem = request.getParameter("semester");
			int semester = Integer.parseInt(sem);
			String password = request.getParameter("password");
			boolean active = true;
			String contact = request.getParameter("contactno");
			
			Student s = new Student();
			
			s.setName(name);
			s.setEmail(email);
			s.setBranch(branch);
			s.setYear(year);
			s.setSemester(semester);
			s.setPassword(password);
			s.setActive(active);
			s.setContact(contact);
			
			msg = studentService.addstudent(s);
			
			
			emailService.sendEmail(s.getEmail(),"Confirmation mail","Hi ,you are registerted !");
			mv.setViewName("displaymsg");
			mv.addObject("message",msg);
		}
		catch (Exception e) 
 		 {
			msg = e.getMessage();
			mv.setViewName("displayerror");
			mv.addObject("message", msg);
		 }
		return mv;
	}
	
	
	@GetMapping("studentlogin") 
    public ModelAndView studentlogin()
    {
      ModelAndView mv = new ModelAndView();
      mv.setViewName("studentlogin");
      return mv;
    }
	
	@PostMapping("checkstudentlogin")
	public ModelAndView checkstudentlogin(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView();
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		
		Student s = studentService.checkstudentlogin(email, password);
		 if(s!=null)
         {
           //session
        	HttpSession session = request.getSession();
        	
        	session.setAttribute("sid", s.getId()); //sid is a session variable
        	session.setAttribute("ename", s.getName()); // sname is a session variable
        	 
           //session
           mv.setViewName("studenthome");
           List<Course> clist = adminService.viewallcourses();
       	mv.addObject("coursedata",clist);
           
         }
         else
         {
           mv.setViewName("studentlogin");
           mv.addObject("message", "Login Failed");
         }
         
         return mv;
	}
	
	@GetMapping("studentnavbar")
	public String studentnavbar()
	{
		
		return "studentnavbar";
	}
	
	@GetMapping("studenthome")
	public ModelAndView studenthome()
	{
		ModelAndView mv = new ModelAndView();
		 mv.setViewName("studenthome");
         List<Course> clist = adminService.viewallcourses();
     	mv.addObject("coursedata",clist);
     	return mv;
	}
	
	
	@GetMapping("adminhome")
	public String adminhome()
	{
		
		return "adminhome";
	}
	
	@GetMapping("adminlogin") 
    public ModelAndView adminlogin()
    {
      ModelAndView mv = new ModelAndView();
      mv.setViewName("adminlogin");
      return mv;
    }
	
	
	 
    @PostMapping("checkadminlogin")
    public ModelAndView checkadminlogin(HttpServletRequest request)
    {
   	 ModelAndView mv = new ModelAndView();
   	 
   	 
   	 String uname = request.getParameter("email");
   	 String pwd  = request.getParameter("password");
   	 
   	 Admin a = adminService.checkadminlogin(uname, pwd);
   	 if(a!=null)
   	 {
   		 mv.setViewName("adminhome");
   	 }
   	 else
   	 {
   		 mv.setViewName("adminlogin");
   		 mv.addObject("message", "Login Failed");
   	 }
   	 return mv;
    }
    
    @GetMapping("viewallstudentsaction")
    public ModelAndView viewallstudents()
    {
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("viewallstudents");
    	
    	List<Student> slist = adminService.viewallstudents();
    	mv.addObject("studentdata",slist);
    
    	return mv;
    }
    
	@GetMapping("viewstudentbyid")
	public ModelAndView viewstudentbyid(@RequestParam("id") int id)
	{
		ModelAndView mv = new ModelAndView();
		Student s = adminService.viewstudentbyid(id);
		mv.setViewName("viewstudentbyid");
		mv.addObject("student", s);
		return mv;
	}
	
	@GetMapping("addcourse")
	public ModelAndView addcourse()
	{
		ModelAndView mv = new ModelAndView();
		mv.addObject("addcourse");
		return mv;
	}
	
	
	@PostMapping("insertcourse")
	public ModelAndView insertcourse(HttpServletRequest request,@RequestParam("courseimage") MultipartFile file) throws IOException, SerialException, SQLException
	{
		ModelAndView mv = new ModelAndView();
		
		String msg = null;
		try
		{
			String yr = request.getParameter("year");
			int year = Integer.parseInt(yr);
			String sem = request.getParameter("semester");
			int semester = Integer.parseInt(sem);
			String coursecode = request.getParameter("coursecode");
			String coursetitle = request.getParameter("coursetitle");
			String ltps = request.getParameter("ltps");
			String cre = request.getParameter("credits");
			int credits = Integer.parseInt(cre);
			  byte[] bytes = file.getBytes();
			  Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
			
			Course c  =new Course();
			
			c.setYear(year);
			c.setSemester(semester);
			c.setCoursecode(coursecode);
			c.setCoursertitle(coursetitle);
			c.setLtps(ltps);
			c.setCredits(credits);
			c.setCourseimage(blob);
			
			msg = adminService.addcourse(c);
			mv.setViewName("coursedisplaymsg");
			mv.addObject("message",msg);
		}
		catch (Exception e) 
 		 {
			msg = e.getMessage();
			mv.setViewName("displayerror");
			mv.addObject("message", msg);
		 }
		return mv;
	}
	
	
	@GetMapping("viewallcoursesaction")
    public ModelAndView viewallcourses()
    {
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("viewallcourses");
    	List<Course> clist = adminService.viewallcourses();
    	mv.addObject("coursedata",clist);
    	return mv;
    }
	
	
	
	@GetMapping("viewcoursebyid")
	public ModelAndView viewcoursebyid(@RequestParam("id") int id)
	{
		ModelAndView mv = new ModelAndView();
		Course c = adminService.viewcoursebyid(id);
		mv.setViewName("viewcoursebyid");
		mv.addObject("course", c);
		return mv;
	}
	
	@GetMapping("displaycourseimage")
	public ResponseEntity<byte[]> displayprodimagedemo(@RequestParam("id") int id) throws IOException, SQLException
	{
	  Course product =  adminService.viewcoursebyid(id);
	  byte [] imageBytes = null;
	  imageBytes = product.getCourseimage().getBytes(1,(int) product.getCourseimage().length());

	  return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
	}

	@GetMapping("deletestudent")
	public ModelAndView deletestudent()
	{
		ModelAndView mv = new ModelAndView();
	       mv.setViewName("deletestudent");
	       
	       List<Student> stulist =  adminService.viewallstudents();
	       
	       mv.addObject("studentdata", stulist);
	       
	       return mv;
	}
	
	@GetMapping("delete/{id}")
    public String deleteaction(@PathVariable("id") int sid)
    {
      adminService.deleteStudent(sid);
      return "redirect:/deletestudent";
    }
	@GetMapping("deletecourse/{id}")
    public String deletecourseaction(@PathVariable("id") int sid)
    {
      adminService.deleteCourse(sid);
      return "redirect:/deletecourse";
    }
	@GetMapping("deletecourse")
	public ModelAndView deletecourse()
	{
		ModelAndView mv = new ModelAndView();
	       mv.setViewName("deletecourse");
	       
	       List<Course> c =  adminService.viewallcourses();
	       
	       mv.addObject("coursedata", c);
	       
	       return mv;
	}
	
	
	@GetMapping("addfaculty")
	public ModelAndView addfaculty()
	{
		ModelAndView mv = new ModelAndView();
		mv.addObject("addfaculty");
		return mv;
	}
	
	@GetMapping("insertfaculty")
	public ModelAndView insertfaculty(HttpServletRequest request)
	{
		ModelAndView mv = new ModelAndView();
		
		String msg = null;
		try
		{
			String id = request.getParameter("id");
			int fid = Integer.parseInt(id);
			String name = request.getParameter("name");
			String designation = request.getParameter("designation");
			String department = request.getParameter("department");
			String contact = request.getParameter("contact");
			
			Faculty f = new Faculty();
			
			f.setId(fid);
			f.setName(name);
			f.setDesignation(designation);
			f.setDepartment(department);
			f.setContact(contact);
			
			msg = adminService.addfaculty(f);
			mv.setViewName("displayadminmsg");
			mv.addObject("message",msg);
		}
		catch (Exception e) 
 		 {
			msg = e.getMessage();
			mv.setViewName("displayerror");
			mv.addObject("message", msg);
		 }
		return mv;
	}
	
	
	@GetMapping("fcoursemapping")
	  public ModelAndView facultycoursemapping()
	  {
		  ModelAndView mv = new ModelAndView("facultycoursemapping");
	  	  
	      List<Course> courselist = adminService.viewallcourses();
	      mv.addObject("coursedata", courselist);
		  
		  List<Faculty> facultylist = adminService.viewallfaculty();
		  mv.addObject("facultydata", facultylist);
		  
		  return mv;
	  }
	
	@PostMapping("fcoursemappinginsert")
	  public ModelAndView fcoursemappinginsert(HttpServletRequest request)
	  {
		  String msg=null;
		  
		  ModelAndView mv = new ModelAndView("facultycoursemapping");
		  
		  int fid = Integer.parseInt(request.getParameter("fid"));
		  int cid = Integer.parseInt(request.getParameter("cid"));
		  int section = Integer.parseInt(request.getParameter("section"));
		  String ftype = request.getParameter("ftype");
		  
		  long n = adminService.checkfcoursemapping(adminService.facultybyid(fid), adminService.viewcoursebyid(cid), section);
		  
		  if(n>0)
		  {
			  msg = "mapping already done";
		  }
		  else
		  {
			  FacultyCourseMapping fcm = new FacultyCourseMapping();
			  
			  fcm.setCourse(adminService.viewcoursebyid(cid));
			  fcm.setFaculty(adminService.facultybyid(fid));
			  fcm.setSection(section);
			  fcm.setFacultytype(ftype);
			  
			  msg = adminService.facultycoursemapping(fcm);
			    
			  
		  }
		  
		  
		  mv.addObject("message", msg);
		  
		  
		  
		  return mv;
	  }
	@GetMapping("viewfcoursemapping")
    public ModelAndView viewfcoursemapping()
    {
      ModelAndView mv = new ModelAndView("viewfcoursemapping");
      
      List<FacultyCourseMapping> fcmlist = adminService.viewfaFacultyCourseMappings();
      mv.addObject("fcmdata", fcmlist);
       
      return mv;
    }
}
