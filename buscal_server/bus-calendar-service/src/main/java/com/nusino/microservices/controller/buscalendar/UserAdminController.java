/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */
package com.nusino.microservices.controller.buscalendar;

import com.nusino.microservices.exception.AuthorizationException;
import com.nusino.microservices.jwt.JwtUtils;
import com.nusino.microservices.jwt.UserTokenSession;
import com.nusino.microservices.model.buscalendar.*;
import com.nusino.microservices.service.buscalendar.UserAdminService;
import com.nusino.microservices.vo.buscalendar.LoginForm;
import com.nusino.microservices.vo.buscalendar.RegistrationForm;
import com.nusino.microservices.vo.buscalendar.RoleConst;
import com.nusino.microservices.vo.buscalendar.UserSecurityDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/admin")
public class UserAdminController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserTokenSession userTokenSession;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserAdminService userService;

    @PostMapping("auth/user/register")
    public @ResponseBody User registerUser(@RequestBody RegistrationForm registrationForm) {
        User user = userService.registerUser(registrationForm);
        return  securityFilter(user);
    }

    @PostMapping(path = "auth/user/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody User login(HttpServletRequest request, @RequestBody LoginForm login) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.getUserId(), login.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserSecurityDetails userDetails = (UserSecurityDetails) authentication.getPrincipal();
        List<Role> roles = userDetails.getAuthorities().stream()
                .map(item -> {
                    Role role = new Role();
                    role.setRoleId(item.getAuthority());
                    return role;
                })
                .collect(Collectors.toList());
        User user = new User();
        user.setRoles(roles);

        user.setOrgId(userDetails.getOrgId());
        user.setUserId(userDetails.getUsername());
        //user.setPerson(userDetails.getPerson());

        String jwt = jwtUtils.generateJwtToken(authentication);
        user.setToken(jwt);
        userService.updateToken(user.getUserId(), jwt);
       return  securityFilter(user);
    }

    @GetMapping(path = "user/logout/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResultInfo logout(HttpServletRequest request, @PathVariable String userId) {
        userService.logout(userId);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setStatus("OK");
        return resultInfo;
    }

    @PostMapping(path = "user/resetpassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    User resetpassword(HttpServletRequest request, @RequestBody LoginForm login) {
        User user = userService.login(login.getUserId(), login.getPassword());
        userService.resetPassword(login.getUserId(), login.getPassword(), login.getNewPassword());
        return  securityFilter(user);
    }

    @PostMapping(path = "auth/user/forgetpassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResultInfo forgetPassword(HttpServletRequest request, @RequestBody LoginForm login) {
        UserResetRequest userResetRequest = new UserResetRequest();
        userResetRequest.setUserId(login.getUserId());
        userResetRequest.setEmail(login.getEmail());
        boolean ok = userService.forgetPassword(userResetRequest);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setStatus(ok ? "OK" : "Err");
        return resultInfo;
    }


    @PostMapping(path = "organization", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Organization saveOrganization(HttpServletRequest request, @RequestBody Organization organization) {
        return userService.saveOrganization(organization);
    }

    @GetMapping(path = "organization/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Organization findOrganization(HttpServletRequest request, @PathVariable String orgId) {
        return userService.findOrganizationById(orgId);
    }

    @GetMapping(path = "organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<Organization> findOrganizations(HttpServletRequest request, @RequestParam(required = false) String keyword, @RequestParam(required = false) String userId) {
        String loginUserId = userTokenSession.retrieveTokenUserId(request);
        Optional<User> userOpt = userService.findById(loginUserId);
        if(!userOpt.isPresent()) {
            throw new AuthorizationException(AuthorizationException.CODE.REQUIRED_LOGIN, "Please login, then try again!");
        }
        User loginUser = userOpt.get();
        boolean isSuperOrgAdmin = false;
        if(loginUser.getOrgId().equalsIgnoreCase(RoleConst.SUPER_ORG)) {
            isSuperOrgAdmin = true;
        }
        if(isSuperOrgAdmin) {
            //userId can null or anything
            return userService.findOrganizationsByKeywordOrUserId(keyword, userId);
        } else {
            //must be itself
            return userService.findOrganizationsByKeywordOrUserId(keyword, loginUserId);
        }

    }

    @PostMapping(path = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    User saveUserWithInfo(HttpServletRequest request, @RequestBody User user) {
        user = userService.saveUserWithInfo(user);
        return securityFilter(user);
    }

    @PostMapping(path = "newuser", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    User saveNewUserWithInfo(HttpServletRequest request, @RequestBody User user) {
        user = userService.saveNewUserWithInfo(user);
        return securityFilter(user);
    }


    @GetMapping(path = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<User> findUsers(HttpServletRequest request, @RequestParam(required = false) String keyword, @RequestParam(required = false) String orgId) {
        String userId = userTokenSession.retrieveTokenUserId(request);
        Optional<User> userOpt = userService.findById(userId);
        if(!userOpt.isPresent()) {
            throw new AuthorizationException(AuthorizationException.CODE.REQUIRED_LOGIN, "Please login, then try again!");
        }
        User loginUser = userOpt.get();
        boolean isSuperOrgAdmin = false;
        boolean isSuperRole = isSuperUser(loginUser);
        if(loginUser.getOrgId().equalsIgnoreCase(RoleConst.SUPER_ORG)) {
            isSuperOrgAdmin = true;
        } else {
            orgId = loginUser.getOrgId();
        }
        List<User> users = userService.findUsersByKeywordOrOrgId(keyword, orgId);
        List<User> returnUsers = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                securityFilter(user);
                if(isSuperRole) {
                    returnUsers.add(user);
                }else if(isSuperOrgAdmin) {
                    if(!isSuperUser(user)) {
                        returnUsers.add(user);
                    }
                } else {
                    //orgId already confined select values, add everything
                    returnUsers.add(user);
                }
            }
        }
        return returnUsers;
    }

    @GetMapping(path = "auth/transform/{text}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResultInfo typeEncoded(@PathVariable String text) {
        userService.typeEncoded(text);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setStatus("OK");
        return resultInfo;
    }


    @GetMapping(path = "user/businessCalendarOwnerships/{orgId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<BusinessCalendarOwnership> findUserCalendarOwnerships(@PathVariable String orgId) {
        List<BusinessCalendarOwnership> businessCalendarOwnerships = userService.findUserCalendarOwnerShipsByOrgIds(orgId);
        return businessCalendarOwnerships;
    }

    private User securityFilter(User user) {
        if (user == null) {
            return user;
        }
        user.setResetToken(null);
        user.setPassword(null);
        return user;
    }

    private boolean isSuperUser(User user) {
        if(user.getRoles() == null) {
            return false;
        }

        for(Role role : user.getRoles()) {
            if(role.getRoleId().equalsIgnoreCase(RoleConst.SUPER_ADMIN_ROLE)) {
                return true;
            }
        }

        return false;
    }


    static class ResultInfo{
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

}
