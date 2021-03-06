package org.activiti.cloud.services.modeling.validation.process;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.CallActivity;
import org.activiti.cloud.modeling.api.Model;
import org.activiti.cloud.modeling.api.ModelValidationError;
import org.activiti.cloud.modeling.api.ProcessModelType;
import org.activiti.cloud.modeling.api.ValidationContext;
import org.activiti.cloud.modeling.core.error.SemanticModelValidationException;
import org.activiti.cloud.services.modeling.converter.BpmnProcessModelContent;
import org.activiti.cloud.services.modeling.converter.ProcessModelContentConverter;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BpmnModelCallActivityValidator implements BpmnModelValidator {

    private ProcessModelType processModelType;
    private ProcessModelContentConverter processModelContentConverter;
    private final String INVALID_CALL_ACTIVITY_REFERENCE_DESCRIPTION = "Call activity '%s' with call element '%s' found in process '%s' references a process id that does not exist in the current project.";
    private final String INVALID_CALL_ACTIVITY_REFERENCE_PROBLEM = "Call activity element must reference a process id present in the current project.";
    private final String INVALID_CALL_ACTIVITY_REFERENCE_NAME = "Invalid call activity reference validator.";
    private final String NO_REFERENCE_FOR_CALL_ACTIVITY_DESCRIPTION = "No call element found for call activity '%s' found in process '%s'. Call activity must have a call element that reference a process id present in the current project.";
    private final String NO_REFERENCE_FOR_CALL_ACTIVITY_PROBLEM = "No call element found for call activity '%s' in process '%s'";
    private final String NO_REFERENCE_FOR_CALL_ACTIVITY_REFERENCE_NAME = "Call activity must have a call element validator.";
    private final String XML_CONTENT_NOT_PRESEND = "Xml content for the model is not present";
    private final String XML_NOT_PARSABLE = "Xml content for the model is not valid.";

    public BpmnModelCallActivityValidator(ProcessModelType processModelType,
                                          ProcessModelContentConverter processModelContentConverter) {
        this.processModelType = processModelType;
        this.processModelContentConverter = processModelContentConverter;
    }

    @Override
    public Stream<ModelValidationError> validate(BpmnModel bpmnModel,
                                                 ValidationContext validationContext) {
        Set<String> availableProcessesIds = validationContext.getAvailableModels(processModelType)
                .stream()
                .flatMap(model -> this.retrieveProcessIdFromModel(model))
                .collect(Collectors.toSet());
        return validateCallActivities(availableProcessesIds,
                                      bpmnModel);
    }

    private Stream<String> retrieveProcessIdFromModel(Model model) throws RuntimeException {
        try {
            return processModelContentConverter.convertToBpmnModel(model.getContent())
                .getProcesses().stream().map(process -> process.getId());
        } catch (IOException ioError) {
            throw new RuntimeException(this.XML_CONTENT_NOT_PRESEND, ioError);
        } catch (XMLStreamException xmlParsingError) {
            throw new RuntimeException(this.XML_NOT_PARSABLE, xmlParsingError);
        }
    }

    private Stream<ModelValidationError> validateCallActivities(Set<String> availableProcessesIds,
                                                                BpmnModel bpmnModel) {
        return processModelContentConverter.convertToModelContent(bpmnModel)
                .map(converter ->
                    this.evaluateProcessCallActivity(converter, availableProcessesIds, bpmnModel))
            .orElse(Stream.empty());
    }

    private Stream<ModelValidationError> evaluateProcessCallActivity(BpmnProcessModelContent converter,
                                                                     Set<String> availableProcessesIds,
                                                                     BpmnModel bpmnModel) {
        Set<CallActivity> availableActivities = converter.findAllNodes(CallActivity.class);
        return availableActivities.stream()
            .map(activity -> validateCallActivity(availableProcessesIds,
                bpmnModel.getMainProcess().getId(),
                activity))
            .filter(Optional::isPresent)
            .map(Optional::get);
    }


    private Optional<ModelValidationError> validateCallActivity(Set<String> availableProcessesIds,
                                                                String mainProcess,
                                                                CallActivity callActivity) {
        String calledElement = callActivity.getCalledElement();
        if (isEmpty(calledElement)) {
            return Optional.of(createModelValidationError(format(NO_REFERENCE_FOR_CALL_ACTIVITY_PROBLEM,
                                                                 callActivity.getId(),
                                                                 mainProcess),
                                                          format(NO_REFERENCE_FOR_CALL_ACTIVITY_DESCRIPTION,
                                                                 callActivity.getId(),
                                                                 mainProcess),
                                                          NO_REFERENCE_FOR_CALL_ACTIVITY_REFERENCE_NAME)
            );
        } else {
            String calledElementId = calledElement.replace("process-",
                                                           "");
            return !availableProcessesIds.contains(calledElementId) ?
                    Optional.of(createModelValidationError(INVALID_CALL_ACTIVITY_REFERENCE_PROBLEM,
                                                           format(INVALID_CALL_ACTIVITY_REFERENCE_DESCRIPTION,
                                                                  callActivity.getId(),
                                                                  calledElementId,
                                                                  mainProcess,
                                                                  INVALID_CALL_ACTIVITY_REFERENCE_NAME),
                                                           INVALID_CALL_ACTIVITY_REFERENCE_NAME)) : Optional.empty();
        }
    }
}
