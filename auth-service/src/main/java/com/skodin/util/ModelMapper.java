package com.skodin.util;

import com.skodin.DTO.UserDTO;
import com.skodin.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelMapper {
    private final org.modelmapper.ModelMapper modelMapper = new org.modelmapper.ModelMapper();

    {
        configureModelMapper(modelMapper);
    }

    public UserDTO getUserDTO(UserEntity user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public UserEntity getUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, UserEntity.class);
    }

    private void configureModelMapper(org.modelmapper.ModelMapper modelMapper) {}
}
