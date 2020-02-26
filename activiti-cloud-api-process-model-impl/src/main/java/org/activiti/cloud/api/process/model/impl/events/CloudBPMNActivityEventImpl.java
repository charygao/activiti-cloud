/*
 * Copyright 2018 Alfresco, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.cloud.api.process.model.impl.events;

import org.activiti.api.process.model.BPMNActivity;
import org.activiti.api.process.model.events.BPMNActivityEvent;
import org.activiti.cloud.api.model.shared.impl.events.CloudRuntimeEventImpl;

public abstract class CloudBPMNActivityEventImpl extends CloudRuntimeEventImpl<BPMNActivity, BPMNActivityEvent.ActivityEvents> {

    public CloudBPMNActivityEventImpl() {
    }

    public CloudBPMNActivityEventImpl(BPMNActivity entity,
                                      String processDefinitionId,
                                      String processInstanceId) {
        super(entity);
        this.setProcessDefinitionId(processDefinitionId);
        this.setProcessInstanceId(processInstanceId);
        setEntityId(entity.getElementId());
    }

    public CloudBPMNActivityEventImpl(String id,
                                      Long timestamp,
                                      BPMNActivity entity,
                                      String processDefinitionId,
                                      String processInstanceId) {
        super(id,
              timestamp,
              entity);
        this.setProcessDefinitionId(processDefinitionId);
        this.setProcessInstanceId(processInstanceId);
        setEntityId(entity.getElementId());
    }

}
