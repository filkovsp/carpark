package io.filkovsp.carpark.config;

import io.filkovsp.carpark.model.ParkingSpace;
import io.filkovsp.carpark.model.Storey;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "car-park")
public class StoreyConfiguration {

    @Setter
    private List<StoreyConfigProperties.StoreyProperties> storeys;

    @Bean("Storeys")
    List<Storey> listOfStoreys() {
        return storeys.stream().map(
                storeyProperties -> {
                    List<ParkingSpace> parkinSpaces = new ArrayList<>();
                    Storey storey = new Storey(storeyProperties.getLevel());
                    for (int i = 0; i < storeyProperties.getCapacity(); i++) {
                        boolean isEvEnabled = checkEvEnabled(storeyProperties.getEvEnabled(), i);
                        String designation = String.format("%s-%03d", storey.getLevel(), i + 1);
                        parkinSpaces.add(new ParkingSpace(designation, isEvEnabled));
                    }
                    storey.getParkingSpaces().addAll(parkinSpaces);
                    return storey;
                }
        ).collect(Collectors.toList());
    }

    private boolean checkEvEnabled(String evConfig, int i) {
        if (evConfig == null) {
            return false;
        }

        if (evConfig.equalsIgnoreCase("none")) {
            return false;
        }

        if (evConfig.equalsIgnoreCase("all")) {
            return true;
        }

        return Arrays.asList(evConfig.split(","))
                .contains(Integer.toString(i));
    }
}
