package com.levin.modules.sys.web;

import com.levin.common.base.Json;
import com.levin.modules.sys.entity.Area;
import com.levin.modules.sys.entity.Organization;
import com.levin.modules.sys.entity.User;
import com.levin.modules.sys.service.AreaService;
import com.levin.modules.sys.service.OrganizationService;
import com.levin.modules.sys.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * TODO 完善注释
 *
 * @auther: lwt
 * @Date: 2018-10-02 00:15
 */
@Controller
@RequestMapping("/sys")
public class IndexController {
    @Autowired
    private AreaService areaService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrganizationService organizationService;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(value = {"", "/test"})
    public String hello(Model model) {
        model.addAttribute("msg", "msg from controller");
        return "index";
    }

    @RequestMapping("/login")
    public String index() {
        return "modules/sys/login";
    }

    @RequestMapping("/home")
    public String home() {
        return "modules/sys/index";
    }

    @RequestMapping("/deskTop")
    public String deskTop() {
        return "modules/sys/sysDesktop";
    }

    @RequestMapping(value = "/area"/*,produces = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
    @ResponseBody
    public Json areaList() {
        //List<Area> areaList = areaService.findBy("code", "0");
        List<Area> areaList = areaService.findBy("id", "110000");
        return new Json().setSuccess(true).setMsg("success").setData(areaList);
    }

    @RequestMapping("/user")
    @ResponseBody
    public Json user() {
        User user = new User();
        user.setPlainPass("123456");
        user.setEmail("levin@gmail.com");
        user.setLoginName("levin");
        user.setName("李万涛");
        user.setUserType("admin");

        user.setOrganization(organizationService.find("1"));
        userService.save(user);
        return new Json().setSuccess(true).setMsg("success").setData(null);
    }
}
