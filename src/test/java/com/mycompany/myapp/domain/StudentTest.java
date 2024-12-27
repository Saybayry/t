package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.GroupTestSamples.*;
import static com.mycompany.myapp.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StudentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Student.class);
        Student student1 = getStudentSample1();
        Student student2 = new Student();
        assertThat(student1).isNotEqualTo(student2);

        student2.setId(student1.getId());
        assertThat(student1).isEqualTo(student2);

        student2 = getStudentSample2();
        assertThat(student1).isNotEqualTo(student2);
    }

    @Test
    void groupTest() {
        Student student = getStudentRandomSampleGenerator();
        Group groupBack = getGroupRandomSampleGenerator();

        student.setGroup(groupBack);
        assertThat(student.getGroup()).isEqualTo(groupBack);

        student.group(null);
        assertThat(student.getGroup()).isNull();
    }
}
