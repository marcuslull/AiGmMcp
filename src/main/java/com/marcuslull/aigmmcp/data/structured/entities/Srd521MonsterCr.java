package com.marcuslull.aigmmcp.data.structured.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "srd521monstercr")
public record Srd521MonsterCr(
        @Id Long id,
        String name,
        Integer cr
) {
}
