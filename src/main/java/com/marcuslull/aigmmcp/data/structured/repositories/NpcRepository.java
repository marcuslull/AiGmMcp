package com.marcuslull.aigmmcp.data.structured.repositories;

import com.marcuslull.aigmmcp.data.structured.entities.Npc;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface NpcRepository extends ListPagingAndSortingRepository<Npc, Long> {
}
