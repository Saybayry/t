package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DisciplineTestSamples.*;
import static com.mycompany.myapp.domain.TeacherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DisciplineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Discipline.class);
        Discipline discipline1 = getDisciplineSample1();
        Discipline discipline2 = new Discipline();
        assertThat(discipline1).isNotEqualTo(discipline2);

        discipline2.setId(discipline1.getId());
        assertThat(discipline1).isEqualTo(discipline2);

        discipline2 = getDisciplineSample2();
        assertThat(discipline1).isNotEqualTo(discipline2);
    }

    @Test
    void teacherTest() {
        Discipline discipline = getDisciplineRandomSampleGenerator();
        Teacher teacherBack = getTeacherRandomSampleGenerator();

        discipline.addTeacher(teacherBack);
        assertThat(discipline.getTeachers()).containsOnly(teacherBack);
        assertThat(teacherBack.getDisciplines()).containsOnly(discipline);

        discipline.removeTeacher(teacherBack);
        assertThat(discipline.getTeachers()).doesNotContain(teacherBack);
        assertThat(teacherBack.getDisciplines()).doesNotContain(discipline);

        discipline.teachers(new HashSet<>(Set.of(teacherBack)));
        assertThat(discipline.getTeachers()).containsOnly(teacherBack);
        assertThat(teacherBack.getDisciplines()).containsOnly(discipline);

        discipline.setTeachers(new HashSet<>());
        assertThat(discipline.getTeachers()).doesNotContain(teacherBack);
        assertThat(teacherBack.getDisciplines()).doesNotContain(discipline);
    }
}
