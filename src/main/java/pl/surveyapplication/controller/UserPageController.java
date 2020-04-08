package pl.surveyapplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserPageController {

    @RequestMapping()
    public String user(){
        return "user";
    }
}
