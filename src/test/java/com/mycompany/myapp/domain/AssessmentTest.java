package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AssessmentTestSamples.*;
import static com.mycompany.myapp.domain.LessonTestSamples.*;
import static com.mycompany.myapp.domain.StudentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssessmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assessment.class);
        Assessment assessment1 = getAssessmentSample1();
        Assessment assessment2 = new Assessment();
        assertThat(assessment1).isNotEqualTo(assessment2);

        assessment2.setId(assessment1.getId());
        assertThat(assessment1).isEqualTo(assessment2);

        assessment2 = getAssessmentSample2();
        assertThat(assessment1).isNotEqualTo(assessment2);
    }

    @Test
    void studentTest() {
        Assessment assessment = getAssessmentRandomSampleGenerator();
        Student studentBack = getStudentRandomSampleGenerator();

        assessment.setStudent(studentBack);
        assertThat(assessment.getStudent()).isEqualTo(studentBack);

        assessment.student(null);
        assertThat(assessment.getStudent()).isNull();
    }

    @Test
    void lessonTest() {
        Assessment assessment = getAssessmentRandomSampleGenerator();
        Lesson lessonBack = getLessonRandomSampleGenerator();

        assessment.setLesson(lessonBack);
        assertThat(assessment.getLesson()).isEqualTo(lessonBack);

        assessment.lesson(null);
        assertThat(assessment.getLesson()).isNull();
    }
}
