package com.example.pdf.controller;

import com.example.pdf.One;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: plani
 * 创建时间: 2019/11/15 14:07
 */
@Controller
public class OneController {

    @RequestMapping("/s")
    public String test(Model model) {
        System.out.println(">>>>>>>>>>>>>>>");
        model.addAttribute("one", "outsite");
        One one = new One();
        one.setAge("19");
        one.setName("www");
        model.addAttribute("po", one);
        return "one";
    }

}
