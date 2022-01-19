#!/bin/bash
set -e
set -xo
if [ -f .env ]; then
  export $(cat .env | sed 's/#.*//g' | xargs)
fi

docker-compose down
docker-compose up --build -d --remove-orphans
