package io.filkovsp.carpark.config;

import lombok.Data;

import java.util.List;

@Data
public class StoreyConfigProperties {
    private List<StoreyProperties> storeys;

    @Data
    public static class StoreyProperties {
        private String level;
        private int capacity;
        private String evEnabled;
    }
}
