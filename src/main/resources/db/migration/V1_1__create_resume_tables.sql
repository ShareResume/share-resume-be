create table companies
(
    id       uuid primary key,
    name     varchar(500) not null,
    logo_url varchar(500)
);

create table resumes
(
    id                     uuid primary key,
    author_id              uuid         not null,
    is_hr_screening_passed boolean      not null,
    company_id             uuid         not null,
    created_at             timestamp    not null,
    years_of_experience    int          not null,
    speciality             varchar(255) not null,
    is_hidden              boolean      not null,
    foreign key (author_id) references users (id),
    foreign key (company_id) references companies (id)
);

create table documents
(
    id          uuid primary key,
    access_type varchar(255),
    resume_id   uuid         not null,
    name        varchar(500) not null,
    directory   varchar(500) not null
);