create table WORD (
    word_id     serial  primary key,
    word_text   varchar(512) not null,
    word_ord    integer not null
);