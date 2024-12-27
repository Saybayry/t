package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DisciplineTestSamples.*;
import static com.mycompany.myapp.domain.LessonTestSamples.*;
import static com.mycompany.myapp.domain.TeacherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LessonTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Lesson.class);
        Lesson lesson1 = getLessonSample1();
        Lesson lesson2 = new Lesson();
        assertThat(lesson1).isNotEqualTo(lesson2);

        lesson2.setId(lesson1.getId());
        assertThat(lesson1).isEqualTo(lesson2);

        lesson2 = getLessonSample2();
        assertThat(lesson1).isNotEqualTo(lesson2);
    }

    @Test
    void disciplineTest() {
        Lesson lesson = getLessonRandomSampleGenerator();
        Discipline disciplineBack = getDisciplineRandomSampleGenerator();

        lesson.setDiscipline(disciplineBack);
        assertThat(lesson.getDiscipline()).isEqualTo(disciplineBack);

        lesson.discipline(null);
        assertThat(lesson.getDiscipline()).isNull();
    }

    @Test
    void teacherTest() {
        Lesson lesson = getLessonRandomSampleGenerator();
        Teacher teacherBack = getTeacherRandomSampleGenerator();

        lesson.setTeacher(teacherBack);
        assertThat(lesson.getTeacher()).isEqualTo(teacherBack);

        lesson.teacher(null);
        assertThat(lesson.getTeacher()).isNull();
    }
}
