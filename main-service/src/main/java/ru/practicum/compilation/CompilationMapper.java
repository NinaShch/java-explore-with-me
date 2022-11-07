package ru.practicum.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.HelperService;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.entity.Compilation;
import ru.practicum.event.EventMapper;
import ru.practicum.event.EventService;

@Mapper(componentModel = "spring", uses = {EventService.class, EventMapper.class, HelperService.class})
public interface CompilationMapper {

    @Mapping(target = "pinned", source = "newCompilationDto.pinned", defaultValue = "false")
    Compilation toNewCompilation(NewCompilationDto newCompilationDto);

    CompilationDto toCompilationDto(Compilation compilation);
}
