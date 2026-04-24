package com.Hardik.Backend_Assesment.Repository;

import com.Hardik.Backend_Assesment.Model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
