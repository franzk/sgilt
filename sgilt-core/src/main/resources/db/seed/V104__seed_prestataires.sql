-- V104 : Seed des prestataires de démonstration (15 prestataires).
-- Exécuté uniquement sur les profils `local` et `staging` via spring.flyway.locations.
--
-- Remplace V103, devenu incompatible avec le schéma introduit par V16 (colonnes
-- `logistics`/`technical` remplacées par `details` JSONB unifiée). Même raison
-- que le squash V101/V102 → V103 en son temps : on ne peut pas éditer un
-- fichier de migration déjà appliqué sans casser son checksum Flyway sur les
-- environnements où il a déjà tourné. V103 a donc été supprimé plutôt qu'édité ;
-- `spring.flyway.ignore-migration-patterns=*:missing` permet à Flyway d'ignorer
-- son absence sur ces environnements.
--
-- Les INSERT utilisent `ON CONFLICT (id) DO UPDATE` (upsert) plutôt qu'un
-- DELETE préalable : une ancienne exécution de ce seed (avec l'ancien
-- `details` dérivé de logistics/technical) est ainsi mise à jour en place.
-- Purger la table `prestataires` casserait la FK `reservations_prestataire_id_fkey`
-- dès qu'une réservation (réelle sur staging, ou de test en local) référence un
-- prestataire de démo — l'upsert évite ce cas sans dépendre de l'absence de
-- données liées. Portée strictement limitée aux UUIDs de démo
-- (dd000001...dd00000f / cc000001...cc00000f) — aucune donnée réelle n'est
-- concernée.
--
-- `details` est renseigné nativement par catégorie (FORMAT, PROCESS, LOGISTICS,
-- DELIVERABLE, OTHER) pour chaque prestataire, à partir du contenu déjà présent
-- dans `offerings`/`identity`/`testimonials` — ce n'est plus une reprise
-- mécanique d'anciennes colonnes `logistics`/`technical` (cf. V16 pour cette
-- logique de reprise, qui ne s'applique qu'aux données de production réelles).
--
-- UUIDs de référence :
--   utilisateurs  : cc000001...cc00000f
--   prestataires  : dd000001...dd00000f
--
-- Les champs JSONB utilisent le dollar-quoting ($$...$$).
-- Le champ `budget` est stocké en JSON string ("texte").
-- Le champ `medias` est un tableau ordonné d'objets { type: IMAGE|YOUTUBE, ref, position }.
-- Le champ `details` est un tableau d'objets { content, category }.

-- ─── 1. Utilisateurs ──────────────────────────────────────────────────────────

INSERT INTO utilisateurs (id, first_name, last_name, email, status, created_at) VALUES
                                                                                    ('cc000001-0000-0000-0000-000000000000', 'Jean-Michel', 'Dupré',    'jean-michel.dupre@sgilt.dev',    'ACTIVE', NOW()),
                                                                                    ('cc000002-0000-0000-0000-000000000000', 'Sophie',      'Marchand', 'sophie.marchand@sgilt.dev',      'ACTIVE', NOW()),
                                                                                    ('cc000003-0000-0000-0000-000000000000', 'Thomas',      'Rousseau', 'thomas.rousseau@sgilt.dev',      'ACTIVE', NOW()),
                                                                                    ('cc000004-0000-0000-0000-000000000000', 'Marc',        'Dumas',    'marc.dumas@sgilt.dev',           'ACTIVE', NOW()),
                                                                                    ('cc000005-0000-0000-0000-000000000000', 'Claire',      'Fontaine', 'claire.fontaine@sgilt.dev',      'ACTIVE', NOW()),
                                                                                    ('cc000006-0000-0000-0000-000000000000', 'Julien',      'Bernard',  'julien.bernard@sgilt.dev',       'ACTIVE', NOW()),
                                                                                    ('cc000007-0000-0000-0000-000000000000', 'Emma',        'Lefebvre', 'emma.lefebvre@sgilt.dev',        'ACTIVE', NOW()),
                                                                                    ('cc000008-0000-0000-0000-000000000000', 'Pierre',      'Moreau',   'pierre.moreau@sgilt.dev',        'ACTIVE', NOW()),
                                                                                    ('cc000009-0000-0000-0000-000000000000', 'Antoine',     'Martin',   'antoine.martin@sgilt.dev',       'ACTIVE', NOW()),
                                                                                    ('cc00000a-0000-0000-0000-000000000000', 'Léo',         'Clairmont','leo.clairmont@sgilt.dev',        'ACTIVE', NOW()),
                                                                                    ('cc00000b-0000-0000-0000-000000000000', 'Nina',        'Petit',    'nina.petit@sgilt.dev',           'ACTIVE', NOW()),
                                                                                    ('cc00000c-0000-0000-0000-000000000000', 'David',       'Lambert',  'david.lambert@sgilt.dev',        'ACTIVE', NOW()),
                                                                                    ('cc00000d-0000-0000-0000-000000000000', 'René',        'Baumann',  'rene.baumann@sgilt.dev',         'ACTIVE', NOW()),
                                                                                    ('cc00000e-0000-0000-0000-000000000000', 'Isabelle',    'Castille', 'isabelle.castille@sgilt.dev',    'ACTIVE', NOW()),
                                                                                    ('cc00000f-0000-0000-0000-000000000000', 'François',    'Meyer',    'francois.meyer@sgilt.dev',       'ACTIVE', NOW())
    ON CONFLICT (id) DO NOTHING;

-- ─── 2. Prestataires ──────────────────────────────────────────────────────────

-- ── Musique ───────────────────────────────────────────────────────────────────

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000001-0000-0000-0000-000000000000',
           'cc000001-0000-0000-0000-000000000000',
           'DJ Animation',
           'dj-animation',
           'Votre soirée mérite une bande-son sur mesure.',
           'Un DJ professionnel pour rythmer vos soirées.',
           'musique',
           $$[{"type":"IMAGE","ref":"uploads/dj-animation.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/dj1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/dj2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/dj3/800/600","position":3},{"type":"YOUTUBE","ref":"zznewKVtKtk","position":4}]$$,
           $$["REPONSE_48H","ADAPTABLE","ACCOMPAGNEMENT"]$$,
           $$["Animation DJ pour mariages et soirées privées","Sonorisation complète incluse (jusqu'à 200 personnes)","Éclairage d'ambiance","Formule duo avec animateur micro sur demande","Set DJ live ou playlist curatée selon vos préférences"]$$,
           $${"quote":"Chaque soirée est unique","bio":"Passionné de musique depuis plus de 15 ans, je mets autant de soin dans le choix d'un morceau que dans l'écoute de ce que vous attendez."}$$,
           $$"Tarif selon durée et configuration. Devis personnalisé sur demande."$$,
           $$[{"author":"Sophie & Marc","text":"La piste de danse n'a pas désempli de la soirée. Un DJ à l'écoute, des transitions impeccables."},{"author":"Thomas D.","text":"On lui a donné carte blanche et il a su lire la salle parfaitement. Exactement ce qu'on voulait."}]$$,
           $$[{"content":"Set DJ 5h : Formule standard : DJ pendant 5h avec sonorisation et éclairage inclus, extensible sur devis.","category":"FORMAT"},{"content":"Formule duo micro : Option avec un animateur micro en complément du DJ, pour les moments clés (discours, jeux, annonces).","category":"FORMAT"},{"content":"Construction de la playlist : Échange préalable pour établir la liste des titres souhaités et ceux à éviter.","category":"PROCESS"},{"content":"Lecture de salle en direct : Le déroulé musical s'ajuste en soirée selon l'ambiance et les retours des invités.","category":"PROCESS"},{"content":"Sonorisation incluse : Système de diffusion adapté jusqu'à 200 personnes, compris dans le tarif.","category":"DELIVERABLE"},{"content":"Éclairage d'ambiance : Jeu de lumières LED fourni par défaut ; micro sans fil disponible sur demande.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et départements limitrophes.","category":"LOGISTICS"},{"content":"Installation : Montage 1h30 avant le début, espace de 4m² minimum avec accès à une prise 220V.","category":"LOGISTICS"}]$$,
           $$[{"question":"Est-ce que je peux soumettre des morceaux à l'avance ?","answer":"Oui, un échange est prévu avant la prestation pour construire ensemble une liste de souhaits et de morceaux à éviter."},{"question":"La sono est-elle incluse dans le tarif ?","answer":"Oui, la sonorisation jusqu'à 200 personnes est incluse. Pour des jauges plus importantes, un devis spécifique est établi."},{"question":"Que se passe-t-il en cas d'imprévu ou d'annulation ?","answer":"Les conditions d'annulation sont précisées dans le contrat. En cas d'imprévu de ma part, je m'engage à proposer un remplaçant de confiance."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000002-0000-0000-0000-000000000000',
           'cc000002-0000-0000-0000-000000000000',
           'Gypsy Reed Ensemble',
           'gypsy-reed-ensemble',
           'Le jazz manouche qui fait danser les âmes.',
           'Un groupe de jazz parfait pour animer vos soirées.',
           'musique',
           $$[{"type":"IMAGE","ref":"uploads/jazz-band.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/jazz1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/jazz2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/jazz3/800/600","position":3},{"type":"YOUTUBE","ref":"_A6w3ECkN4k","position":4}]$$,
           $$["EQUIPE","INTERLOCUTEUR_UNIQUE","ADAPTABLE"]$$,
           $$["Quartet jazz manouche (guitare, contrebasse, violon, guitare rythmique)","Formule trio disponible pour les petits espaces","Répertoire : Reinhardt, Grappelli, standards jazz et bossa","Musique de fond pour cocktail ou dîner","Set dansant en soirée sur demande","Sonorisation légère incluse pour les formules sonorisées"]$$,
           $${"quote":"Quatre musiciens, une passion commune : la musique qui voyage.","bio":"On joue ensemble depuis 2018, et chaque concert est une conversation entre nos instruments. On aime autant jouer dans un château alsacien que dans une grange rénovée."}$$,
           $$"À partir de 800€ pour une formule cocktail (2h). Tarif dégressif selon la durée. Devis sur demande."$$,
           $$[{"author":"Claire & Antoine","text":"Nos invités en parlent encore. Une ambiance incroyable du cocktail jusqu'au dessert, exactement ce qu'on cherchait."}]$$,
           $$[{"content":"Formule quartet : Guitare, contrebasse, violon et guitare rythmique ; formule trio possible pour les petits espaces.","category":"FORMAT"},{"content":"Répertoire : Standards de jazz manouche (Reinhardt, Grappelli) et bossa, en musique de fond ou set dansant.","category":"FORMAT"},{"content":"Choix des morceaux : Échange par email après réservation pour discuter des titres souhaités, dans la limite du répertoire.","category":"PROCESS"},{"content":"Sonorisation légère : Disponible en option pour les formules amplifiées, avec micros de scène.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et Grand Est, déplacement possible au-delà sur devis.","category":"LOGISTICS"},{"content":"Installation : 45 minutes avant la prestation, espace de 6m² minimum pour le quartet.","category":"LOGISTICS"},{"content":"Besoin électrique : Formule acoustique : aucun besoin électrique. Formule sonorisée : alimentation 220V nécessaire.","category":"LOGISTICS"},{"content":"Jeu en extérieur : Possible si le sol est stable ; un espace couvert est requis en cas de pluie.","category":"OTHER"}]$$,
           $$[{"question":"Peut-on demander des morceaux spécifiques ?","answer":"Oui, nous acceptons les demandes en amont dans la limite de notre répertoire. Un échange par email est prévu après la réservation pour en discuter."},{"question":"Jouez-vous en extérieur ?","answer":"Oui, à condition que le sol soit stable et qu'un espace couvert soit disponible en cas de pluie. La formule acoustique est particulièrement adaptée aux extérieurs."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000003-0000-0000-0000-000000000000',
           'cc000003-0000-0000-0000-000000000000',
           'Starlight Pulse',
           'starlight-pulse',
           'Pop, rock, dancefloor — une énergie qui ne retombe pas.',
           'Un groupe pop-rock énergique pour une ambiance inoubliable.',
           'musique',
           $$[{"type":"IMAGE","ref":"uploads/pop-rock-band.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/poprock1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/poprock2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/poprock3/800/600","position":3},{"type":"YOUTUBE","ref":"4sErhkkcOUU","position":4}]$$,
           $$["REPONSE_48H","ACCOMPAGNEMENT","ECORESPONSABLE"]$$,
           $$["Groupe de 4 musiciens (guitare, basse, batterie, clavier)","Deux chanteurs / chanteuses","Répertoire pop-rock français et international des années 80 à aujourd'hui","Formule concert (2 sets de 45min) ou animation continue","Karaoké animé en option"]$$,
           $${"quote":"On est là pour que personne ne reste assis.","bio":"Depuis 2015, on parcourt les salles alsaciennes avec une seule obsession : que vous repartiez avec des crampes aux jambes."}$$,
           $$"Formule soirée complète à partir de 1 800€. Devis sur demande selon durée et configuration."$$,
           $$[{"author":"Julie & Romain","text":"Tout le monde dansait, même les grands-parents. Un groupe incroyable, pro du début à la fin."},{"author":"Comité des fêtes de Molsheim","text":"Troisième fois qu'on les fait venir. La salle est toujours pleine et le public repart ravi."}]$$,
           $$[{"content":"Formule concert : Deux sets de 45 minutes, ou animation continue de 3h sur demande.","category":"FORMAT"},{"content":"Composition du groupe : Quatre musiciens (guitare, basse, batterie, clavier) et deux chanteurs.","category":"FORMAT"},{"content":"Surprise musicale : Sur demande : chanson dédiée, entrée personnalisée, moment scénarisé avec le groupe.","category":"PROCESS"},{"content":"Backline et éclairage inclus : Amplis, batterie et jeux de lumière scéniques fournis, sonorisation jusqu'à 300 personnes.","category":"DELIVERABLE"},{"content":"Karaoké animé : Option disponible en fin de soirée.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et Grand Est.","category":"LOGISTICS"},{"content":"Installation : 2h avant le début, scène de 12m² minimum, deux prises 16A nécessaires.","category":"LOGISTICS"}]$$,
           $$[{"question":"Combien de temps dure une prestation type ?","answer":"Une soirée classique comprend 2 sets de 45 minutes avec une pause. Une animation continue de 3h est aussi possible sur demande."},{"question":"Peut-on avoir une surprise musicale ?","answer":"Oui, on adore les moments surprise — chanson dédiée, entrée des mariés personnalisée. Dites-nous ce que vous avez en tête."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000004-0000-0000-0000-000000000000',
           'cc000004-0000-0000-0000-000000000000',
           'The Jive Rebels',
           'the-jive-rebels',
           'Du rock qui transpire, des riffs qui restent.',
           'Un groupe rock pour faire vibrer votre public.',
           'musique',
           $$[{"type":"IMAGE","ref":"uploads/rock-band.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/rock1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/rock2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/rock3/800/600","position":3},{"type":"YOUTUBE","ref":"BOCaSJOARFM","position":4}]$$,
           $$["INTERLOCUTEUR_UNIQUE","EQUIPE"]$$,
           $$["Trio ou quartet rock (guitare, basse, batterie + chant)","Répertoire : classic rock, hard rock, grunge des années 70 à 2000","2 sets de 45 minutes ou set unique de 90 minutes","Sonorisation incluse"]$$,
           $${"quote":"Juste du rock honnête pour un public qui aime ça.","bio":"On joue du rock parce que rien d'autre ne nous convient. Pas de compromis sur le son, pas de titre trop lisse."}$$,
           $$"À partir de 1 200€ pour une soirée. Devis sur demande."$$,
           $$[{"author":"Marc D.","text":"Une énergie de dingue. Le public n'en revenait pas — et pourtant c'était une salle de mairie !"}]$$,
           $$[{"content":"Formation : Trio ou quartet (guitare, basse, batterie, chant), répertoire rock classique aux années 2000.","category":"FORMAT"},{"content":"Durée : Deux sets de 45 minutes ou un set unique de 90 minutes.","category":"FORMAT"},{"content":"Sonorisation et éclairage inclus : Backline complet et système Yamaha jusqu'à 250 personnes fournis.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et régions limitrophes.","category":"LOGISTICS"},{"content":"Installation : 2h avant le début, scène de 10m² minimum, deux prises 16A.","category":"LOGISTICS"},{"content":"Prestation en extérieur : Possible avec un espace couvert pour le matériel et une surface stable pour la batterie.","category":"OTHER"}]$$,
           $$[{"question":"Jouez-vous en extérieur ?","answer":"Oui, avec un espace couvert pour le matériel en cas de pluie et une surface stable pour la batterie."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

-- ── Restauration ──────────────────────────────────────────────────────────────

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000005-0000-0000-0000-000000000000',
           'cc000005-0000-0000-0000-000000000000',
           'Éclat Gourmet',
           'eclat-gourmet',
           'Une cuisine raffinée qui sublime chaque moment.',
           'Un service traiteur haut de gamme pour vos événements.',
           'restauration',
           $$[{"type":"IMAGE","ref":"uploads/traiteur-gourmet.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/catering1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/catering2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/catering3/800/600","position":3},{"type":"YOUTUBE","ref":"6D4_3NIpvkI","position":4}]$$,
           $$["ADAPTABLE","ACCOMPAGNEMENT","ECORESPONSABLE","REPONSE_48H"]$$,
           $$["Cocktail dînatoire ou repas assis (de 20 à 300 personnes)","Buffet froid ou chaud","Service à table avec personnel inclus","Vaisselle et nappage fournis sur demande","Formule fromages et desserts alsaciens disponible"]$$,
           $${"quote":"On cuisine comme on reçoit.","bio":"Avec soin, sans se prendre trop au sérieux. Ce qui compte, c'est que vos invités se régalent et que vous profitiez de votre fête."}$$,
           $$"À partir de 45€ par personne pour un cocktail dînatoire. Devis sur demande selon formule et nombre de convives."$$,
           $$[{"author":"Famille Schneider","text":"Tous nos invités nous ont demandé le contact du traiteur. Les plats étaient beaux et délicieux."},{"author":"Entreprise Strasbourgeoise","text":"Impeccable pour notre séminaire. Ponctuel, professionnel, et les retours des collaborateurs ont été unanimes."}]$$,
           $$[{"content":"Formules : Cocktail dînatoire ou repas assis, de 20 à 300 personnes.","category":"FORMAT"},{"content":"Régimes alimentaires : Options végétariennes et sans gluten proposées systématiquement dès la conception du menu.","category":"FORMAT"},{"content":"Construction du menu : Le menu est ajusté avec le client selon la formule choisie et les contraintes alimentaires des invités.","category":"PROCESS"},{"content":"Service inclus : Personnel de service disponible (1 serveur pour 20 personnes), vaisselle et nappage en option.","category":"DELIVERABLE"},{"content":"Spécialités : Formule fromages et desserts alsaciens disponible.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace.","category":"LOGISTICS"},{"content":"Installation : 2h avant le service ; cuisine sur place possible si l'équipement le permet, sinon livraison en liaison froide.","category":"LOGISTICS"}]$$,
           $$[{"question":"Proposez-vous des options végétariennes ou sans gluten ?","answer":"Oui, systématiquement. Tous les régimes alimentaires sont pris en compte dès la conception du menu."},{"question":"Jusqu'à combien de personnes pouvez-vous intervenir ?","answer":"Nous gérons des événements de 20 à 300 personnes. Au-delà, nous pouvons étudier la demande selon le format."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000006-0000-0000-0000-000000000000',
           'cc000006-0000-0000-0000-000000000000',
           'Food Truck Burgers',
           'food-truck-burgers',
           'Des burgers qui méritent le détour, livrés à votre événement.',
           'Des burgers gourmands pour régaler vos invités.',
           'restauration',
           $$[{"type":"IMAGE","ref":"uploads/food-truck-burgers.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/burger1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/burger2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/burger3/800/600","position":3},{"type":"YOUTUBE","ref":"z2xjErKDd-o","position":4}]$$,
           $$["REPONSE_48H","EQUIPE"]$$,
           $$["Service au camion pour événements privés ou publics","Carte de 4 à 6 burgers selon formule","Options végétariennes et sans gluten disponibles","Frites fraîches maison","Boissons et desserts sur demande"]$$,
           $${"quote":"Chaque burger est une fierté.","bio":"Le food truck, c'est nous depuis 2017. On a commencé sur les marchés alsaciens, et on ne s'est jamais lassés."}$$,
           $$"Forfait événement à partir de 600€ pour 50 personnes. Tarif au convive possible selon format."$$,
           $$[{"author":"Lucas M.","text":"Le meilleur burger que j'ai mangé de ma vie, et dans le jardin de mes parents en plus. Tout le monde a adoré."}]$$,
           $$[{"content":"Carte : 4 à 6 burgers selon la formule, options végétariennes et sans gluten disponibles.","category":"FORMAT"},{"content":"Frites maison : Frites fraîches préparées sur place, boissons et desserts en option.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et départements limitrophes.","category":"LOGISTICS"},{"content":"Installation : Arrivée 1h avant le service, surface plane requise pour le stationnement du camion.","category":"LOGISTICS"},{"content":"Alimentation : Branchement 220V ou groupe électrogène fourni par le camion.","category":"LOGISTICS"},{"content":"Cadence de service : Environ 80 à 100 couverts par heure ; un second camion peut être mobilisé pour les grands événements.","category":"OTHER"}]$$,
           $$[{"question":"Combien de burgers par heure le camion peut-il servir ?","answer":"Environ 80 à 100 couverts par heure selon la formule. Pour les grands événements, un second camion peut être mobilisé."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000007-0000-0000-0000-000000000000',
           'cc000007-0000-0000-0000-000000000000',
           'Bar à Cocktails',
           'bar-a-cocktails',
           'Des cocktails qui racontent une histoire, à votre santé.',
           'Un bar à cocktails pour une expérience unique.',
           'restauration',
           $$[{"type":"IMAGE","ref":"uploads/bar-cocktails.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/cocktail1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/cocktail2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/cocktail3/800/600","position":3},{"type":"YOUTUBE","ref":"EFuBvEt84OI","position":4}]$$,
           $$["INTERLOCUTEUR_UNIQUE","ADAPTABLE","ACCOMPAGNEMENT"]$$,
           $$["Bar mobile pour événements privés et professionnels","Carte de cocktails classiques et signatures","Mocktails et boissons sans alcool","Animations bar : cours de cocktail, show flair sur demande","Glace, garnitures et matériel fournis"]$$,
           $${"quote":"Chaque cocktail est une histoire à raconter.","bio":"On ne verse pas juste un liquide — on crée un moment. Notre bar s'adapte à votre décor et à votre ambiance."}$$,
           $$"Forfait à partir de 500€ pour 50 personnes (2h de service). Tarif à la consommation possible."$$,
           $$[{"author":"Amélie R.","text":"Le cocktail signature qu'ils ont créé pour notre mariage était parfait. Les invités en ont reparlé toute la soirée."}]$$,
           $$[{"content":"Formules : Forfait à la personne ou service à la consommation, avec ou sans animation.","category":"FORMAT"},{"content":"Carte de cocktails : Cocktails classiques et signatures, mocktails et boissons sans alcool inclus.","category":"DELIVERABLE"},{"content":"Animations bar : Cours de cocktail ou show flair disponibles sur demande.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace.","category":"LOGISTICS"},{"content":"Installation : 1h avant le service, espace de 4m² minimum avec accès 220V.","category":"LOGISTICS"},{"content":"Formule open bar : Disponible en alternative au tarif à la consommation, selon budget.","category":"OTHER"}]$$,
           $$[{"question":"Peut-on avoir un bar ouvert toute la soirée ?","answer":"Oui, on propose des formules open bar ou à la consommation selon votre préférence et votre budget."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000008-0000-0000-0000-000000000000',
           'cc000008-0000-0000-0000-000000000000',
           'Buffet Cuisine Française',
           'buffet-cuisine-francaise',
           'La générosité du buffet, la finesse de la cuisine française.',
           'Un buffet raffiné pour sublimer vos réceptions.',
           'restauration',
           $$[{"type":"IMAGE","ref":"uploads/cuisine-francaise.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/french1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/french2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/french3/800/600","position":3},{"type":"YOUTUBE","ref":"30dp3iP66Gs","position":4}]$$,
           $$["ECORESPONSABLE","EQUIPE","REPONSE_48H"]$$,
           $$["Buffet froid ou mixte (chaud/froid)","Entrées, plats, fromages, desserts — formule complète ou à la carte","Spécialités alsaciennes disponibles","Service avec ou sans personnel","Vaisselle et nappage fournis en option"]$$,
           $${"quote":"Pas de fioritures, juste du bon.","bio":"Notre cuisine est celle qu'on aimerait manger chez des amis : généreuse, vraie, faite avec du temps."}$$,
           $$"À partir de 35€ par personne. Devis personnalisé selon formule et nombre."$$,
           $$[{"author":"Association Sportive Obernai","text":"Pour notre gala annuel, le buffet était à la hauteur. Copieux, savoureux, et le personnel très agréable."}]$$,
           $$[{"content":"Formules : Buffet froid ou mixte (chaud/froid), à la carte ou en formule complète (entrées, plats, fromages, desserts).","category":"FORMAT"},{"content":"Spécialités alsaciennes : Choucroute, baeckeoffe, tarte flambée et kougelhopf disponibles sur demande.","category":"DELIVERABLE"},{"content":"Service : Personnel de service en option ; vaisselle et nappage fournis sur demande.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace.","category":"LOGISTICS"},{"content":"Installation : Livraison et installation 1h30 avant le service ; cuisine sur place possible selon équipement disponible.","category":"LOGISTICS"}]$$,
           $$[{"question":"Peut-on intégrer des spécialités alsaciennes au menu ?","answer":"Absolument. Choucroute, baeckeoffe, tarte flambée, kougelhopf — on adore mettre la région à l'honneur."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd000009-0000-0000-0000-000000000000',
           'cc000009-0000-0000-0000-000000000000',
           'Camion Pizza',
           'camion-pizza',
           'Le four à bois se déplace, la vraie pizza aussi.',
           'Une délicieuse pizza cuite au feu de bois sur place.',
           'restauration',
           $$[{"type":"IMAGE","ref":"uploads/pizza-truck.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/pizza1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/pizza2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/pizza3/800/600","position":3},{"type":"YOUTUBE","ref":"HOZs4hWPiKk","position":4}]$$,
           $$["ADAPTABLE","INTERLOCUTEUR_UNIQUE"]$$,
           $$["Service pizza au feu de bois pour événements privés","Carte de 6 à 8 pizzas classiques et spéciales","Options végétariennes disponibles","Pizza blanche (sans tomate) disponible","Service continu ou par vagues selon format"]$$,
           $${"quote":"Un voyage en Italie.","bio":"On est tombés amoureux de la pizza napolitaine lors d'un voyage en Italie. Depuis 2019, on partage cette passion en Alsace avec un four qu'on a ramené de là-bas."}$$,
           $$"Forfait événement à partir de 550€ pour 50 personnes."$$,
           $$[{"author":"Théo & Camille","text":"Les pizzas étaient incroyables et le spectacle du four à bois a fasciné tout le monde, même les enfants."}]$$,
           $$[{"content":"Carte : 6 à 8 pizzas classiques et spéciales, dont une option sans tomate, options végétariennes disponibles.","category":"FORMAT"},{"content":"Service : Service continu ou par vagues selon le format de l'événement.","category":"PROCESS"},{"content":"Four à bois embarqué : Cuisson à 400°C, autonomie complète en bois et matériel, sans branchement nécessaire.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et Grand Est.","category":"LOGISTICS"},{"content":"Installation : Surface plane et accessible (largeur minimum 2,5m), distance de sécurité de 3m autour du four.","category":"LOGISTICS"},{"content":"Cadence de cuisson : 80 à 120 pizzas par heure selon la formule.","category":"OTHER"}]$$,
           $$[{"question":"Le camion peut-il venir dans un jardin privé ?","answer":"Oui, si l'accès est suffisant (largeur min. 2,5m) et que le sol est stable. On vérifie ça ensemble en amont."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

-- ── Photo ─────────────────────────────────────────────────────────────────────

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd00000a-0000-0000-0000-000000000000',
           'cc00000a-0000-0000-0000-000000000000',
           'Léo Clairmont Photographe',
           'leo-clairmont-photographe',
           'Des images qui gardent ce que la mémoire oublie.',
           'Des souvenirs inoubliables pour votre grand jour.',
           'photo',
           $$[{"type":"IMAGE","ref":"uploads/photographe-mariage.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/wedding1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/wedding2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/wedding3/800/600","position":3},{"type":"YOUTUBE","ref":"wlPFNYKkGSA","position":4}]$$,
           $$["REPONSE_48H","ACCOMPAGNEMENT","INTERLOCUTEUR_UNIQUE","ADAPTABLE"]$$,
           $$["Reportage photo mariage (journée complète ou demi-journée)","Séance couple avant le mariage incluse dans la formule complète","Photos de famille et portraits","Reportage événementiel (anniversaire, baptême, séminaire)","Tirage et album photo disponibles en option"]$$,
           $${"quote":"Saisir l'instant présent","bio":"Je photographie des gens, pas des poses. Mon travail, c'est d'être là au bon moment — discret, patient, attentif. Les photos que je préfère sont celles où personne ne regardait l'objectif."}$$,
           $$"Formule mariage à partir de 1 400€ (demi-journée). Journée complète sur devis."$$,
           $$[{"author":"Emma & Louis","text":"On a pleuré en découvrant les photos. Léo a capté des moments qu'on n'avait même pas vus sur le moment."},{"author":"Sarah M.","text":"Séance famille absolument parfaite. Les enfants l'ont adoré et les photos sont magnifiques."}]$$,
           $$[{"content":"Formules : Reportage mariage en demi-journée ou journée complète, avec séance couple incluse en formule complète.","category":"FORMAT"},{"content":"Autres reportages : Anniversaires, baptêmes, séminaires et portraits de famille.","category":"FORMAT"},{"content":"Repérage : Visite des lieux proposée en amont du jour J pour préparer les prises de vue.","category":"PROCESS"},{"content":"Livraison photo : Entre 400 et 600 photos retouchées livrées en JPEG haute résolution pour une journée complète ; les fichiers RAW ne sont pas livrés.","category":"DELIVERABLE"},{"content":"Tirage et album : Disponibles en option.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace, déplacement possible en France sur devis.","category":"LOGISTICS"},{"content":"Second photographe : Disponible en option pour les grands événements.","category":"LOGISTICS"}]$$,
           $$[{"question":"Combien de photos livrez-vous ?","answer":"Pour un mariage journée complète, entre 400 et 600 photos retouchées. Toutes les photos sélectionnées sont livrées, sans restriction."},{"question":"Peut-on avoir les photos brutes (RAW) ?","answer":"Je livre uniquement les fichiers retouchés en JPEG haute résolution. Les RAW ne font pas partie de la prestation."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd00000b-0000-0000-0000-000000000000',
           'cc00000b-0000-0000-0000-000000000000',
           'Photobooth Vintage',
           'photobooth-vintage',
           'Des souvenirs instantanés, tirés sur place en 10 secondes.',
           'Un photobooth original pour des photos funs.',
           'photo',
           $$[{"type":"IMAGE","ref":"uploads/photobooth-vintage.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/photobooth1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/photobooth2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/photobooth3/800/600","position":3},{"type":"YOUTUBE","ref":"ybJPbM_ExTk","position":4}]$$,
           $$["EQUIPE","REPONSE_48H"]$$,
           $$["Borne photobooth vintage avec tirage instantané","Fond personnalisable (couleur, motif, décor)","Accessoires et props inclus","Galerie digitale partagée après événement","Personnalisation du cadre photo (nom, date, logo)"]$$,
           $${"quote":"Un classique qui ne se démode jamais.","bio":"On a chiné cette borne dans une vente aux enchères en 2016 et on en est tombés amoureux. Depuis, elle voyage de fête en fête et ne manque jamais de faire sourire."}$$,
           $$"Forfait soirée (4h) à partir de 450€. Heures supplémentaires en option."$$,
           $$[{"author":"Nina & Pierre","text":"Le photobooth a été le hit de la soirée. File d'attente non-stop et des souvenirs géniaux pour tout le monde."}]$$,
           $$[{"content":"Forfait : Soirée de 4h avec tirage instantané, heures supplémentaires en option.","category":"FORMAT"},{"content":"Tirages et galerie : Tirage papier en 10 secondes et galerie digitale partagée après l'événement.","category":"DELIVERABLE"},{"content":"Personnalisation : Fond, accessoires et cadre photo (nom, date, logo) personnalisables.","category":"DELIVERABLE"},{"content":"Personnalisation du cadre : Les éléments graphiques sont à transmettre une semaine avant l'événement.","category":"PROCESS"},{"content":"Zone d'intervention : Alsace.","category":"LOGISTICS"},{"content":"Installation : 45 minutes avant le début, espace de 3m² plus zone de prise de vue, accès 220V nécessaire.","category":"LOGISTICS"}]$$,
           $$[{"question":"Peut-on personnaliser le cadre avec notre prénom et la date ?","answer":"Oui, le cadre est entièrement personnalisable. On vous demande les éléments 1 semaine avant l'événement."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd00000c-0000-0000-0000-000000000000',
           'cc00000c-0000-0000-0000-000000000000',
           'Studio Photo Mobile',
           'studio-photo-mobile',
           'La qualité studio, dans votre salle de réception.',
           'Un studio photo pour des portraits professionnels.',
           'photo',
           $$[{"type":"IMAGE","ref":"uploads/studio-photo-mobile.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/studio1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/studio2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/studio3/800/600","position":3},{"type":"YOUTUBE","ref":"VoHDwCCxWA4","position":4}]$$,
           $$["ACCOMPAGNEMENT","ADAPTABLE","ECORESPONSABLE"]$$,
           $$["Studio photo mobile monté sur place","Portraits individuels ou en groupe","Fond blanc, noir ou coloré au choix","Retouche légère incluse","Idéal pour séminaires, galas, événements corporate"]$$,
           $${"quote":"Des photos dont les gens sont fiers.","bio":"On apporte la lumière et le cadre — vous apportez vos sourires. En 5 minutes par personne, on fait des photos dont les gens sont fiers."}$$,
           $$"À partir de 600€ pour une session de 3h (jusqu'à 60 portraits). Devis sur demande."$$,
           $$[{"author":"DRH Groupe Alsace Industries","text":"Parfait pour nos portraits collaborateurs. Rapide, pro, et tout le monde était content du résultat."}]$$,
           $$[{"content":"Session : 3h de prise de vue, jusqu'à 60 portraits, environ 20 personnes par heure en individuel.","category":"FORMAT"},{"content":"Retouche incluse : Retouche légère comprise dans la prestation, fond blanc, noir ou coloré au choix.","category":"DELIVERABLE"},{"content":"Zone d'intervention : Alsace et Grand Est.","category":"LOGISTICS"},{"content":"Installation : 1h avant le début, espace de 6m² minimum, deux prises 220V nécessaires.","category":"LOGISTICS"},{"content":"Usage type : Particulièrement adapté aux séminaires, galas et événements corporate.","category":"OTHER"}]$$,
           $$[{"question":"Combien de personnes peuvent être photographiées sur une session ?","answer":"Environ 20 personnes par heure pour des portraits individuels. Pour les groupes, comptez plus de temps."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

-- ── Services / Lieux ──────────────────────────────────────────────────────────

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd00000d-0000-0000-0000-000000000000',
           'cc00000d-0000-0000-0000-000000000000',
           'Salle des Fêtes Rathsamhausen',
           'salle-fetes-rathsamhausen',
           'Un espace chaleureux au cœur du vignoble alsacien.',
           'Une salle spacieuse pour vos événements.',
           'services',
           $$[{"type":"IMAGE","ref":"uploads/salle-fetes.png","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/venue1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/venue2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/venue3/800/600","position":3},{"type":"YOUTUBE","ref":"SdAQjcIMjpo","position":4}]$$,
           $$["INTERLOCUTEUR_UNIQUE","EQUIPE","ACCOMPAGNEMENT"]$$,
           $$["Location de salle pour mariages, anniversaires, séminaires","Capacité : 30 à 200 personnes selon configuration","Cuisine équipée disponible","Terrasse et jardin privatifs","Tables, chaises et nappage inclus","Hébergement possible sur place (chambres en option)"]$$,
           $${"quote":"Chaque pierre a une histoire.","bio":"Cette salle, on l'a rénovée avec nos mains pendant trois ans. Chaque pierre a une histoire. On la loue à des gens qui savent prendre soin des lieux."}$$,
           $$"Location à partir de 800€ le week-end. Tarif journée disponible."$$,
           $$[{"author":"Famille Brunner","text":"Un cadre magnifique, un accueil irréprochable. Nos invités ont été sous le charme dès l'arrivée."}]$$,
           $$[{"content":"Capacité : 30 à 200 personnes selon configuration de la salle.","category":"FORMAT"},{"content":"Équipements inclus : Tables, chaises et nappage inclus ; cuisine équipée, sono de base, vidéoprojecteur et wifi disponibles.","category":"DELIVERABLE"},{"content":"Espaces extérieurs : Terrasse et jardin privatifs, hébergement possible sur place en option.","category":"DELIVERABLE"},{"content":"Liberté du traiteur : La salle est libre de traiteur, chaque client peut choisir son prestataire.","category":"PROCESS"},{"content":"Accès : Accès par la D35, parking de 80 véhicules.","category":"LOGISTICS"},{"content":"Remise des clés : Livraisons acceptées la veille de 9h à 18h, remise des clés la veille au soir.","category":"LOGISTICS"},{"content":"Horaires musique : Musique amplifiée autorisée jusqu'à 2h du matin, musique d'ambiance uniquement au-delà.","category":"OTHER"}]$$,
           $$[{"question":"Peut-on apporter son propre traiteur ?","answer":"Oui, la salle est libre de traiteur. Vous pouvez venir avec le prestataire de votre choix."},{"question":"Jusqu'à quelle heure peut-on faire de la musique ?","answer":"Musique amplifiée autorisée jusqu'à 2h du matin. Au-delà, musique d'ambiance uniquement."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd00000e-0000-0000-0000-000000000000',
           'cc00000e-0000-0000-0000-000000000000',
           'Château pour Mariage',
           'chateau-mariage',
           'Votre grand jour dans un écrin de pierre et de vigne.',
           'Un cadre enchanteur pour célébrer votre union.',
           'services',
           $$[{"type":"IMAGE","ref":"uploads/chateau-mariage.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/chateau1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/chateau2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/chateau3/800/600","position":3},{"type":"YOUTUBE","ref":"615biPQMdJQ","position":4}]$$,
           $$["REPONSE_48H","INTERLOCUTEUR_UNIQUE","ADAPTABLE"]$$,
           $$["Location exclusive du château et du parc","Salle de réception (jusqu'à 250 personnes)","Cérémonie laïque dans le parc ou dans la chapelle","12 chambres pour hébergement des invités","Cuisine professionnelle disponible","Parking sécurisé pour 150 véhicules"]$$,
           $${"quote":"Un lieu chargé d'histoire, prêt à accueillir la vôtre.","bio":"Ce château est dans notre famille depuis cinq générations. On le partage avec ceux qui veulent y créer leurs propres souvenirs."}$$,
           $$"Location week-end à partir de 4 500€. Devis sur demande selon saison et format."$$,
           $$[{"author":"Laura & Mathieu","text":"Un lieu de rêve. Nos invités n'en revenaient pas. On a l'impression d'avoir vécu un conte de fées."},{"author":"Directrice IESEG Strasbourg","text":"Cadre exceptionnel pour notre séminaire de direction. Prestations et accueil au niveau."}]$$,
           $$[{"content":"Capacité : Salle de réception jusqu'à 250 personnes, 12 chambres pour l'hébergement des invités.","category":"FORMAT"},{"content":"Espaces inclus : Parc, chapelle et salle de réception compris dans la location exclusive.","category":"DELIVERABLE"},{"content":"Équipements : Cuisine professionnelle, système son et lumière scénique, wifi haut débit dans tout le château.","category":"DELIVERABLE"},{"content":"Cérémonie : Cérémonie laïque possible dans le parc ou la chapelle ; la cérémonie civile reste en mairie.","category":"PROCESS"},{"content":"Accès : Sortie Sélestat Nord (A35), parking sécurisé pour 150 véhicules, hélipad disponible sur demande.","category":"LOGISTICS"},{"content":"Accessibilité : Accès PMR sur demande.","category":"LOGISTICS"},{"content":"Exclusivité : Un seul événement à la fois : le château et son parc sont réservés en intégralité.","category":"OTHER"}]$$,
           $$[{"question":"La location est-elle exclusive ?","answer":"Oui, nous ne faisons qu'un événement à la fois. Le château et son parc vous sont entièrement réservés."},{"question":"Peut-on organiser la cérémonie civile sur place ?","answer":"La cérémonie civile se tient en mairie. En revanche, une cérémonie laïque ou religieuse peut être organisée dans la chapelle ou le parc."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

INSERT INTO prestataires (id, utilisateur_id, name, slug, baseline, short_description, category_key, medias, badges, offerings, identity, budget, testimonials, details, faq, status, created_at)
VALUES (
           'dd00000f-0000-0000-0000-000000000000',
           'cc00000f-0000-0000-0000-000000000000',
           'Gîte de la mangouste',
           'gite-de-la-mangouste',
           'Dormir sur place, réveillon ou lendemain de fête serein.',
           'Un hébergement confortable pour vos invités.',
           'services',
           $$[{"type":"IMAGE","ref":"uploads/hebergement-groupe.jpg","position":0},{"type":"IMAGE","ref":"https://picsum.photos/seed/hebergement1/800/600","position":1},{"type":"IMAGE","ref":"https://picsum.photos/seed/hebergement2/800/600","position":2},{"type":"IMAGE","ref":"https://picsum.photos/seed/hebergement3/800/600","position":3},{"type":"YOUTUBE","ref":"_2tBuidbF88","position":4}]$$,
           $$["ACCOMPAGNEMENT","EQUIPE","INTERLOCUTEUR_UNIQUE"]$$,
           $$["Location du gîte complet (16 personnes)","Location à la chambre possible hors saison","Draps et serviettes fournis","Petit-déjeuner en option","Idéal pour week-ends de fête, enterrements de vie, séjours groupes"]$$,
           $${"quote":"C'est chez vous !","bio":"On a aménagé ce gîte pour que les gens s'y sentent comme chez eux — parce que pendant leur séjour, c'est chez eux."}$$,
           $$"Location complète à partir de 350€ la nuit. Week-end (2 nuits) à partir de 600€."$$,
           $$[{"author":"Groupe EVG de Julien","text":"Parfait pour notre week-end. Grand, bien équipé, calme. On reviendra."}]$$,
           $$[{"content":"Capacité : Location complète (16 personnes) ou à la chambre hors saison.","category":"FORMAT"},{"content":"Confort inclus : Draps et serviettes fournis, cuisine équipée, salon avec cheminée, wifi inclus.","category":"DELIVERABLE"},{"content":"Séjour : Petit-déjeuner disponible en option.","category":"PROCESS"},{"content":"Accès : Village de Bernardswiller, à 20 minutes de Strasbourg, parking privé pour 8 véhicules.","category":"LOGISTICS"},{"content":"Horaires : Check-in à partir de 16h, check-out avant 11h.","category":"LOGISTICS"},{"content":"Animaux et musique : Animaux acceptés sur demande ; musique modérée acceptée jusqu'à minuit, sono à valider au préalable.","category":"OTHER"}]$$,
           $$[{"question":"Peut-on organiser une fête dans le gîte ?","answer":"Musique modérée acceptée jusqu'à minuit. Pour les fêtes avec sono, merci de nous contacter au préalable."},{"question":"Le linge de lit est-il fourni ?","answer":"Oui, draps, taies et serviettes sont inclus et changés entre chaque séjour."}]$$,
           'PUBLISHED',
           NOW()
       )
    ON CONFLICT (id) DO UPDATE SET
        utilisateur_id    = EXCLUDED.utilisateur_id,
        name              = EXCLUDED.name,
        slug              = EXCLUDED.slug,
        baseline          = EXCLUDED.baseline,
        short_description = EXCLUDED.short_description,
        category_key      = EXCLUDED.category_key,
        medias            = EXCLUDED.medias,
        badges            = EXCLUDED.badges,
        offerings         = EXCLUDED.offerings,
        identity          = EXCLUDED.identity,
        budget            = EXCLUDED.budget,
        testimonials      = EXCLUDED.testimonials,
        details           = EXCLUDED.details,
        faq               = EXCLUDED.faq,
        status            = EXCLUDED.status;

-- ─── 3. Liens prestataires ↔ sous-catégories ─────────────────────────────────

INSERT INTO prestataires_sous_categories (prestataire_id, subcat_key) VALUES
                                                                          ('dd000001-0000-0000-0000-000000000000', 'dj'),
                                                                          ('dd000002-0000-0000-0000-000000000000', 'jazz'),
                                                                          ('dd000003-0000-0000-0000-000000000000', 'pop-rock'),
                                                                          ('dd000004-0000-0000-0000-000000000000', 'pop-rock'),
                                                                          ('dd000005-0000-0000-0000-000000000000', 'traiteur'),
                                                                          ('dd000006-0000-0000-0000-000000000000', 'food-truck'),
                                                                          ('dd000007-0000-0000-0000-000000000000', 'traiteur'),
                                                                          ('dd000008-0000-0000-0000-000000000000', 'traiteur'),
                                                                          ('dd000009-0000-0000-0000-000000000000', 'food-truck'),
                                                                          ('dd00000a-0000-0000-0000-000000000000', 'photographe'),
                                                                          ('dd00000b-0000-0000-0000-000000000000', 'photobooth'),
                                                                          ('dd00000c-0000-0000-0000-000000000000', 'photographe'),
                                                                          ('dd00000d-0000-0000-0000-000000000000', 'location-lieu'),
                                                                          ('dd00000e-0000-0000-0000-000000000000', 'location-lieu'),
                                                                          ('dd00000f-0000-0000-0000-000000000000', 'location-lieu')
    ON CONFLICT (prestataire_id, subcat_key) DO NOTHING;