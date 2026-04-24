package net.franzka.sgilt.core.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@DiscriminatorValue("NOTE")
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Note extends ReservationFeed {

    @Column(columnDefinition = "text")
    private String content;
}