#!/bin/bash
if [ ! -d ./deploy/postgres_data ]; then
    mkdir ./deploy/postgres_data
    mkdir ./deploy/postgres_data/init
    mkdir ./deploy/postgres_data/volume_db
fi

if [ ! -d ./deploy/postgres_data/init ]; then
    mkdir ./deploy/postgres_data/init
fi

if [ ! -d ./deploy/postgres_data/volume_db ]; then
    mkdir ./deploy/postgres_data/volume_db
fi

if [ ! -d ./deploy/app/artifact ]; then
    mkdir ./deploy/app/artifact
    cd rest_app
    mvn clean package
    cd .. && cp rest_app/target/rest_app-1.0.jar ./deploy/app/artifact/
fi

if [ ! -f ./deploy/app/artifact/rest_app-1.0.jar ]; then
    cd rest_app
    mvn clean package
    cd .. && cp rest_app/target/rest_app-1.0.jar ./deploy/app/artifact/
fi

sudo docker-compose -f ./deploy/docker-compose.yaml up -d