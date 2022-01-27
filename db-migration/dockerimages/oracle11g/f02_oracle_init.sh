# expdp frenchelectricity/oracle@research full=Y directory=oracle_dump dumpfile=frenchelectricity.dmp
# expdp frenchelectricity/oracle@research directory=oracle_dump dumpfile=frenchelectricity.dmp SCHEMAS=frenchelectricity
# expdp frenchelectricityext/oracle@research directory=oracle_dump dumpfile=frenchelectricityext.dmp SCHEMAS=frenchelectricityext
# expdp covid_weekly/oracle@research directory=oracle_dump dumpfile=covid_weekly.dmp SCHEMAS=covid_weekly
# expdp foodmart/oracle@research directory=oracle_dump dumpfile=foodmart.dmp SCHEMAS=foodmart
impdp covid_weekly/oracle@127.0.0.1:1521/xe directory=oracle_dump dumpfile=COVID_WEEKLY.DMP
impdp foodmart/oracle@127.0.0.1:1521/xe directory=oracle_dump dumpfile=FOODMART.DMP
impdp frenchelectricity/oracle@127.0.0.1:1521/xe directory=oracle_dump dumpfile=FRENCHELECTRICITY.DMP
impdp frenchelectricityext/oracle@127.0.0.1:1521/xe directory=oracle_dump dumpfile=FRENCHELECTRICITYEXT.DMP
