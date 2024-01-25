package com.skodin.repositories;

import com.skodin.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM questions.questions OFFSET floor(random() * (SELECT COUNT(*) FROM questions.questions)) LIMIT 1", nativeQuery = true)
    Question findRandom();

}
