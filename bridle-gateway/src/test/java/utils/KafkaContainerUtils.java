package utils;

import org.testcontainers.containers.KafkaContainer;

import java.io.IOException;

public class KafkaContainerUtils {
    public static CommandResult createTopic(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash",
                                                                                                       "-c",
                                                                                                       String.format(
                                                                                                               "kafka-topics --create --bootstrap-server localhost:9092" +
                                                                                                                       " --topic %s --partitions 1 --replication-factor 1",
                                                                                                               topicName));
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static CommandResult deleteTopic(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash",
                                                                                                       "-c",
                                                                                                       String.format(
                                                                                                               "kafka-topics --delete --bootstrap-server localhost:9092 --topic %s",
                                                                                                               topicName));
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static CommandResult readMessage(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash",
                                                                                                       "-c",
                                                                                                       String.format(
                                                                                                               "kafka-console-consumer --bootstrap-server localhost:9092" +
                                                                                                                       " --topic %s --from-beginning --max-messages 1",
                                                                                                               topicName));
        return new CommandResult(execResult.getExitCode(), execResult.getStdout(), execResult.getStderr());
    }

    public static int countMessages(KafkaContainer kafkaContainer, String topicName)
    throws IOException, InterruptedException {
        org.testcontainers.containers.Container.ExecResult execResult = kafkaContainer.execInContainer("/bin/bash",
                                                                                                       "-c",
                                                                                                       String.format(
                                                                                                               "kafka-run-class kafka.tools.GetOffsetShell --broker-list localhost:9092" +
                                                                                                                       " --topic %s --offsets -1",
                                                                                                               topicName));
        return Integer.parseInt(execResult.getStdout().strip().split(":")[2]);
    }

    public static void setupKafka(KafkaContainer kafkaContainer, int kafkaPort) {
        kafkaContainer.start();
        System.setProperty("kafka-out.brokers", "localhost:" + kafkaContainer.getMappedPort(kafkaPort).toString());
    }
}
