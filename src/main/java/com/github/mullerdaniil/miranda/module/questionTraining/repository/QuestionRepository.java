package com.github.mullerdaniil.miranda.module.questionTraining.repository;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query("from Question order by random() limit 1")
    Question getRandom();

    @Query("""
           from Question q
           left join fetch q.questionTags qt
           where q.id = :id""")
    Optional<Question> findById(Long id);

    @Query("""
           select q
           from QuestionTag qt
           join qt.tag t
           join qt.question q
           where t.name in (:tagNames)
           and q.points <= :maxPoints
           order by random()
           limit 1""")
    Optional<Question> getRandomByMaxPointsAndTagNames(Integer maxPoints, Set<String> tagNames);
}