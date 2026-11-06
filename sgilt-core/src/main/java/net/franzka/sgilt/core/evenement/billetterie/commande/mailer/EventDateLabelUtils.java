package net.franzka.sgilt.core.evenement.billetterie.commande.mailer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Libellé de la date et des horaires d'un événement dans un e-mail : « Jeudi 3 décembre 2026 ·
 * 19h – 2h ». Même rendu que le front (page événement, e-billet) — doublon assumé, à faire
 * évoluer des deux côtés.
 */
public final class EventDateLabelUtils {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);

    private EventDateLabelUtils() {}

    /**
     * Construit le libellé de la date et des horaires d'un événement : « Jeudi 3 décembre 2026 ·
     * 19h – 2h », « … · 19h » (début seul), « … · jusqu'à 2h » (fin seule), ou la date seule sans
     * horaires.
     *
     * @param date     la date de l'événement
     * @param startsAt le début, ou null
     * @param endsAt   la fin, ou null
     * @return le libellé
     */
    public static String eventDateLabel(LocalDate date, LocalDateTime startsAt, LocalDateTime endsAt) {
        String day = date.format(DATE_FORMAT);
        String dateLabel = Character.toUpperCase(day.charAt(0)) + day.substring(1);
        String hours = hoursLabel(startsAt, endsAt);
        return hours == null ? dateLabel : dateLabel + " · " + hours;
    }

    // « 19h – 2h », « 19h », « jusqu'à 2h », ou null sans horaires.
    private static String hoursLabel(LocalDateTime startsAt, LocalDateTime endsAt) {
        if (startsAt != null && endsAt != null) return timeOfDay(startsAt) + " – " + timeOfDay(endsAt);
        if (startsAt != null) return timeOfDay(startsAt);
        if (endsAt != null) return "jusqu'à " + timeOfDay(endsAt);
        return null;
    }

    // « 19h », ou « 19h30 » avec des minutes.
    private static String timeOfDay(LocalDateTime time) {
        int minutes = time.getMinute();
        return time.getHour() + "h" + (minutes == 0 ? "" : String.format("%02d", minutes));
    }
}
