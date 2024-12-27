package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DisciplineTestSamples.*;
import static com.mycompany.myapp.domain.TeacherTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeacherTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Teacher.class);
        Teacher teacher1 = getTeacherSample1();
        Teacher teacher2 = new Teacher();
        assertThat(teacher1).isNotEqualTo(teacher2);

        teacher2.setId(teacher1.getId());
        assertThat(teacher1).isEqualTo(teacher2);

        teacher2 = getTeacherSample2();
        assertThat(teacher1).isNotEqualTo(teacher2);
    }

    @Test
    void disciplineTest() {
        Teacher teacher = getTeacherRandomSampleGenerator();
        Discipline disciplineBack = getDisciplineRandomSampleGenerator();

        teacher.addDiscipline(disciplineBack);
        assertThat(teacher.getDisciplines()).containsOnly(disciplineBack);

        teacher.removeDiscipline(disciplineBack);
        assertThat(teacher.getDisciplines()).doesNotContain(disciplineBack);

        teacher.disciplines(new HashSet<>(Set.of(disciplineBack)));
        assertThat(teacher.getDisciplines()).containsOnly(disciplineBack);

        teacher.setDisciplines(new HashSet<>());
        assertThat(teacher.getDisciplines()).doesNotContain(disciplineBack);
    }
}
