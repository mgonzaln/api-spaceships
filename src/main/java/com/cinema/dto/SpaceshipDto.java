package com.cinema.dto;

import lombok.Builder;

@Builder
public record SpaceshipDto(String id, String name, String origin) {
}
