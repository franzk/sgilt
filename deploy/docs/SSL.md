# SGILT — SSL Certificates

SGILT uses Cloudflare Origin Certificates for SSL termination.
These certificates are free, valid for 15 years, and trusted by Cloudflare in Full (strict) mode.

> Important: Cloudflare Origin Certificates are only trusted by Cloudflare.
> They will not work if you bypass Cloudflare and connect directly to the server.
> This is the expected behavior — the server should only receive traffic from Cloudflare.

---

## Certificate coverage

| Domain | Certificate |
|---|---|
| `staging.sgilt.fr`, `auth-staging.sgilt.fr` | `*.sgilt.fr` Origin Certificate |
| `sgilt.alsace`, `auth.sgilt.alsace` | `*.sgilt.alsace` Origin Certificate |

One wildcard certificate covers all subdomains of a given domain.

---

## Generating a certificate

### 1. Go to Cloudflare

In the Cloudflare dashboard for the target domain:
**SSL/TLS → Origin Server → Create Certificate**

### 2. Configure the certificate

- **Private key type**: RSA (2048)
- **Hostnames**:
    - `*.sgilt.fr`
    - `sgilt.fr`
      (or `*.sgilt.alsace` and `sgilt.alsace` for production)
- **Certificate validity**: 15 years

Click **Create**.

### 3. Save the files

Cloudflare displays the certificate and private key **only once**.
Copy them immediately:

- Certificate → save as `origin.pem`
- Private key → save as `origin.key`

---

## Installing on the server

### 1. Create the certificate directory

```bash
sudo mkdir -p /etc/ssl/sgilt
```

### 2. Copy the files

```bash
sudo nano /etc/ssl/sgilt/origin.pem   # paste the certificate
sudo nano /etc/ssl/sgilt/origin.key   # paste the private key
```

### 3. Set permissions

```bash
sudo chmod 644 /etc/ssl/sgilt/origin.pem
sudo chmod 644 /etc/ssl/sgilt/origin.key
sudo chown -R deployer:deployer /etc/ssl/sgilt
```

### 4. Verify

Check that the certificate covers the expected domains:

```bash
openssl x509 -in /etc/ssl/sgilt/origin.pem -text -noout | grep "DNS:"
```

Expected output:
```
DNS:*.sgilt.fr, DNS:sgilt.fr
```

Check that the certificate and key match:

```bash
openssl x509 -noout -modulus -in /etc/ssl/sgilt/origin.pem | md5sum
openssl rsa -noout -modulus -in /etc/ssl/sgilt/origin.key | md5sum
```

Both md5 hashes must be identical.

---

## nginx configuration

The nginx templates mount the certificate directory as a read-only volume
and reference the files in the server block:

```nginx
server {
    listen 443 ssl;

    ssl_certificate /etc/ssl/sgilt/origin.pem;
    ssl_certificate_key /etc/ssl/sgilt/origin.key;

    ...
}
```

The volume is declared in the docker-compose files:

```yaml
nginx-front:
    volumes:
      - /etc/ssl/sgilt:/etc/ssl/sgilt:ro
```

---

## Cloudflare SSL mode

Set SSL mode to **Full (strict)** in Cloudflare:
**SSL/TLS → Overview → Configure → Custom SSL/TLS → Full (strict)**

This ensures Cloudflare validates the Origin Certificate before forwarding traffic.

---

## Certificate renewal

Cloudflare Origin Certificates are valid for 15 years.
No automatic renewal is needed.

When a certificate expires or needs to be rotated:
1. Generate a new certificate in Cloudflare (same procedure as above)
2. Replace the files on the server
3. Restart the nginx containers:

```bash
docker restart nginx-front-staging nginx-auth-staging
```