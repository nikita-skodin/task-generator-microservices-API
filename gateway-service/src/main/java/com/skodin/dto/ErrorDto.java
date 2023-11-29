package com.skodin.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

public record ErrorDto(String message, String error) {
}