package com.nowgroup.ngMantisExtractor.mbt.repo;

import org.springframework.data.repository.CrudRepository;

import com.nowgroup.ngMantisExtractor.mbt.dto.Bug;

public interface BugRepository extends CrudRepository<Bug, Integer> {

}
