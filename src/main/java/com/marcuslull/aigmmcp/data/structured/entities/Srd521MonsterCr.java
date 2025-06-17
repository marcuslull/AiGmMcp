package com.marcuslull.aigmmcp.data.structured.entities;

import org.springframework.data.annotation.Id;

public record Srd521MonsterCr(
        @Id Long id,
        String name,
        Integer cr
) {
}
