package com.example.sleeping.data.persisteent;

import com.example.sleeping.data.domain.DataCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataCountRepository extends JpaRepository<DataCount, Long> {
}
