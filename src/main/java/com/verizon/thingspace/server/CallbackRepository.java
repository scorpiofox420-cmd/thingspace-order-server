package com.verizon.thingspace.server;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CallbackRepository extends JpaRepository<CallbackRecord, Long> {
}
