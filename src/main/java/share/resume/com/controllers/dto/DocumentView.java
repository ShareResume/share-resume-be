package share.resume.com.controllers.dto;

import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.DocumentAccessTypeEnum;

@Getter
@Setter
public class DocumentView {
    private DocumentAccessTypeEnum accessType;
    private String url;
    private String name;
}
