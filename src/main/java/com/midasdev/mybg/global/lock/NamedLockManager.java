package com.midasdev.mybg.global.lock;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class NamedLockManager {

    private final EntityManager entityManager;

    /**
     * MySQL Named Lock 획득 시도 <br/>
     * - 트랜잭션 커밋 시점에 해제됨 <br/>
     * - 트랜잭션 롤백 시점에도 해제 시도함 <br/>
     * @param key 잠금 키
     * @param waitSec 대기 시간(초)
     * @return 획득 성공 여부
     */
    public boolean tryAcquire(String key, int waitSec) {
        // 1) 같은 EntityManager로 GET_LOCK 실행 → 같은 커넥션 사용
        int lock = ((Number) entityManager.createNativeQuery("SELECT GET_LOCK(:k, :w)")
                                             .setParameter("k", key)
                                             .setParameter("w", waitSec)
                                             .getSingleResult()).intValue();
        if (lock == 0) {
            log.error("Failed to acquire named lock: {}", key);
            return false;
        }

        // 2) 커밋 이후에 같은 커넥션으로 RELEASE_LOCK 실행되도록 등록
        final Session session = entityManager.unwrap(Session.class);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // 커밋 직후 해제
                session.doWork(conn -> {
                    try (var ps = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
                        ps.setString(1, key);
                        ps.execute();
                    }
                });
                log.info("Released named lock: {}", key);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    // 롤백 등 비정상 종료 시에도 해제 시도
                    session.doWork(conn -> {
                        try (var ps = conn.prepareStatement("SELECT RELEASE_LOCK(?)")) {
                            ps.setString(1, key);
                            ps.execute();
                        }
                    });
                    log.info("Released named lock after rollback: {}", key);
                }
            }
        });
        log.info("Acquired named lock: {}", key);
        return true;
    }

}
