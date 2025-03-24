create table comments
(
    id                uuid primary key not null,
    resume_id         uuid             not null,
    parent_comment_id uuid,
    message           varchar(1000)    not null,
    reactions_rate    int              not null default 0,
    foreign key (resume_id) references resumes (id)
);
create table user_comment_vote_state
(
    id         uuid primary key not null,
    user_id    uuid             not null,
    comment_id uuid             not null,
    vote_state varchar(255)     not null,
    foreign key (user_id) references users (id),
    foreign key (comment_id) references comments (id)
);
create table users_favorites_resumes
(
    id        uuid not null,
    user_id   uuid not null,
    resume_id uuid not null,
    foreign key (user_id) references users (id),
    foreign key (resume_id) references resumes (id)
)