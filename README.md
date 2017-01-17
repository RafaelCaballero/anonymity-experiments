# anonymity-experiments
Experiments for checking anonymity algorithms for generating screening appointments

**Requisites**
[PostgreSQL] (https://www.postgresql.org/) 

**Install**
Just download the [jar file] (https://github.com/RafaelCaballero/anonymity-experiments/blob/master/anonymity.jar) 

The PostgreSQL instance must contain a table 'data' with format:

```SQL
CREATE TABLE cr_data (
    screening_date text,
    center_nr bigint,
    reg bigint,
    year character varying(4)
);
```
You can also import [this file] (https://github.com/RafaelCaballero/anonymity-experiments/blob/master/data.zip) exported
with pg_dump. The file is encrypted; the password is available upon request.

