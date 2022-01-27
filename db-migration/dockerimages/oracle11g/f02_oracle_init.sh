# expdp frenchelectricity/oracle@research directory=oracle_dump dumpfile=frenchelectricity.dmp SCHEMAS=frenchelectricity
# expdp frenchelectricityext/oracle@research directory=oracle_dump dumpfile=frenchelectricityext.dmp SCHEMAS=frenchelectricityext
# expdp covid_weekly/oracle@research directory=oracle_dump dumpfile=covid_weekly.dmp SCHEMAS=covid_weekly
# expdp foodmart/oracle@research directory=oracle_dump dumpfile=foodmart.dmp SCHEMAS=foodmart
chmod -R 777 /data
ls -las /data
impdp covid_weekly/oracle@127.0.0.1:1521/xe DIRECTORY=oracle_dump DUMPFILE=COVID_WEEKLY.DMP SCHEMAS=covid_weekly
impdp foodmart/oracle@127.0.0.1:1521/xe DIRECTORY=oracle_dump DUMPFILE=FOODMART.DMP SCHEMAS=SCHEMAS
impdp frenchelectricity/oracle@127.0.0.1:1521/xe DIRECTORY=oracle_dump DUMPFILE=FRENCHELECTRICITY.DMP SCHEMAS=frenchelectricity
impdp frenchelectricityext/oracle@127.0.0.1:1521/xe DIRECTORY=oracle_dump DUMPFILE=FRENCHELECTRICITYEXT.DMP SCHEMAS=frenchelectricityext
