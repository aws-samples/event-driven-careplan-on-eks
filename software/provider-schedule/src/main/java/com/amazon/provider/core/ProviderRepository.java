package com.amazon.provider.core;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProviderRepository extends CrudRepository<Provider, UUID>{

}
