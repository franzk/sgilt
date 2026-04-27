INSERT INTO utilisateurs (id, first_name, last_name, email, status, created_at)
VALUES ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Test', 'Prestataire', 'test-prestataire@sgilt.test', 'ACTIVE', NOW());

INSERT INTO prestataires (id, utilisateur_id, name, slug, created_at)
VALUES ('37d43573-8cd0-4eef-a838-91d9bb4f2323', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Prestataire Test', 'prestataire-test', NOW());
