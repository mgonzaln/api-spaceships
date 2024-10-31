package com.cinema.dto;

import lombok.Builder;

@Builder
public record RequestDto (String name, String origin){}
