docker run --name sgilt -p 80:80 -p 443:443 \
 -v $PWD/front:/usr/share/nginx/html \
 -v $PWD/nginx-conf/nginx.conf:/etc/nginx/conf.d/default.conf \
 -v $PWD/nginx-conf/ssl:/etc/nginx/ssl \
nginx