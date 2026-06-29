package br.com.guimasnacopa.apifootbol.client;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.guimasnacopa.apifootbol.dto.CampeonatoDTO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ApiFutebolClient {

    private static final String API_URL =
            "https://api.api-futebol.com.br/v1/campeonatos";

    private static final String TOKEN =
            "Bearer live_2c96f9098f30ddf6f31f5ed6a8eff5";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CampeonatoDTO> listarCampeonatos() throws Exception {

        URL url = new URL(API_URL);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", TOKEN);
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException(
                    "Erro ao consumir API. HTTP Code: " + responseCode
            );
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return objectMapper.readValue(
                response.toString(),
                new TypeReference<List<CampeonatoDTO>>() {
                }
        );
    }

    public static void main(String[] args) {

        try {

            ApiFutebolClient client = new ApiFutebolClient();

            List<CampeonatoDTO> campeonatos =
                    client.listarCampeonatos();

            campeonatos.forEach(c ->
                    System.out.println(
                            c.getCampeonatoId()
                                    + " - "
                                    + c.getNomePopular()
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}