package com.bridle.core.components.kafka;

/**
 * Реакция по изменению оффсета (позиции чтения) в случае, если для данной группы потребителей еще не был установлено
 * смещение, либо оно имеет значения за актуальными пределами топика
 * Взято из документации {@link org.apache.kafka.clients.consumer.ConsumerConfig}
 * <p>
 * What to do when there is no initial offset in Kafka or if
 * the current offset does not exist any more on the server (e.g. because that data has been deleted):
 * <ul><li>earliest: automatically reset the offset to the earliest offset<li>latest: automatically reset the
 * offset to the latest offset</li><li>none: throw exception to the consumer if no previous offset is found for
 * the consumer's group</li><li>anything else: throw exception to the consumer.</li></ul>";
 */
public enum AutoOffsetReset {
    /**
     * Сдвинуть позицию чтения на минимальную из возможных - читать с начала топика
     */
    EARLIEST("earliest"),
    /**
     * Сдвинуть позицию чтения на максимальную из возможных - читать начиная с конца топика
     */
    LATEST("latest"),
    /**
     * В случае обнаружения отсутствия оффсета выкидывать исключение
     */
    NONE("none");

    private final String value;

    AutoOffsetReset(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
