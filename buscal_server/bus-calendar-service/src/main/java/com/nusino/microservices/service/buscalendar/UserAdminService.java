/**
 * copyright © Nusino Technologies Inc, 2021, All rights reserved.
 * dhuang05@gmail.com
 */

package com.nusino.microservices.service.buscalendar;

import com.nusino.microservices.dao.businesscalendar.repository.*;
import com.nusino.microservices.exception.AuthorizationException;
import com.nusino.microservices.exception.FeedbackableException;
import com.nusino.microservices.jwt.UserTokenSession;
import com.nusino.microservices.model.buscalendar.*;
import com.nusino.microservices.service.buscalendar.util.CommonUtil;
import com.nusino.microservices.service.buscalendar.util.JsonUtil;
import com.nusino.microservices.vo.buscalendar.RegistrationForm;
import com.nusino.microservices.vo.buscalendar.RoleConst;
import com.nusino.microservices.vo.buscalendar.UserSecurityDetails;
import io.jsonwebtoken.Jwts;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;



@Service
public class UserAdminService  implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private UserResetRequestRepository userResetRequestRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private BusinessCalendarOwnershipRepository businessCalendarOwnershipRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Value("${SUPER_PUBLIC_ORG_ID:SUPER_ORG}")
    private String superOrgId;

    public Optional<User> findById(String userId) {
        return usersRepository.findById(userId);
    }

    public User save(User user) {
        return usersRepository.save(user);
    }

    @Transactional
    public void logout(String userId) {
        usersRepository.updateToken(userId, null);
    }

    public void typeEncoded(String text)  {
        System.out.println("---Encoded:" + encoder.encode(text));
    }

    public User login(String userId, String password) {
        Optional<User> userOptional = usersRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "User not found, or password not matched!");
        }
        User user = userOptional.get();
        if (user == null || user.getPassword() == null || user.getPassword().trim().isEmpty() || password == null || password.trim().isEmpty() ||
                !encoder.matches(password.trim(), user.getPassword().trim())) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "User not found, or password not matched!");
        }
        if (user.getStatus() != Status.ACTIVE) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "User is not activated!");
        }
        return user;
    }

    public boolean isUserIdTaken(String userId) {
        return usersRepository.findUserById(userId) != null ? true : false;
    }

    public void _saveUser(User user) {
        String password = user.getPassword();
        if (password == null) {
            Optional<User> existingUserOption = usersRepository.findById(user.getUserId());
            User existingUser = existingUserOption.get();
            if (existingUser != null) {
                password = existingUser.getPassword();
            }
        }
        if (password == null || password.trim().isEmpty()) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "Password required");
        }
        user.setPassword(encoder.encode(password.trim()));
        usersRepository.save(user);
    }

    public void resetPasswordAndActivateUser(String userId, String newPassword, String token) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "Password required");
        }
        Optional<User> existingUserOption = usersRepository.findById(userId);
        if (!existingUserOption.isPresent() || existingUserOption.get() == null) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "User not found");
        }

        User existingUser = existingUserOption.get();
        existingUser.setStatus(Status.ACTIVE);
        existingUser.setResetToken(null);
        existingUser.setTokenCreatedDate(null);
        existingUser.setPassword(encoder.encode(newPassword.trim()));
        usersRepository.save(existingUser);
    }

    public void resetPassword(String userId, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty() || oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "Password required");
        }
        Optional<User> existingUserOption = usersRepository.findById(userId);
        if (!existingUserOption.isPresent() || existingUserOption.get() == null) {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "User not found or password not matched");
        }
        User existingUser = existingUserOption.get();
        if (encoder.matches(oldPassword.trim(), existingUser.getPassword().trim())) {
            existingUser.setPassword(encoder.encode(newPassword.trim()));
            existingUser = usersRepository.save(existingUser);
        } else {
            throw new AuthorizationException(AuthorizationException.CODE.UNAUTHORIZED, "User not found or password not matched");
        }
    }

    @Transactional
    public User saveNewUserWithInfo(User user) {
        if (isUserIdTaken(user.getUserId().trim())) {
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "UserId is taken, please input other one.");
        }
        String email = user.getPerson().getContact().getEmail();
        if(!CommonUtil.isEmailValid(email) || isUserEmailTaken(email)){
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "Email already taken or is invalid, please try other email");
        }
        return saveUserWithInfo(user);
    }

    @Transactional
    public User registerUser(RegistrationForm registrationForm) {
        User user = registrationForm.getUser();
        if(isUserIdTaken(user.getUserId())){
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "UserId is taken, please input other one.");
        }
        String email = user.getPerson().getContact().getEmail();
        if(!CommonUtil.isEmailValid(email) || isUserEmailTaken(email)){
            throw new FeedbackableException(FeedbackableException.CODE.DATA_ERR, "Email already taken or is invalid, please try other email");
        }

        if(user.getPerson().getPersonId() == null) {
            user.getPerson().setPersonId(CommonUtil.newUuidNoDash());

        }
        if(user.getPerson().getContact().getContactId() == null) {
            user.getPerson().getContact().setContactId(CommonUtil.newUuidNoDash());
        }

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setRoleId(RoleConst.ADMIN_ROLE);
        roles.add(role);
        //
        role = new Role();
        role.setRoleId(RoleConst.API_ROLE);
        roles.add(role);
        //
        role = new Role();
        role.setRoleId(RoleConst.TRIAL_ROLE);
        roles.add(role);
        //
        user.setRoles(roles);

        //
        Organization organization = registrationForm.getOrganization();
        if(organization.getFirstContactPerson().getPersonId() == null) {
            organization.getFirstContactPerson().setPersonId(CommonUtil.newUuidNoDash());

        }
        if(organization.getFirstContactPerson().getContact().getContactId() == null) {
            organization.getFirstContactPerson().getContact().setContactId(CommonUtil.newUuidNoDash());
        }

        organization.setOrgId(CommonUtil.newUuidNoDash());
        organizationRepository.saveAndFlush(organization);

        user.setOrgId(organization.getOrgId());

        return saveUserWithInfo(user);
    }


    @Transactional
    public User saveUserWithInfo(User user) {
        if (StringUtil.isNullOrEmpty(user.getUserId())) {
            throw new AuthorizationException(AuthorizationException.CODE.DATA_ISSUE, "User ID required");
        }
        if (user.getPerson() != null) {
            if (StringUtil.isNullOrEmpty(user.getPerson().getPersonId())) {
                user.getPerson().setPersonId(CommonUtil.newUuidNoDash());
            }
            if (user.getPerson().getContact() != null) {
                if (StringUtil.isNullOrEmpty(user.getPerson().getContact().getContactId())) {
                    user.getPerson().getContact().setContactId(CommonUtil.newUuidNoDash());
                }
            }
        }
        Optional<User> existingUserOption = usersRepository.findById(user.getUserId());
        User retUser = null;
        if (existingUserOption.isPresent() && existingUserOption.get() != null) {
            User existingUser = existingUserOption.get();
            if (user.getPassword() != null && !StringUtils.isEmpty(user.getPassword().trim())) {
                user.setPassword(encoder.encode(user.getPassword().trim()));
            } else {
                user.setPassword(existingUser.getPassword());
            }
            user.setToken(existingUser.getToken());
            user.setResetToken(null);
        } else {
            if (user.getPassword() != null && !StringUtils.isEmpty(user.getPassword().trim())) {
                user.setPassword(encoder.encode(user.getPassword().trim()));
            }
        }
        retUser = usersRepository.saveAndFlush(user);
        return retUser;
    }

    public List<BusinessCalendarOwnership> findUserCalendarOwnerShipsByOrgIds(String... orgIds) {
        Set<String> idList = new HashSet<>();
        for (String orgId : orgIds) {
            if (orgId == null) {
                continue;
            }
            findOrgChildren(orgId, idList);
        }
        List<BusinessCalendarOwnership> calendarOwnerships = businessCalendarOwnershipRepository.findByOrgIds(new ArrayList<>(idList));
        for (BusinessCalendarOwnership calendarOwnership : calendarOwnerships) {
            calendarOwnership.setCalendarInstJson(null);
            calendarOwnership.setCalendarInstUrl(null);
        }
        return calendarOwnerships;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = usersRepository.findUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + userId);
        }
        return UserSecurityDetails.build(user);
    }

    public List<User> findUsersByKeywordOrOrgId(String keyword, String orgId) {
        List<User> users = null;
        if (keyword != null && !StringUtils.isEmpty(keyword.trim())) {
            keyword = "*" + keyword.trim() + "*";
            keyword = keyword.replaceAll("[*]{1,}", "%").toUpperCase().trim();
        }
        if (keyword != null && !StringUtils.isEmpty(keyword.trim()) && orgId != null && !StringUtils.isEmpty(orgId.trim())) {
            users = usersRepository.findByKeywordAndOrgId(keyword, orgId);
        } else {
            if (keyword != null && !StringUtils.isEmpty(keyword.trim())) {
                users = usersRepository.findByKeyword(keyword);
            } else {
                users = usersRepository.findByOrgId(orgId);
            }
        }
        if (users != null) {
            for (User user : users) {
                if (user.getPerson() != null) {
                    user.setPerson(copyPerson(user.getPerson()));
                }
            }
        }
        return users;
    }


    public Organization findOrganizationById(String orgId) {
        return organizationRepository.findById(orgId).get();
    }

    public List<Organization> findOrganizationsByKeywordOrUserId(String keyword, String userId) {
        List<Organization> organizations = null;
        if (keyword != null && !StringUtils.isEmpty(keyword.trim())) {
            keyword = "*" + keyword.trim() + "*";
            keyword = keyword.replaceAll("[*]{1,}", "%").toUpperCase();
        }
        if (keyword != null && !StringUtils.isEmpty(keyword.trim()) && userId != null && !StringUtils.isEmpty(userId.trim())) {
            organizations = organizationRepository.findByKeywordAndUserId(userId, keyword);
        } else {
            if (keyword != null && !StringUtils.isEmpty(keyword.trim())) {
                organizations = organizationRepository.findByKeyword(keyword);
            } else if(userId != null) {
                organizations = organizationRepository.findByUserId(userId);
            } else {
                organizations = organizationRepository.findAll();
            }
        }

        if (organizations != null) {
            for (Organization organization : organizations) {
                if (organization.getFirstContactPerson() != null) {
                    organization.setFirstContactPerson(copyPerson(organization.getFirstContactPerson()));
                }
                if (organization.getSecondContactPerson() != null) {
                    organization.setSecondContactPerson(copyPerson(organization.getSecondContactPerson()));
                }
            }
        }

        return organizations;
    }


    public List<BusinessCalendarOwnership> findUserAccessibleCalendarOwnerShips(String userOrgId) {
        List<BusinessCalendarOwnership> temps =  findUserCalendarOwnerShipsByOrgIds(superOrgId, userOrgId);
        List<BusinessCalendarOwnership> sorted = new ArrayList<>();
        for(BusinessCalendarOwnership owner : temps) {
            if(owner.getOwnerId().trim().equalsIgnoreCase(RoleConst.SUPER_ORG.trim())) {
                sorted.add(owner);
            }
        }
        temps.removeAll(sorted);
        sorted.addAll(temps);
        return sorted;
    }

    public void findOrgChildren(String parentOrgId, Set<String> idList) {
        idList.add(parentOrgId);
        List<Organization> children = organizationRepository.findAllChildren(parentOrgId);
        if (children != null && children.size() > 0) {
            for (Organization org : children) {
                idList.add(org.getOrgId());
                findOrgChildren(org.getOrgId(), idList);
            }
        }
    }

    @Transactional
    public boolean forgetPassword(UserResetRequest userResetRequest) {
        userResetRequestRepository.saveAndFlush(userResetRequest);
        return true;
    }

    @Transactional
    public Organization saveOrganization(Organization organization) {
        if (StringUtil.isNullOrEmpty(organization.getOrgId())) {
            organization.setOrgId(CommonUtil.newUuidNoDash());
        }
        if (organization.getFirstContactPerson() != null) {
            if (organization.getFirstContactPerson().getContact() != null) {
                if (StringUtil.isNullOrEmpty(organization.getFirstContactPerson().getContact().getContactId())) {
                    organization.getFirstContactPerson().getContact().setContactId(CommonUtil.newUuidNoDash());
                }
                organization.getFirstContactPerson().setContact(contactRepository.save(organization.getFirstContactPerson().getContact()));
            }
            if (StringUtil.isNullOrEmpty(organization.getFirstContactPerson().getPersonId())) {
                organization.getFirstContactPerson().setPersonId(CommonUtil.newUuidNoDash());
            }
            organization.setFirstContactPerson(personRepository.save(organization.getFirstContactPerson()));
        }

        if (organization.getSecondContactPerson() != null) {
            if (StringUtil.isNullOrEmpty(organization.getSecondContactPerson().getFirstName()) && StringUtil.isNullOrEmpty(organization.getSecondContactPerson().getLastName())) {
                organization.setSecondContactPerson(null);
            }
            if (organization.getSecondContactPerson().getContact() != null) {
                if (StringUtil.isNullOrEmpty(organization.getSecondContactPerson().getContact().getContactId())) {
                    organization.getSecondContactPerson().getContact().setContactId(CommonUtil.newUuidNoDash());
                }
                organization.getSecondContactPerson().setContact(contactRepository.save(organization.getSecondContactPerson().getContact()));
            }
            if (StringUtil.isNullOrEmpty(organization.getSecondContactPerson().getPersonId())) {
                organization.getSecondContactPerson().setPersonId(CommonUtil.newUuidNoDash());
            }
            organization.setSecondContactPerson(personRepository.save(organization.getSecondContactPerson()));
        }

        return organizationRepository.saveAndFlush(organization);
    }

    public Optional<UserDetails> findByToken(String token) {
        User user = usersRepository.findUserByToken(token);
        UserDetails userDetails = JsonUtil.clone(user, UserDetails.class);
        return Optional.of(userDetails);
    }

    @Transactional
    public void updateToken(String userId, String token) {
        usersRepository.updateToken(userId, token);
    }

    public boolean isUserEmailTaken (String email) {
        return contactRepository.countByEmail(email.trim()) > 0 ? true : false;
    }

    private Person copyPerson(Person person) {
        if (person == null) {
            return null;
        }
        Person cloned = new Person();
        cloned.setPersonId(person.getPersonId());
        cloned.setContact(copyContact(person.getContact()));
        cloned.setFirstName(person.getFirstName());
        cloned.setLastName(person.getLastName());
        cloned.setPosition(person.getPosition());
        cloned.setSolute(person.getSolute());
        cloned.setTitle(person.getTitle());
        return cloned;
    }

    private Contact copyContact(Contact contact) {
        if (contact == null) {
            return null;
        }
        Contact cloned = new Contact();
        cloned.setContactId(contact.getContactId());
        cloned.setEmail(contact.getEmail());
        cloned.setMailingAddress(contact.getMailingAddress());
        cloned.setOtherInfo(contact.getOtherInfo());
        cloned.setPrimaryPhone(contact.getPrimaryPhone());
        cloned.setSecondaryPhone(contact.getSecondaryPhone());
        return cloned;
    }


}
