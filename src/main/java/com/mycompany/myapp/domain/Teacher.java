package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Teacher.
 */
@Entity
@Table(name = "teacher")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Teacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(name = "fname", length = 255, nullable = false)
    private String fname;

    @NotNull
    @Size(max = 255)
    @Column(name = "mname", length = 255, nullable = false)
    private String mname;

    @Size(max = 255)
    @Column(name = "lname", length = 255)
    private String lname;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_teacher__discipline",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "discipline_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "teachers" }, allowSetters = true)
    private Set<Discipline> disciplines = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Teacher id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFname() {
        return this.fname;
    }

    public Teacher fname(String fname) {
        this.setFname(fname);
        return this;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return this.mname;
    }

    public Teacher mname(String mname) {
        this.setMname(mname);
        return this;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return this.lname;
    }

    public Teacher lname(String lname) {
        this.setLname(lname);
        return this;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Set<Discipline> getDisciplines() {
        return this.disciplines;
    }

    public void setDisciplines(Set<Discipline> disciplines) {
        this.disciplines = disciplines;
    }

    public Teacher disciplines(Set<Discipline> disciplines) {
        this.setDisciplines(disciplines);
        return this;
    }

    public Teacher addDiscipline(Discipline discipline) {
        this.disciplines.add(discipline);
        return this;
    }

    public Teacher removeDiscipline(Discipline discipline) {
        this.disciplines.remove(discipline);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Teacher)) {
            return false;
        }
        return getId() != null && getId().equals(((Teacher) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Teacher{" +
            "id=" + getId() +
            ", fname='" + getFname() + "'" +
            ", mname='" + getMname() + "'" +
            ", lname='" + getLname() + "'" +
            "}";
    }
}
