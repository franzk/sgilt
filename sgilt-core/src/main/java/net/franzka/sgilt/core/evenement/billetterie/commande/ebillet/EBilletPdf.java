package net.franzka.sgilt.core.evenement.billetterie.commande.ebillet;

/**
 * PDF d'un e-billet, prêt à être servi en téléchargement ou joint à un e-mail.
 *
 * @param content  le contenu du PDF
 * @param fileName le nom du fichier proposé à l'acheteur
 */
public record EBilletPdf(byte[] content, String fileName) {}
