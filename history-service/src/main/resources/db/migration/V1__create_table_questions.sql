create table questions.questions
(
    id       bigint not null
        primary key,
    answer   varchar(255),
    question varchar(255)
);