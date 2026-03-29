<template>
  <div class="container">
    <div class="top-header">
      <div class="nav-left">
        <div class="model">
          <div>
            <span class="pri"></span> 正常
          </div>
          <div>
            <span class="pub"></span>异常
          </div>
        </div>
      </div>
      <div class="nav-right">
        <div>
          <el-select @change="fetchFreshData" size="mini" v-model="healthRecordQueryDto.healthModelId" placeholder="请选择模组">
            <el-option v-for="(item, index) in healthModelOptions" :key="index" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
        </div>
      </div>
    </div>
    <!-- 表格及分页信息 -->
    <div>
      <el-table :data="apiResult.data">
        <el-table-column prop="avatar" width="50" label="头像">
          <template #default="scope">
            <img style="width: 30px;height: 30px;" :src="scope.row.avatar" alt="">
          </template>
        </el-table-column>
        <el-table-column prop="username" width="180" label="记录者"></el-table-column>
        <el-table-column prop="healthModelName" width="100" label="记录项"></el-table-column>
        <el-table-column prop="healthModelUnit" width="100" label="单位"></el-table-column>
        <el-table-column prop="normalValue" label="指标情况">
          <template #default="scope">
            <div class="model">
              <span v-if="normalValueText(scope.row)" class="pri"></span>
              <span v-else class="pub"></span>
              {{ normalValueText(scope.row) ? '正常' : '异常' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="value" width="120" label="记录值">
          <template #default="scope">
            <div>
              {{ scope.row.value }}{{ scope.row.healthModelUnit }}
            </div>
          </template>
        </el-table-column>
        <el-table-column width="220" prop="normalValue" label="阈值">
          <template #default="scope">
            <div>{{ normalValueRangeText(scope.row) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" :sortable="true" width="168" label="创建时间"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <div class="operate-buttons">
              <el-dropdown trigger="click" placement="bottom-end">
                <span class="el-dropdown-link">
                  <i class="el-icon-more"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item @click.native="handleDelete(scope.row)" icon="el-icon-delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页组件区域 -->
      <div class="pager">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
          :current-page="healthRecordQueryDto.current" :page-sizes="[10, 20]" :page-size="healthRecordQueryDto.size"
          layout="total, sizes, prev, pager, next, jumper" :total="apiResult.total"></el-pagination>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <el-dialog title="删除健康记录" :show-close="false" :visible.sync="dialogDeletedVisible" width="20%">
      <span>确定删除健康记录数据？</span>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="dialogDeletedVisible = false">取消</el-button>
        <el-button size="mini" type="primary" @click="confirmDeleted">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: null,
      apiResult: {
        data: [],
        total: 0,
      },
      healthRecordQueryDto: {
        current: 1,
        size: 10,
        healthModelId: null,
      },
      dialogDeletedVisible: false,
      healthModelOptions: [],
    };
  },
  created() {
    this.fetchHealthModelOptions();
    this.fetchFreshData();
  },
  methods: {
    normalValueRangeText(healthModel) {
      const valueRange = healthModel.normalValue.split(',');
      return `下限值：${valueRange[0]}${healthModel.healthModelUnit};上限值：${valueRange[1]}${healthModel.healthModelUnit}`
    },
    normalValueText(healthModel) {
      const valueRange = healthModel.normalValue.split(',');
      // true ： 正常情况；false：异常情况
      return healthModel.value > valueRange[0] && healthModel.value < valueRange[1];
    },
    async fetchHealthModelOptions() {
      try {
        const { data } = await this.$axios.get('/health-model/options');
        this.healthModelOptions = data;
        this.healthModelOptions.unshift({ value: null, label: '全部' });
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    async fetchFreshData() {
      try {
        const { data, total } = await this.$axios.post('/health-record/list', this.healthRecordQueryDto);
        this.apiResult.data = data;
        this.apiResult.total = total;
      } catch (error) {
        console.error('查询健康记录信息异常:', error);
      }
    },
    handleSizeChange(size) {
      this.healthRecordQueryDto.size = size;
      this.healthRecordQueryDto.current = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(current) {
      this.healthRecordQueryDto.current = current;
      this.fetchFreshData();
    },
    handleDelete(row) {
      this.dialogDeletedVisible = true;
      this.id = row.id;
    },
    async confirmDeleted() {
      try {
        const { code } = await this.$axios.delete(`/health-record/${this.id}`);
        if (code === 200) {
          this.$notify.success({
            title: '健康记录删除',
            message: '删除成功',
            position: 'buttom-right',
            duration: 1000,
          });
          this.dialogDeletedVisible = false;
          this.id = null;
          this.fetchFreshData();
        }
      } catch (error) {
        console.log("删除健康记录数据异常：", error);
      }
    }
  },
};
</script>

<style scoped lang="scss">
.model {
  display: flex;
  justify-content: left;
  align-items: center;
  gap: 16px;
  font-size: 14px;

  .pri {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: rgb(57, 132, 7);
    margin-right: 5px;
  }

  .pub {
    display: inline-block;
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background-color: rgb(227, 74, 32);
    margin-right: 5px;
  }
}

.pager {
  margin-block: 20px;
}

.operate-buttons {
  //opacity: 0;
  transition: opacity 0.3s;
  cursor: pointer;

  i {
    padding: 8px;
    border-radius: 6px;
    transition: all .5s ease;

    &:hover {
      background-color: rgb(236, 237, 238);
    }
  }
}

.el-table__body tr:hover .operate-buttons {
  opacity: 1;
}

.container {
  margin: 10px 20px;
}

.top-header {
  margin-block: 10px;
  padding-inline: 10px;
  border-radius: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .nav-left,
  .nav-right {
    display: flex;
    justify-content: left;
    align-items: center;
    gap: 10px;
  }
}
</style>