package Videoclub.mapper;

import Videoclub.dto.VideoGameCopyDTO;
import Videoclub.dto.VideoGameDTO;
import Videoclub.entity.GameCopy;
import Videoclub.entity.VideoGame;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoGameMapper {

    @Mapping(target = "availableCopies", ignore = true)
    @Mapping(target = "totalCopies", ignore = true)
    VideoGameDTO toDto(VideoGame entity);

    VideoGame toEntity(VideoGameDTO dto);

    VideoGameCopyDTO toDtoCopy(GameCopy entity);
    GameCopy toEntityCopy(VideoGameCopyDTO dto);
}
