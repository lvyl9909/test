package org.teamy.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.teamy.backend.DataMapper.StudentDataMapper;
import org.teamy.backend.model.Person;
import org.teamy.backend.repository.StudentClubRepository;
import org.teamy.backend.repository.StudentRepository;
import org.teamy.backend.security.model.Role;
import org.teamy.backend.service.StudentClubService;
import org.teamy.backend.service.StudentService;

import java.sql.SQLException;
import java.util.*;

public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;
    private final StudentClubRepository studentClubRepository;
    private static CustomUserDetailsService instance;
    public static synchronized CustomUserDetailsService getInstance(StudentRepository studentRepository,StudentClubRepository studentClubRepository) {
        if (instance == null) {
            instance = new CustomUserDetailsService(studentRepository,studentClubRepository);
        }
        return instance;
    }
    public CustomUserDetailsService(StudentRepository studentRepository,StudentClubRepository studentClubRepository) {
        this.studentRepository = studentRepository;
        this.studentClubRepository = studentClubRepository;
    }

    @Override
    public Person loadUserByUsername(String username) throws UsernameNotFoundException {
        Person user=null;
        try {
            user = studentRepository.findStudentByUsername(username);

            if (user == null) {
                System.out.println("username not found");
                throw new UsernameNotFoundException("User not found");
            }else {
                // 添加基础角色
                Set<Role> roles = new HashSet<>();
                roles.add(new Role("USER"));  // 基础角色

                // 加载 clubId 列表并动态添加角色
                List<Integer> clubIds = studentClubRepository.findClubIdByStudentId(Math.toIntExact(user.getId()));
                for (Integer clubId : clubIds) {
                    roles.add(new Role("CLUB_" + clubId));  // 动态角色
                }

                // 将所有角色设置到用户对象
                user.setRoles(roles);
                System.out.println("yong hu quan xian :"+user.getAuthorities());
                System.out.println(user.getPassword());
            }
            return user;
        } catch (SQLException e) {
            throw new UsernameNotFoundException("Database error", e);
        }
    }

    // 你可以添加方法来动态添加或删除用户
}