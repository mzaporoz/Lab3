package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> translations;
    private final List<String> countryCodes;
    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        translations = new HashMap<>();
        countryCodes = new ArrayList<>();
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject countryData = jsonArray.getJSONObject(i);
                String countryCode = countryData.getString("country").toLowerCase();
                countryCodes.add(countryCode);

                Map<String, String> languageMap = new HashMap<>();
                for (String key : countryData.keySet()) {
                    if (!key.equals("country")) {
                        languageMap.put(key, countryData.getString(key));
                    }
                }

                translations.put(countryCode, languageMap);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        country = country.toLowerCase();
        if (translations.containsKey(country)) {
            return new ArrayList<>(translations.get(country).keySet());
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryCodes);
    }

    @Override
    public String translate(String country, String language) {
        country = country.toLowerCase();
        if (translations.containsKey(country)) {
            Map<String, String> languageMap = translations.get(country);
            if (languageMap.containsKey(language)) {
                return languageMap.get(language);
            } else {
                return "Translation not available for language: " + language;
            }
        } else {
            return "Country not found";
        }
    }
}
