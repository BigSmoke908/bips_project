package de.ostfalia.bips.ws25.camunda;

public record Option<T>(String label, T value) {
    public static <T> Option<T> of(String label, T value) {
        return new Option<>(label, value);
    }
}
