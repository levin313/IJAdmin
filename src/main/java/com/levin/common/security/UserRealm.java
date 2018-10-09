//package com.levin.common.security;
//
//
//import com.google.common.base.Objects;
//import com.levin.common.utils.security.Encodes;
//import com.levin.modules.sys.entity.Menu;
//import com.levin.modules.sys.entity.Role;
//import com.levin.modules.sys.entity.User;
//import com.levin.modules.sys.service.MenuService;
//import com.levin.modules.sys.service.UserService;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.shiro.SecurityUtils;
//import org.apache.shiro.authc.*;
//import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
//import org.apache.shiro.authz.AuthorizationInfo;
//import org.apache.shiro.authz.SimpleAuthorizationInfo;
//import org.apache.shiro.realm.AuthorizingRealm;
//import org.apache.shiro.session.Session;
//import org.apache.shiro.subject.PrincipalCollection;
//import org.apache.shiro.util.ByteSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.io.Serializable;
//
///**
// * 用户登录授权service(shrioRealm)
// */
//@Service
//@DependsOn({"userDao", "menuDao"})
//public class UserRealm extends AuthorizingRealm {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private MenuService menuService;
//
//    /**
//     * 认证回调函数,登录时调用.
//     */
//    @Override
//    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
//        //UsernamePasswordCaptchaToken token = (UsernamePasswordCaptchaToken) authcToken;
//        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
//        User user = userService.getUser(token.getUsername());
//
//        if (user != null /*&& doCaptchaValidate(token)*/) {
//            byte[] salt = Encodes.decodeHex(user.getSalt());
//            ShiroUser shiroUser = new ShiroUser(user.getId(), user.getLoginName(), user.getName());
//            //设置用户session
//            Session session = SecurityUtils.getSubject().getSession();
//            session.setAttribute("user", user);
//            return new SimpleAuthenticationInfo(shiroUser, user.getPassword(), ByteSource.Util.bytes(salt), getName());
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
//     */
//    @Override
//    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
//        ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();
//        User user = userService.getUser(shiroUser.loginName);
//
//        //把principals放session中 key=userId value=principals
//        SecurityUtils.getSubject().getSession().setAttribute(String.valueOf(user.getId()), SecurityUtils.getSubject().getPrincipals());
//
//        //赋予角色
//        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
//        //赋予角色
//        for (Role userRole : user.getRoleList()) {
//            info.addRole(userRole.getName());
//        }
//        //赋予权限
//        for (Menu menu : menuService.getPermissions(user.getId())) {
//            if (StringUtils.isNotBlank(menu.getPermission()))
//                info.addStringPermission(menu.getPermission());
//        }
//
//        return info;
//    }
//
//    /**
//     * 验证码校验
//     *
//     * @param token token
//     * @return boolean
//     */
//    private boolean doCaptchaValidate(UsernamePasswordCaptchaToken token) {
//        String captcha = (String) SecurityUtils.getSubject().getSession().getAttribute("com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY");
//        if (captcha != null && !captcha.equalsIgnoreCase(token.getCaptcha())) {
//            throw new CaptchaException("验证码错误！");
//        }
//        return true;
//    }
//
//
//    /**
//     * 设定Password校验的Hash算法与迭代次数.
//     */
//    @SuppressWarnings("static-access")
//    @PostConstruct
//    public void initCredentialsMatcher() {
//        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(userService.HASH_ALGORITHM);
//        matcher.setHashIterations(userService.HASH_INTERATIONS);
//        setCredentialsMatcher(matcher);
//    }
//
//    /**
//     * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息.
//     */
//    public static class ShiroUser implements Serializable {
//        private static final long serialVersionUID = -1373760761780840081L;
//        public String id;
//        private String loginName;
//        public String name;
//
//        private ShiroUser(String id, String loginName, String name) {
//            this.id = id;
//            this.loginName = loginName;
//            this.name = name;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        /**
//         * 本函数输出将作为默认的<shiro:principal/>输出.
//         */
//        @Override
//        public String toString() {
//            return loginName;
//        }
//
//        /**
//         * 重载hashCode,只计算loginName;
//         */
//        @Override
//        public int hashCode() {
//            return Objects.hashCode(loginName);
//        }
//
//        /**
//         * 重载equals,只计算loginName;
//         */
//        @Override
//        public boolean equals(Object obj) {
//            if (this == obj) {
//                return true;
//            }
//            if (obj == null) {
//                return false;
//            }
//            if (getClass() != obj.getClass()) {
//                return false;
//            }
//            ShiroUser other = (ShiroUser) obj;
//            if (loginName == null) {
//                return other.loginName == null;
//            } else return loginName.equals(other.loginName);
//        }
//    }
//
//    @Override
//    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
//        super.clearCachedAuthorizationInfo(principals);
//    }
//
//    @Override
//    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
//        super.clearCachedAuthenticationInfo(principals);
//    }
//
//    @Override
//    public void clearCache(PrincipalCollection principals) {
//        super.clearCache(principals);
//    }
//
//}
