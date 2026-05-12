package net.franzka.sgilt.core.evenement.mapper;

import net.franzka.sgilt.core.evenement.domain.JournalEvenement;
import net.franzka.sgilt.core.evenement.dto.JournalEvenementDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JournalEvenementMapper {

    JournalEvenementDto toDto(JournalEvenement journalEvenement);
}
