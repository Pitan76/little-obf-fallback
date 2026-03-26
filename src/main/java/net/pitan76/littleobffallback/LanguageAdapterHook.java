package net.pitan76.littleobffallback;

import net.fabricmc.loader.api.LanguageAdapter;
import net.fabricmc.loader.api.ModContainer;

public class LanguageAdapterHook implements LanguageAdapter {
    @Override
    public <T> T create(ModContainer mod, String value, Class<T> type) {
        try {
            Class<?> targetClass = Class.forName(value, true, Thread.currentThread().getContextClassLoader());
            return type.cast(targetClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize legacy mod: " + value, e);
        }
    }
}