docker rm -f sgilt
docker rmi sgilt

docker build -t sgilt .
docker run -p 80:80 -p 443:443 --name sgilt sgilt
