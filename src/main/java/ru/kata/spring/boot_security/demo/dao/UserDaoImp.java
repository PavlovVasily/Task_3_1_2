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
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class UserDaoImp implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public boolean saveUser(String name, String email, byte age, String password) {
        User userFromDB = getUserByEmail(email);
        if (userFromDB != null) {
            return false;
        }

        User user = new User();
        user.setAge(age);
        user.setName(name);
        user.setEmail(email);
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
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

        return em.createQuery("select u from User u join fetch u.roles").getResultList();

    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        User userFromDB = getUserByEmail(user.getEmail());
        if (userFromDB != null) {
            return false;
        }

        em.merge(user);
        return true;
    }

    @Override
    public User getUserById(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("Пользователя с Id=" + id + " нет");
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
