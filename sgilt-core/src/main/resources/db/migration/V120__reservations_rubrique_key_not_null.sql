-- Toute réservation est désormais rangée à son entrée (ReservationService.create reçoit sa rubrique) ;
-- les réservations existantes ont été rangées par V119.
ALTER TABLE reservations ALTER COLUMN rubrique_key SET NOT NULL;
