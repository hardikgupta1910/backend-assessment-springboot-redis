package com.Hardik.Backend_Assesment.Repository;

import com.Hardik.Backend_Assesment.Model.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {
}
