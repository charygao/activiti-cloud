package org.activiti.cloud.connectors.starter.test.it;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MockCloudRuntimeEventsChannels {

    String COMMAND_CONSUMER = "commandConsumer";

    String COMMAND_RESULTS = "commandResults";

    String AUDIT_PRODUCER = "auditProducer";

    @Input(COMMAND_CONSUMER)
    SubscribableChannel commandConsumer();

    @Output(COMMAND_RESULTS)
    MessageChannel commandResults();

    @Output(AUDIT_PRODUCER)
    MessageChannel auditProducer();
}
