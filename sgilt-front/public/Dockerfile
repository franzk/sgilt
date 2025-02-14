# Utilisation de l'image officielle de Nginx
FROM nginx:latest

# Supprimer la configuration par défaut
RUN rm -rf /etc/nginx/conf.d/default.conf

# Copier la configuration personnalisée dans le bon dossier
COPY default.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers du build Vue.js dans le dossier Nginx
COPY . /usr/share/nginx/html

# Copier les certificats SSL dans le conteneur
COPY ssl /etc/nginx/ssl

# Exposer les ports 80 et 443
EXPOSE 80 443

# Lancer Nginx
CMD ["nginx", "-g", "daemon off;"]