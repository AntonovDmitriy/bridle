package com.bridle.core.components.kafka;

/**
 * Позиция оффсета для чтения сообщений при подключении к топику
 * <p>
 * Set if KafkaConsumer will read from beginning or end on startup:
 * beginning : read from beginning
 * end : read from end
 */
public enum ChangeOffsetTo {
    /**
     * Сдвинуть позицию чтения на минимальную из возможных - читать с начала топика
     */
    BEGINNING("beginning"),
    /**
     * Сдвинуть позицию чтения на максимальную из возможных - читать начиная с конца топика
     */
    END("end"),
    /**
     * Читать с текущеий позиции оффсета
     */
    CURRENT_OFFSET(null);

    private String value;

    ChangeOffsetTo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
