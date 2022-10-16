FROM postgres:14.5
COPY ./sql/create_user.sql /docker-entrypoint-initdb.d/
RUN localedef -i ru_RU -c -f UTF-8 -A /usr/share/locale/locale.alias ru_RU.UTF-8
ENV LANG "ru_RU.utf8"
ENV POSTGRES_PASSWORD "manager"
ENV POSTGRES_USER "manager"
ENV POSTGRES_DB "allidb"

