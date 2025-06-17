package com.marcuslull.aigmmcp.data.structured.repositories;

import com.marcuslull.aigmmcp.data.structured.entities.Srd521MonsterCr;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface Srd521MonsterCrRepository extends ListPagingAndSortingRepository<Srd521MonsterCr, Long> {
    List<Srd521MonsterCr> findAllByCr(Integer cr);
}
