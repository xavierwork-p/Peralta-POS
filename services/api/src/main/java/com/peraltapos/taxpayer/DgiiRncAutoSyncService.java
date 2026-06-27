package com.peraltapos.taxpayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class DgiiRncAutoSyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DgiiRncAutoSyncService.class);

    private final DgiiRncRepository dgiiRncRepository;
    private final DgiiRncImportService dgiiRncImportService;
    private final boolean autoSyncEnabled;
    private final Duration staleAfter;
    private final AtomicBoolean syncRunning = new AtomicBoolean(false);

    public DgiiRncAutoSyncService(
            DgiiRncRepository dgiiRncRepository,
            DgiiRncImportService dgiiRncImportService,
            @Value("${app.dgii.rnc.auto-sync-enabled:true}") boolean autoSyncEnabled,
            @Value("${app.dgii.rnc.stale-after-hours:24}") long staleAfterHours
    ) {
        this.dgiiRncRepository = dgiiRncRepository;
        this.dgiiRncImportService = dgiiRncImportService;
        this.autoSyncEnabled = autoSyncEnabled;
        this.staleAfter = Duration.ofHours(staleAfterHours);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartupIfStale() {
        if (!autoSyncEnabled || !isRncDataStale()) {
            return;
        }

        CompletableFuture.runAsync(() -> sync("arranque"));
    }

    @Scheduled(cron = "${app.dgii.rnc.sync-cron:0 0 3 * * *}")
    public void scheduledSync() {
        if (!autoSyncEnabled || !isRncDataStale()) {
            return;
        }

        sync("programada");
    }

    private boolean isRncDataStale() {
        return dgiiRncRepository.findLatestUpdate()
                .map(latestUpdate -> latestUpdate.plus(staleAfter).isBefore(OffsetDateTime.now()))
                .orElse(true);
    }

    private void sync(String trigger) {
        if (!syncRunning.compareAndSet(false, true)) {
            LOGGER.info("Sincronizacion DGII RNC omitida porque ya hay una importacion en curso");
            return;
        }

        try {
            LOGGER.info("Ejecutando sincronizacion DGII RNC por {}", trigger);
            dgiiRncImportService.importFromDgii();
        } catch (RuntimeException exception) {
            LOGGER.warn("No se pudo sincronizar DGII RNC por {}: {}", trigger, exception.getMessage());
        } finally {
            syncRunning.set(false);
        }
    }
}
