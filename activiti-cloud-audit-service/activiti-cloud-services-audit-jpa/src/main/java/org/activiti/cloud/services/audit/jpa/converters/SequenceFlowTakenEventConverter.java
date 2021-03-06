package org.activiti.cloud.services.audit.jpa.converters;

import org.activiti.api.process.model.events.SequenceFlowEvent;
import org.activiti.cloud.api.model.shared.events.CloudRuntimeEvent;
import org.activiti.cloud.api.model.shared.impl.events.CloudRuntimeEventImpl;
import org.activiti.cloud.api.process.model.events.CloudSequenceFlowTakenEvent;
import org.activiti.cloud.api.process.model.impl.events.CloudSequenceFlowTakenEventImpl;
import org.activiti.cloud.services.audit.jpa.events.AuditEventEntity;
import org.activiti.cloud.services.audit.jpa.events.SequenceFlowAuditEventEntity;

public class SequenceFlowTakenEventConverter extends BaseEventToEntityConverter {
    
    public SequenceFlowTakenEventConverter(EventContextInfoAppender eventContextInfoAppender) {
        super(eventContextInfoAppender);
    }
    
    @Override
    public String getSupportedEvent() {
        return SequenceFlowEvent.SequenceFlowEvents.SEQUENCE_FLOW_TAKEN.name();
    }

    @Override
    protected SequenceFlowAuditEventEntity createEventEntity(CloudRuntimeEvent cloudRuntimeEvent) {
        return new SequenceFlowAuditEventEntity((CloudSequenceFlowTakenEvent) cloudRuntimeEvent);
    }

    @Override
    protected CloudRuntimeEventImpl<?, ?> createAPIEvent(AuditEventEntity auditEventEntity) {
        SequenceFlowAuditEventEntity sequenceFlowTakenAuditEventEntity = (SequenceFlowAuditEventEntity) auditEventEntity;

        return new CloudSequenceFlowTakenEventImpl(sequenceFlowTakenAuditEventEntity.getEventId(),
                                                   sequenceFlowTakenAuditEventEntity.getTimestamp(),
                                                   sequenceFlowTakenAuditEventEntity.getSequenceFlow());
    }
}
