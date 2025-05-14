DROP TABLE IF EXISTS resumes_companies;

CREATE TABLE resumes_companies
(
    id                     UUID PRIMARY KEY,
    resume_id              UUID NOT NULL,
    company_id             UUID NOT NULL,
    is_hr_screening_passed BOOLEAN,
    CONSTRAINT fk_resumes_companies_resume FOREIGN KEY (resume_id) REFERENCES resumes (id),
    CONSTRAINT fk_resumes_companies_company FOREIGN KEY (company_id) REFERENCES companies (id)
);
