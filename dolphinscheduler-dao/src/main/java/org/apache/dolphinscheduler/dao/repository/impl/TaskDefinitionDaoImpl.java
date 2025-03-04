/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dolphinscheduler.dao.repository.impl;

import org.apache.dolphinscheduler.dao.entity.TaskDefinition;
import org.apache.dolphinscheduler.dao.entity.TaskDefinitionLog;
import org.apache.dolphinscheduler.dao.entity.WorkflowDefinition;
import org.apache.dolphinscheduler.dao.entity.WorkflowTaskRelationLog;
import org.apache.dolphinscheduler.dao.mapper.TaskDefinitionLogMapper;
import org.apache.dolphinscheduler.dao.mapper.TaskDefinitionMapper;
import org.apache.dolphinscheduler.dao.mapper.WorkflowDefinitionMapper;
import org.apache.dolphinscheduler.dao.mapper.WorkflowTaskRelationLogMapper;
import org.apache.dolphinscheduler.dao.repository.BaseDao;
import org.apache.dolphinscheduler.dao.repository.TaskDefinitionDao;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;

@Repository
@Slf4j
public class TaskDefinitionDaoImpl extends BaseDao<TaskDefinition, TaskDefinitionMapper> implements TaskDefinitionDao {

    @Autowired
    private WorkflowDefinitionMapper workflowDefinitionMapper;

    @Autowired
    private WorkflowTaskRelationLogMapper workflowTaskRelationLogMapper;

    @Autowired
    private TaskDefinitionLogMapper taskDefinitionLogMapper;

    public TaskDefinitionDaoImpl(@NonNull TaskDefinitionMapper taskDefinitionMapper) {
        super(taskDefinitionMapper);
    }

    @Override
    public List<TaskDefinition> getTaskDefinitionListByDefinition(long workflowDefinitionCode) {
        WorkflowDefinition workflowDefinition = workflowDefinitionMapper.queryByCode(workflowDefinitionCode);
        if (workflowDefinition == null) {
            log.error("Cannot find process definition, code: {}", workflowDefinitionCode);
            return Lists.newArrayList();
        }

        List<WorkflowTaskRelationLog> processTaskRelations =
                workflowTaskRelationLogMapper.queryByWorkflowCodeAndVersion(
                        workflowDefinition.getCode(), workflowDefinition.getVersion());
        Set<TaskDefinition> taskDefinitionSet = processTaskRelations
                .stream()
                .filter(p -> p.getPostTaskCode() > 0)
                .map(p -> new TaskDefinition(p.getPostTaskCode(), p.getPostTaskVersion()))
                .collect(Collectors.toSet());

        if (taskDefinitionSet.isEmpty()) {
            return Lists.newArrayList();
        }
        List<TaskDefinitionLog> taskDefinitionLogs = taskDefinitionLogMapper.queryByTaskDefinitions(taskDefinitionSet);
        return Lists.newArrayList(taskDefinitionLogs);
    }

    @Override
    public TaskDefinition findTaskDefinition(long taskCode, int taskDefinitionVersion) {
        return taskDefinitionLogMapper.queryByDefinitionCodeAndVersion(taskCode, taskDefinitionVersion);
    }

    @Override
    public void deleteByWorkflowDefinitionCodeAndVersion(long workflowDefinitionCode, int workflowDefinitionVersion) {
        mybatisMapper.deleteByWorkflowDefinitionCodeAndVersion(workflowDefinitionCode, workflowDefinitionVersion);
    }

    @Override
    public void deleteByTaskDefinitionCodes(Set<Long> needToDeleteTaskDefinitionCodes) {
        if (CollectionUtils.isEmpty(needToDeleteTaskDefinitionCodes)) {
            return;
        }
        mybatisMapper.deleteByBatchCodes(new ArrayList<>(needToDeleteTaskDefinitionCodes));
    }

    @Override
    public List<TaskDefinition> queryByCodes(Collection<Long> taskDefinitionCodes) {
        if (CollectionUtils.isEmpty(taskDefinitionCodes)) {
            return Collections.emptyList();
        }
        return mybatisMapper.queryByCodeList(taskDefinitionCodes);
    }

    @Override
    public TaskDefinition queryByCode(long taskCode) {
        return mybatisMapper.queryByCode(taskCode);
    }

    @Override
    public List<String> queryAllTaskDefinitionWorkerGroups(long projectCode) {
        return mybatisMapper.queryAllTaskDefinitionWorkerGroups(projectCode);
    }
}
