package com.chess.server.gameplay;

import com.chess.engine.GameEngine;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.*;

@Converter(autoApply = true)
public class GameEngineConverter implements AttributeConverter<GameEngine, byte[]> {

    @Override
    public byte[] convertToDatabaseColumn(GameEngine attribute) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(attribute);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameEngine convertToEntityAttribute(byte[] dbData) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dbData);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (GameEngine) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}