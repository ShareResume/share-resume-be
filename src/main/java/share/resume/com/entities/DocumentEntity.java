package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.DocumentAccessTypeEnum;

import java.util.UUID;

@Getter
@Setter
@Table(name = "DOCUMENTS")
@Entity
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DocumentAccessTypeEnum accessType;

    @ManyToOne
    private ResumeEntity resume;
    private String name;
    private String directory;
}
