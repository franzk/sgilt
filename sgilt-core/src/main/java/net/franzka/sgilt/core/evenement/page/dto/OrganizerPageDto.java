package net.franzka.sgilt.core.evenement.page.dto;

/**
 * Page d'un événement telle que l'administre son organisateur : son contenu et son état.
 *
 * @param content    le contenu de la page
 * @param published  la page est publiée (sinon, brouillon introuvable sur /e/{slug})
 * @param slugLocked le slug est verrouillé : la page a déjà été publiée
 */
public record OrganizerPageDto(
        PageContentDto content,
        boolean published,
        boolean slugLocked
) {}
