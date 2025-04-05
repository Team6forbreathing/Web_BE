package com.example.sleeping.user.domain;

import com.example.sleeping.user.presentation.dto.UserRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String password;

    private String name;

    private String gender;

    private int age;

    private int height;

    private int weight;

    private boolean comp;

    private Role role;

    private User(String userId, String password, String name, String gender, int age, int height, int weight, boolean comp) {
        this.userId = userId;
        this.password = genPw(password);
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.comp = comp;
        this.role = Role.USER;
    }

    public static User of(UserRequest userRequest) {
        return new User(
                userRequest.userId(),
                userRequest.userPw(),
                userRequest.userName(),
                userRequest.userGender(),
                userRequest.userAge(),
                userRequest.userHeight(),
                userRequest.userWeight(),
                userRequest.userComp()
        );
    }

    private String genPw(String rawPw) {
        return BCrypt.hashpw(rawPw, BCrypt.gensalt());
    }

    public boolean checkPw(String pw) {
        return BCrypt.checkpw(pw, this.password);
    }

    public void update(UserRequest userRequest) {
        this.name = userRequest.userName();
        this.gender = userRequest.userGender();
        this.age = userRequest.userAge();
        this.height = userRequest.userHeight();
        this.weight = userRequest.userWeight();
        this.comp = userRequest.userComp();
    }

    public void updatePw(String password) {
        this.password = genPw(password);
    }

    public void grant() {
        if(this.role == Role.USER) {
            this.role = Role.AUTHORIZED_USER;
            return;
        }

        this.role = Role.USER;
    }
}
