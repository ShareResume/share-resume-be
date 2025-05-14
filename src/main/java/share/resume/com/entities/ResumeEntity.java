package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "RESUMES")
@Entity
public class ResumeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID")
    private UserEntity author;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "resume")
    private List<ResumesCompaniesEntity> resumesCompanies;

    private LocalDateTime createdAt;
    private Integer yearsOfExperience;

    @Enumerated(EnumType.STRING)
    private SpecialityEnum speciality;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "resume")
    private List<DocumentEntity> documents;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<CommentEntity> comments;
    private boolean isHidden;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status;

}
