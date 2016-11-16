# anonymity-experiments
Experiments for checking anonymity algorithms for generating screening appointments

**Requisites**
[PostgreSQL] (https://www.postgresql.org/) 

**Install**
Just download the [jar file] (https://github.com/RafaelCaballero/anonymity-experiments/blob/master/anonymity.jar) 

The PostgreSQL instance must contain a table cr_data

```SQL
CREATE TABLE cr_data (
    index bigint,
    "PID" bigint,
    diagnosisdate text,
    lab_nr bigint,
    reg bigint,
    year character varying(4)
);
```
You can also import [this file] (https://github.com/RafaelCaballero/anonymity-experiments/blob/master/cr_data.zip) exported
with pg_dump. The file is encrypted; the password is available upon request.

