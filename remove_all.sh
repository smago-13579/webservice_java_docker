#!/bin/bash
sudo docker-compose -f ./deploy/docker-compose.yaml down
sudo docker rmi -f $(sudo docker images -aq)
sudo rm -rf ./deploy/app/artifact
sudo rm -rf ./deploy/postgres_data
