/*
 * This code was generated by AWS Flow Framework Annotation Processor.
 * Refer to Amazon Simple Workflow Service documentation at http://aws.amazon.com/documentation/swf 
 *
 * Any changes made directly to this file will be lost when 
 * the code is regenerated.
 */
 package com.eucalyptus.loadbalancing.workflow;

import com.amazonaws.services.simpleworkflow.flow.DataConverter;
import com.amazonaws.services.simpleworkflow.flow.StartWorkflowOptions;
import com.amazonaws.services.simpleworkflow.flow.WorkflowClientExternalBase;
import com.amazonaws.services.simpleworkflow.flow.generic.GenericWorkflowClientExternal;
import com.amazonaws.services.simpleworkflow.model.WorkflowExecution;
import com.amazonaws.services.simpleworkflow.model.WorkflowType;

class ModifyLoadBalancerAttributesWorkflowClientExternalImpl extends WorkflowClientExternalBase implements ModifyLoadBalancerAttributesWorkflowClientExternal {

    public ModifyLoadBalancerAttributesWorkflowClientExternalImpl(WorkflowExecution workflowExecution, WorkflowType workflowType, 
            StartWorkflowOptions options, DataConverter dataConverter, GenericWorkflowClientExternal genericClient) {
        super(workflowExecution, workflowType, options, dataConverter, genericClient);
    }

    @Override
    public void modifyLoadBalancerAttributes(String accountNumber, String loadbalancer, com.eucalyptus.loadbalancing.common.msgs.LoadBalancerAttributes attributes) { 
        modifyLoadBalancerAttributes(accountNumber, loadbalancer, attributes, null);
    }

    @Override
    public void modifyLoadBalancerAttributes(String accountNumber, String loadbalancer, com.eucalyptus.loadbalancing.common.msgs.LoadBalancerAttributes attributes, StartWorkflowOptions startOptionsOverride) {
    
        Object[] _arguments_ = new Object[3]; 
        _arguments_[0] = accountNumber;
        _arguments_[1] = loadbalancer;
        _arguments_[2] = attributes;
        dynamicWorkflowClient.startWorkflowExecution(_arguments_, startOptionsOverride);
    }


    @Override
    public com.eucalyptus.loadbalancing.workflow.ElbWorkflowState getState()  {
        com.eucalyptus.loadbalancing.workflow.ElbWorkflowState _state_ = null;
        try {
            _state_ = dynamicWorkflowClient.getWorkflowExecutionState(com.eucalyptus.loadbalancing.workflow.ElbWorkflowState.class);
        } catch (Throwable _failure_) {
            if (_failure_ instanceof RuntimeException) {
                throw (RuntimeException) _failure_;
            } else if (_failure_ instanceof Error) {
                throw (Error) _failure_;
            } else {
                throw new RuntimeException("Unknown exception.", _failure_);
            }
        }

        return _state_;
    }
}