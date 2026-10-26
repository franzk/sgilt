package net.franzka.sgilt.core.evenement.page.domain;

/**
 * Point fort d'une page d'événement (pictogramme et deux lignes de texte).
 *
 * @param icon     la clé du pictogramme ('music'…), traduite en icône par le front
 * @param label    le libellé principal
 * @param sublabel le libellé secondaire
 */
public record PageEvenementPicto(String icon, String label, String sublabel) {}
