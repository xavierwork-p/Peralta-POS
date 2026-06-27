package com.peraltapos.taxpayer;

import com.peraltapos.common.web.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class DgiiRncImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DgiiRncImportService.class);
    private static final int BATCH_SIZE = 500;
    private static final Charset DGII_CHARSET = Charset.forName("windows-1252");

    private final DgiiRncRepository dgiiRncRepository;
    private final RestTemplate restTemplate;
    private final String sourceUrl;

    public DgiiRncImportService(
            DgiiRncRepository dgiiRncRepository,
            @Value("${app.dgii.rnc.url:https://dgii.gov.do/app/WebApps/Consultas/RNC/DGII_RNC.zip}") String sourceUrl
    ) {
        this.dgiiRncRepository = dgiiRncRepository;
        this.restTemplate = new RestTemplate();
        this.sourceUrl = sourceUrl;
    }

    public DgiiRncImportSummary importFromDgii() {
        LOGGER.info("Iniciando importacion DGII RNC desde {}", sourceUrl);
        Path zipPath = downloadZip();

        try {
            DgiiRncImportSummary summary = importZip(zipPath);
            LOGGER.info(
                    "Importacion DGII RNC completada: leidos={}, insertados={}, actualizados={}, ignorados={}",
                    summary.read(),
                    summary.inserted(),
                    summary.updated(),
                    summary.ignored()
            );
            return summary;
        } finally {
            try {
                Files.deleteIfExists(zipPath);
            } catch (IOException ignored) {
                // El archivo temporal se puede limpiar en la proxima corrida del sistema.
            }
        }
    }

    private Path downloadZip() {
        try {
            Path zipPath = Files.createTempFile("dgii-rnc-", ".zip");

            restTemplate.execute(sourceUrl, HttpMethod.GET, null, response -> {
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new BusinessException("La DGII respondio con estado " + response.getStatusCode());
                }

                Files.copy(response.getBody(), zipPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                return null;
            });

            return zipPath;
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BusinessException("No se pudo descargar el archivo DGII_RNC.zip");
        }
    }

    private DgiiRncImportSummary importZip(Path zipPath) {
        ImportCounters counters = new ImportCounters();

        try (
                ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(zipPath));
                InputStreamReader streamReader = new InputStreamReader(zipInputStream, DGII_CHARSET);
                BufferedReader reader = new BufferedReader(streamReader)
        ) {
            ZipEntry selectedEntry = moveToDataEntry(zipInputStream);

            if (selectedEntry == null) {
                throw new BusinessException("El ZIP de DGII no contiene un archivo TXT o CSV");
            }

            List<DgiiRncImportRow> batch = new ArrayList<>(BATCH_SIZE);
            String line;

            while ((line = reader.readLine()) != null) {
                counters.read++;
                DgiiRncImportRow row = parseLine(line);

                if (row == null) {
                    counters.ignored++;
                    continue;
                }

                batch.add(row);

                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch, counters);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch, counters);
            }

            return counters.toSummary();
        } catch (BusinessException exception) {
            throw exception;
        } catch (IOException exception) {
            throw new BusinessException("No se pudo leer el ZIP de DGII");
        }
    }

    private ZipEntry moveToDataEntry(ZipInputStream zipInputStream) throws IOException {
        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            String name = entry.getName().toLowerCase();

            if (!entry.isDirectory() && (name.endsWith(".txt") || name.endsWith(".csv"))) {
                return entry;
            }
        }

        return null;
    }

    private DgiiRncImportRow parseLine(String line) {
        if (line == null || line.isBlank() || !line.contains("|")) {
            return null;
        }

        String[] columns = line.split("\\|", -1);

        if (columns.length < 2) {
            return null;
        }

        String rnc = cleanRnc(columns[0]);
        String razonSocial = cleanText(valueAt(columns, 1));

        if (!isValidDominicanTaxId(rnc) || razonSocial == null) {
            return null;
        }

        return new DgiiRncImportRow(
                rnc,
                razonSocial,
                cleanText(valueAt(columns, 2)),
                cleanText(valueAt(columns, 3)),
                cleanText(valueAt(columns, 4)),
                cleanText(valueAt(columns, 5)),
                cleanText(valueAt(columns, 6)),
                cleanText(valueAt(columns, 7)),
                cleanText(valueAt(columns, 8))
        );
    }

    private void saveBatch(List<DgiiRncImportRow> batch, ImportCounters counters) {
        List<DgiiRncImportRow> uniqueRows = deduplicateBatch(batch);
        Set<String> rncs = uniqueRows.stream().map(DgiiRncImportRow::rnc).collect(Collectors.toSet());
        Map<String, DgiiRnc> existingByRnc = dgiiRncRepository.findAllById(rncs)
                .stream()
                .collect(Collectors.toMap(DgiiRnc::getRnc, Function.identity()));

        List<DgiiRnc> records = uniqueRows.stream()
                .map(row -> {
                    DgiiRnc existing = existingByRnc.get(row.rnc());

                    if (existing == null) {
                        counters.inserted++;
                        return DgiiRnc.fromImport(row);
                    }

                    counters.updated++;
                    existing.updateFrom(row);
                    return existing;
                })
                .toList();

        dgiiRncRepository.saveAll(records);
    }

    private List<DgiiRncImportRow> deduplicateBatch(List<DgiiRncImportRow> batch) {
        Set<String> seen = new HashSet<>();
        List<DgiiRncImportRow> uniqueRows = new ArrayList<>();

        for (DgiiRncImportRow row : batch) {
            if (seen.add(row.rnc())) {
                uniqueRows.add(row);
            }
        }

        return uniqueRows;
    }

    private String valueAt(String[] columns, int index) {
        return index < columns.length ? columns[index] : null;
    }

    private String cleanText(String value) {
        String cleanValue = value == null ? null : value.trim();
        return cleanValue == null || cleanValue.isBlank() ? null : cleanValue;
    }

    private String cleanRnc(String value) {
        return value == null ? "" : value.replaceAll("\\D", "");
    }

    private boolean isValidDominicanTaxId(String value) {
        return value.length() == 9 || value.length() == 11;
    }

    private static class ImportCounters {
        private long read;
        private long inserted;
        private long updated;
        private long ignored;

        private DgiiRncImportSummary toSummary() {
            return new DgiiRncImportSummary(read, inserted, updated, ignored, OffsetDateTime.now());
        }
    }
}
