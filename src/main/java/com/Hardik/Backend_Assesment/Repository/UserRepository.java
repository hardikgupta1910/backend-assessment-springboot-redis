package com.Hardik.Backend_Assesment.Repository;

import com.Hardik.Backend_Assesment.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
