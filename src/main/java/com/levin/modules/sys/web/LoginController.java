//package com.levin.modules.sys.web;
//
//import com.levin.common.base.Json;
//import com.levin.modules.sys.entity.User;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.UsernamePasswordToken;
//import org.apache.shiro.subject.Subject;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//@Controller
//@RequestMapping("/sys")
//public class LoginController {
//
//    @RequestMapping(value = "/index", method = RequestMethod.GET)
//    public String index(Model model) {
//        Subject subject = SecurityUtils.getSubject();
//        User user = (User) subject.getSession().getAttribute("user");
//        model.addAttribute("userName", user.getName());
//        return "modules/sys/index";
//    }
//
//    @RequestMapping(value = {"/login", ""}, method = RequestMethod.GET)
//    public String login() {
//        Subject subject = SecurityUtils.getSubject();
//        if (subject.isAuthenticated() || subject.isRemembered()) {
//            return "redirect:admin/index";
//        }
//
//        return "modules/sys/login";
//    }
//
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
//    @ResponseBody
//    public Json check(String userName, String password) {
//        try {
//            UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
//            Subject currentUser = SecurityUtils.getSubject();
//            if (!currentUser.isAuthenticated()) {
//                token.setRememberMe(true);
//                currentUser.login(token);
//            }
//        } catch (Exception e) {
//            return new Json().setSuccess(false).setMsg("登录失败");
//        }
//        return new Json().setSuccess(true).setMsg("success");
//    }
//
//    @RequestMapping(value = "/logout", method = RequestMethod.GET)
//    public String logout() {
//        Subject subject = SecurityUtils.getSubject();
//        subject.logout();
//        return "modules/sys/login";
//    }
//
//
//}
