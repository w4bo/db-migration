#!/bin/bash
set -e
set -xo
if [ -f .env ]; then
  export $(cat .env | sed 's/#.*//g' | xargs)
fi

docker-compose down
docker-compose up --build -d --remove-orphans

./wait-for-it.sh ${MYSQL_A_IP}:${MYSQL_A_PORT} --strict --timeout=100 -- echo "MySQL A is up"
./wait-for-it.sh ${MYSQL_A_IP}:${MYSQL_B_PORT} --strict --timeout=100 -- echo "MySQL B is up"
./wait-for-it.sh ${ORACLE_A_IP}:${ORACLE_A_PORT} --strict --timeout=100 -- echo "Oracle A is up"
