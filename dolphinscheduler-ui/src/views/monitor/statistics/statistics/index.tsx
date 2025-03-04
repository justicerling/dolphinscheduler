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

import { defineComponent } from 'vue'
import { NGrid, NGi, NTabs, NTabPane, NCard } from 'naive-ui'
import ListCommandTable from './list-command-table'
import ListErrorCommandTable from './list-error-command-table'
import { useI18n } from 'vue-i18n'

const statistics = defineComponent({
  name: 'statistics',
  setup() {},

  render() {
    const { t } = useI18n()
    return (
      <NGrid x-gap='12' y-gap='8' cols='1' responsive='screen'>
        <NGi>
          <NCard>
            <NTabs
              type='card'
              animated
              pane-style={{
                padding: '0px',
                border: 'none'
              }}
            >
              <NTabPane
                name='command'
                tab={t('monitor.statistics.command_statistics_list')}
                display-directiv='show'
              >
                <ListCommandTable></ListCommandTable>
              </NTabPane>
              <NTabPane
                name='command-error'
                tab={t('monitor.statistics.failure_command_statistics_list')}
                display-directiv='show'
              >
                <ListErrorCommandTable></ListErrorCommandTable>
              </NTabPane>
            </NTabs>
          </NCard>
        </NGi>
      </NGrid>
    )
  }
})

export default statistics
