package net.franzka.sgilt.core.survey.domain;

import java.util.List;

/**
 * Configuration d'un sondage — chargée depuis un fichier JSON statique versionné
 * ({@code survey/{slug}.json}), servie telle quelle au front et réutilisée côté back pour valider
 * les réponses soumises.
 *
 * @param slug        identifiant du sondage, utilisé dans l'URL et comme nom de fichier de config
 * @param title       titre affiché en haut de la page
 * @param description texte d'introduction, sous le titre
 * @param questions   liste ordonnée des questions, sans logique conditionnelle entre elles
 */
public record Survey(
        String slug,
        String title,
        String description,
        List<Question> questions
) {}
