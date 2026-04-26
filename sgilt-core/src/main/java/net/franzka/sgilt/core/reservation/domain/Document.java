package net.franzka.sgilt.core.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("DOCUMENT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Document extends ReservationFeed {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String url;

    private String mimeType;
}