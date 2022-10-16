create table GAME (
      game_id     serial  primary key,
      lead_id     integer not null,
      chat_id     integer not null,
      last_ord    integer not null,
      active      boolean
);