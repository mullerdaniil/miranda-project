package com.github.mullerdaniil.miranda.module.questionTraining.repository;

import com.github.mullerdaniil.miranda.module.questionTraining.entity.Question;
import com.github.mullerdaniil.miranda.module.questionTraining.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("""
           select t from QuestionTag qt
           left join qt.tag t
           where qt.question.id = :questionId""")
    List<Tag> findTagsByQuestionId(Long questionId);

/*    @Query("""
           from Tag t
           left join fetch t.questionTags
           where t.name = :name""")*/
    Optional<Tag> findByName(String name);

    Optional<Tag> findById(Long id);
}