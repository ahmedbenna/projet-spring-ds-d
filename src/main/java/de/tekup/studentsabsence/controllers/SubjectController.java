package de.tekup.studentsabsence.controllers;


import de.tekup.studentsabsence.entities.Group;
import de.tekup.studentsabsence.entities.Student;
import de.tekup.studentsabsence.entities.Subject;
import de.tekup.studentsabsence.services.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/subjects")
@AllArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;
    private  final GroupSubjectService groupSubjectService;
    private  final StudentService studentService;
    private final EmailService emailService;
    private final GroupService groupService;

    @GetMapping({"", "/"})
    public String index(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "subjects/index";
    }
    @GetMapping("/add")
    public String addView(Model model) {
        model.addAttribute("subject", new Subject());
        return "subjects/add";
    }
    @PostMapping("/add")
    public String add(@Valid Subject subject, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "subjects/add";
        }
        subjectService.addSubject(subject);
        return "redirect:/subjects";
    }
    @GetMapping("/{id}/update")
    public String updateView(@PathVariable Long id, Model model) {
        model.addAttribute("subject", subjectService.getSubjectById(id));
        return "subjects/update";
    }
    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid Subject subject, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "subjects/update";
        }
        subjectService.updateSubject(subject);
        return "redirect:/subjects";
    }
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return "redirect:/subjects";
    }

    @GetMapping("/{id}/show")
    public String show(@PathVariable Long id, Model model) {
        //Question 2
        List<Group> groups=new ArrayList<>();
        groupSubjectService.getSubjectsGroupBySubjectId(id).forEach(groupSubject -> groups.add(groupSubject.getGroup()));
        model.addAttribute("subject", subjectService.getSubjectById(id));
        model.addAttribute("groups",groups);
        model.addAttribute("subjectService",subjectService);
        return "subjects/show";
    }
    @GetMapping("/Mail/{sid}/{sbid}")
    public String sendingMail(@PathVariable Long sid,@PathVariable Long sbid){
        Subject subject=subjectService.getSubjectById(sbid);
        Student student= studentService.getStudentBySid(sid);
        emailService.sendEliminatedEmail(student,subject);
        return "redirect:/subjects/"+sbid+"/show";
    }


}