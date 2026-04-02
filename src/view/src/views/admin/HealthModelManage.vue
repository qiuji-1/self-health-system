<template>
  <div class="container">
    <div class="top-header">
      <div class="nav-left">
        <Tab :buttons="[
          { label: '全部', value: 'null' },
          { label: '公共模组', value: '0' },
          { label: '私人模组', value: '1' }
        ]" initialActive="null" @change="handleChange" />
      </div>
      <div class="nav-right">
        <div>
          <AutoInput placeholder="搜索健康模组" @listener="listener" />
        </div>
        <div class="primary-bt" @click="savehealthModel">
          <i class="el-icon-plus"></i>
          新增健康模组
        </div>
      </div>
    </div>
    <!-- 表格及分页信息 -->
    <div>
      <el-table :data="apiResult.data">
        <el-table-column prop="iconUrl" width="80" label="图标">
          <template #default="scope">
            <img style="width: 30px;height: 30px;" :src="scope.row.iconUrl" alt="">
          </template>
        </el-table-column>
        <el-table-column prop="name" width="120" label="模组名">
          <template #default="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.detail" placement="right">
              <div>{{ scope.row.name }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="unit" width="100" label="单位"></el-table-column>
        <el-table-column prop="symbol" width="100" label="符号"></el-table-column>
        <el-table-column prop="normalValue" label="阈值">
          <template #default="scope">
            <div>{{ normalValueText(scope.row) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="normalValue" width="120" label="模组权限">
          <template #default="scope">
            <div class="model">
              <span v-if="scope.row.isGlobal" class="pri"></span>
              <span v-else class="pub"></span>
              {{ scope.row.isGlobal ? '私有模组' : '公共模组' }}
            </div>
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
                  <el-dropdown-item @click.native="handleEdit(scope.row)" icon="el-icon-edit">
                    修改
                  </el-dropdown-item>
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
          :current-page="healthModelQueryDto.current" :page-sizes="[10, 20]" :page-size="healthModelQueryDto.size"
          layout="total, sizes, prev, pager, next, jumper" :total="apiResult.total"></el-pagination>
      </div>
    </div>

    <el-drawer :title="isCreateMode ? '新增健康模组' : '修改健康模组'" size="40%" :visible.sync="drawer" :direction="direction"
      :before-close="handleClose">
      <div style="padding: 10px 30px  10px 0;">
        <el-form ref="healthModel" :model="healthModel" label-width="80px">
          <el-form-item label="*图标">
            <div>
              <img style="width: 60px;height: 60px;border-radius: 5px;" v-if="iconUrl" :src="iconUrl || ''" alt="">
              <el-upload class="avatar-uploader" :action="fileUploadApi" :show-file-list="false"
                :on-success="handleImageSuccess">
                <i style="font-size: 14px;" class="el-icon-picture">点击此上传/替换图片</i>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="*名称">
            <el-input placeholder="请输入模组名，100个字以内" v-model="healthModel.name"></el-input>
          </el-form-item>
          <el-form-item label="*单位">
            <el-input placeholder="请输入模组单位" v-model="healthModel.unit"></el-input>
          </el-form-item>
          <el-form-item label="*符号">
            <el-input placeholder="请输入模组符号" v-model="healthModel.symbol"></el-input>
          </el-form-item>
          <el-form-item label="*阈值">
            <el-input placeholder="请输入正常阈值，格式：xxx,xxx" v-model="healthModel.normalValue"></el-input>
          </el-form-item>
          <el-form-item label="*简介">
            <el-input placeholder="请输入模组简介，200个字以内" type="textarea" :rows="4" v-model="healthModel.detail"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button v-if="isCreateMode" type="primary" @click="onSavehealthModel">立即新增</el-button>
            <el-button v-else type="primary" @click="onUpdatehealthModel">立即修改</el-button>
            <el-button @click="handleClose">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>

    <!-- 删除确认弹窗 -->
    <el-dialog title="删除健康模组" :show-close="false" :visible.sync="dialogDeletedVisible" width="20%">
      <span>确定删除健康模组数据？</span>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="dialogDeletedVisible = false">取消</el-button>
        <el-button size="mini" type="primary" @click="confirmDeleted">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

import AutoInput from "@/components/AutoInput.vue";
import Tab from "@/components/Tab" // 导入封装好的Tab组件
export default {
  components: { AutoInput, Tab },
  data() {
    return {
      fileUploadApi: '/api/v1.0/self-health-api/file/upload',
      drawer: false,
      direction: 'rtl',
      iconUrl: '',
      healthModel: {
        id: null,
        name: '',
        detail: '',
        iconUrl: null,
        unit: '',
        symbol: '',
        normalVaalue: '',
        userId: null,
        isGlobal: null,
        createTime: null
      },
      isCreateMode: true, // 是否是新增模式
      id: null,
      apiResult: {
        data: [],
        total: 0,
      },
      healthModelQueryDto: {
        current: 1,
        size: 10,
        role: null,
        username: null,
      },
      dialogDeletedVisible: false,
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    handleChange(obj) {
      this.healthModelQueryDto.isGlobal = Number(obj.value);
      this.handleCurrentChange(1);
    },
    normalValueText(healthModel) {
        const valueRange = healthModel.normalValue.split(',');
  return `${valueRange[0]}~${valueRange[1]}${healthModel.unit}`;
    },
    async onUpdatehealthModel() {
      try {
        this.healthModel.iconUrl = this.iconUrl;
        const { message } = await this.$axios.put('/health-model/update', this.healthModel);
        this.$message.success(message);
        this.handleClose();
        this.fetchFreshData();
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    async onSavehealthModel() {
      try {
        this.healthModel.iconUrl = this.iconUrl;
        const { message } = await this.$axios.post('/health-model/save', this.healthModel);
        this.$message.success(message);
        this.fetchFreshData();
        this.handleClose();
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    savehealthModel() {
      this.drawer = true;
    },
    handleClose() {
      this.drawer = false;
      this.isCreateMode = true;
      this.healthModel = {
        id: null,
        typeId: null,
        cover: '',
        title: '',
        content: '',
        summary: '',
        createTime: null
      };
      this.iconUrl = '';
    },
    handleImageSuccess(res) {
      this.$notify({
        title: '图标上传',
        type: res.code === 200 ? 'success' : 'error',
        message: res.code === 200 ? '上传成功' : res.data,
        position: 'buttom-right',
        duration: 1000,
      })
      if (res.code === 200) {
        this.iconUrl = res.data;
        console.log(this.healthModel.iconUrl);
      }
    },
    listener(text) {
      this.healthModelQueryDto.name = text;
      this.fetchFreshData();
    },
    async fetchFreshData() {
      try {
        const { data, total } = await this.$axios.post('/health-model/list', this.healthModelQueryDto);
        this.apiResult.data = data;
        this.apiResult.total = total;
      } catch (error) {
        console.error('查询健康模组信息异常:', error);
      }
    },
    handleSizeChange(size) {
      this.healthModelQueryDto.size = size;
      this.healthModelQueryDto.current = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(current) {
      this.healthModelQueryDto.current = current;
      this.fetchFreshData();
    },
    handleEdit(data) {
      this.healthModel = { ...data };
      this.iconUrl = data.iconUrl;
      this.drawer = true;
      this.isCreateMode = false;
    },
    handleDelete(row) {
      this.dialogDeletedVisible = true;
      this.id = row.id;
    },
    async confirmDeleted() {
      try {
        const { code } = await this.$axios.delete(`/health-model/${this.id}`);
        if (code === 200) {
          this.$notify.success({
            title: '健康模组删除',
            message: '删除成功',
            position: 'buttom-right',
            duration: 1000,
          });
          this.dialogDeletedVisible = false;
          this.id = null;
          this.fetchFreshData();
        }
      } catch (error) {
        console.log("删除健康模组数据异常：", error);
      }
    }
  },
};
</script>

<style scoped lang="scss">
.model {
  .pri {
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: rgb(51, 51, 51);
    margin-right: 5px;
  }

  .pub {
    display: inline-block;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background-color: rgb(148, 165, 34);
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