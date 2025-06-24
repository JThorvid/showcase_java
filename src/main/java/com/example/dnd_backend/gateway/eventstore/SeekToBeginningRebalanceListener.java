package com.example.dnd_backend.gateway.eventstore;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SeekToBeginningRebalanceListener implements ConsumerAwareRebalanceListener {

    @Override
    public void onPartitionsAssigned(
            Consumer<?, ?> consumer,
            @NonNull Collection<TopicPartition> partitions) {
        consumer.seekToBeginning(partitions);
    }
}
