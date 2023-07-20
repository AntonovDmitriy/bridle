package utils;

import org.testcontainers.containers.Container;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

public class KafkaContainerUtils {
    public static CommandResult createTopic(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        String command = String.format("kafka-topics --create --bootstrap-server localhost:9092" +
                                               " --topic %s --partitions 1 --replication-factor 1", topicName);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static CommandResult deleteTopic(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        String command = String.format("kafka-topics --delete --bootstrap-server localhost:9092 --topic %s", topicName);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static CommandResult readMessage(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        String command = String.format("kafka-console-consumer --bootstrap-server localhost:9092" +
                                               " --topic %s --from-beginning --max-messages 1", topicName);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static CommandResult writeMessageToTopic(KafkaContainer kafkaContainer, String topicName, String message)
    throws IOException, InterruptedException {
        String command = String.format(
                "echo \"%s\" | kafka-console-producer --bootstrap-server localhost:9092 --topic %s",
                message,
                topicName);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static int countMessages(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        String command = String.format(
                "kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic %s --offsets -1",
                topicName);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        return Integer.parseInt(execResult.getStdout().strip().split(":")[2]);
    }

    public static long getConsumerGroupOffset(KafkaContainer kafkaContainer,
            String topicName,
            String consumerGroup,
            int partition) throws IOException, InterruptedException {
        String command = String.format("kafka-consumer-groups --bootstrap-server localhost:9092 --group %s --describe",
                                       consumerGroup);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        String[] lines = execResult.getStdout().split("\\r?\\n");
        for (String line : lines) {
            if (line.contains(topicName) && line.contains(Integer.toString(partition))) {
                String[] cols = line.split("\\s+");
                return Long.parseLong(cols[3]);
            }
        }
        return -1;
    }

    public static long getTopicEndOffset(KafkaContainer kafkaContainer, String topicName, int partition)
    throws IOException, InterruptedException {
        String command = String.format(
                "kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic %s --offsets -1 --partitions %d",
                topicName,
                partition);
        Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash", "-c", command);
        return Long.parseLong(execResult.getStdout().strip().split(":")[2]);
    }

    public static long getUnreadMessageCount(KafkaContainer kafkaContainer,
            String topicName,
            String consumerGroup,
            int partition) throws IOException, InterruptedException {
        long consumerOffset = getConsumerGroupOffset(kafkaContainer, topicName, consumerGroup, partition);
        long endOffset = getTopicEndOffset(kafkaContainer, topicName, partition);
        if (consumerOffset == -1) {
            return endOffset;
        }
        return endOffset - consumerOffset;
    }

    public static void setupKafka(KafkaContainer kafkaContainer, int kafkaPort) {
        kafkaContainer.start();
        System.setProperty("kafka-out.brokers", "localhost:" + kafkaContainer.getMappedPort(kafkaPort).toString());
        System.setProperty("kafka-in.brokers", "localhost:" + kafkaContainer.getMappedPort(kafkaPort).toString());
    }

    public static KafkaContainer createKafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
                .withEnv("KAFKA_DELETE_TOPIC_ENABLE", "true")
                .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "false");
    }
}
