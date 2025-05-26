package com.example.sleeping.data.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DataCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long count;

    private DataCount(Long count) {
        this.count = count;
    }

    public static DataCount from(Long count) {
        return new DataCount(count);
    }

    public void updateCount(Long count) {
        this.count = count;
    }
}
