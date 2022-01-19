# DB Migration

[![build](https://github.com/w4bo/db-migration/actions/workflows/build.yml/badge.svg)](https://github.com/w4bo/db-migration/actions/workflows/build.yml)

Moving data from/to databases.

## Running the migration

- Edit the `.env` file
- Run `./gradlew runMigration`

## Supported migrations

| From | To |
| - | - | 
| Oracle | MySQL |
| MySQL | Oracle |
| MySQL | MySQL |

Supported versions:

- Oracle: 11g
- MySQL: 5.7
