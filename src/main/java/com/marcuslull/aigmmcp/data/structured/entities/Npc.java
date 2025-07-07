package com.marcuslull.aigmmcp.data.structured.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "npc")
public record Npc(
        @Id Long id,
        String name,
        String race,
        Character sex,
        Integer age,
        String description,
        String personality,
        String background,
        String npc_class,
        Integer level,
        String status,
        String location,
        String notes
) {
}
