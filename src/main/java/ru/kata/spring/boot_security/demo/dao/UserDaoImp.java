package ru.kata.spring.boot_security.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class UserDaoImp implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    public void iniRoles () {
        System.out.println("iniRoles");
        em.createNativeQuery("INSERT INTO `pptask_3_1_2`.`roles` (`id`, `role`) VALUES ('1', 'ROLE_USER');")
                .executeUpdate();
        em.createNativeQuery("INSERT INTO `pptask_3_1_2`.`roles` (`id`, `role`) VALUES ('2', 'ROLE_ADMIN');")
                .executeUpdate();

    }

    @Override
    @Transactional
    public boolean saveUser(String name, String email, byte age, String password, String[] roleNames) {
        User userFromDB = getUserByEmail(email);
        if (userFromDB != null) {
            return false;
        }

        User user = new User();
        user.setAge(age);
        user.setName(name);
        user.setEmail(email);

        List<Role> roleList = em.createQuery("select r from Role r where r.role IN (:roles)", Role.class)
                .setParameter("roles", Arrays.asList(roleNames))
                .getResultList();
        if (roleList == null) {
            return false;
        }
        user.setRoles(new HashSet<>(roleList));

        user.setPassword(passwordEncoder.encode(password));
        em.persist(user);
        em.flush();
        return true;
    }

    @Override
    @Transactional
    public void removeUserById(long id) {
        em.remove(getUserById(id));
        em.flush();
    }

    @Override
    public List<User> getAllUsers() {

        List<User> notSortList = new ArrayList<>(
                new HashSet<>(
                        em.createQuery("select u from User u join fetch u.roles").
                                getResultList()));

        return notSortList.stream().
                sorted(((o1, o2) -> (int) (o1.getId() - o2.getId()))).
                collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean updateUser(User user, String[] roleNames) {
        List<Role> roleList = em.createQuery("select r from Role r where r.role IN (:roles)", Role.class)
                .setParameter("roles", Arrays.asList(roleNames))
                .getResultList();
        if (roleList == null) {
            return false;
        }
        user.setRoles(new HashSet<>(roleList));

        em.merge(user);
        return true;
    }

    @Override
    public User getUserById(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("???????????????????????? ?? Id=" + id + " ??????");
        }
        return user;
    }

    @Override
    public User getUserByEmail(String email) {
        return getAllUsers().stream()
                .filter(u -> u.getEmail().equals(email))
                .findAny().orElse(null);
    }

}
