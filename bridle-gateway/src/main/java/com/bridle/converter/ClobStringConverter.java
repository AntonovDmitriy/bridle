package com.bridle.converter;

import org.springframework.core.convert.converter.Converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

public class ClobStringConverter implements Converter<Clob, String> {

    @Override
    public String convert(Clob source) {
        if (source == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        try (Reader reader = source.getCharacterStream(); BufferedReader br = new BufferedReader(reader)) {

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException | SQLException e) {
            throw new ConverterException("Error while conversion Clob to String", e);
        }

        return sb.toString();
    }
}
