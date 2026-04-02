<template>
  <div class="container">
    <div class="top-header">
      <div class="nav-left"></div>
      <div class="nav-right">
        <div>
          <AutoInput placeholder="搜索健康资讯" @listener="listener" />
        </div>
        <div class="primary-bt" @click="saveHealthNews">
          <i class="el-icon-plus"></i>
          新增健康资讯
        </div>
      </div>
    </div>
    <!-- 表格及分页信息 -->
    <div>
      <el-table :data="apiResult.data">
        <el-table-column prop="title" label="标题"></el-table-column>
        <el-table-column prop="showCount" :sortable="true" width="88" label="展现量"></el-table-column>
        <el-table-column prop="clickRate" :sortable="true" width="88" label="点击率">
          <template #default="scope">
            <div>{{ scope.row.clickRate }}%</div>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" :sortable="true" width="88" label="阅读量"></el-table-column>
        <el-table-column prop="upvoteCount" :sortable="true" width="88" label="点赞量"></el-table-column>
                <el-table-column prop="collectionCount" :sortable="true" width="88" label="收藏量"></el-table-column>
        <el-table-column prop="createTime" :sortable="true" width="168" label="发布时间"></el-table-column>
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
          :current-page="healthNewsQueryDto.current" :page-sizes="[10, 20]" :page-size="healthNewsQueryDto.size"
          layout="total, sizes, prev, pager, next, jumper" :total="apiResult.total"></el-pagination>
      </div>
    </div>

    <el-drawer :title="dialogControlOperation ? '新增健康资讯' : '修改健康资讯'" size="40%" :visible.sync="drawer"
      :direction="direction" :before-close="handleClose">
      <div style="padding: 10px 30px  10px 0;">
        <el-form ref="healthNews" :model="healthNews" label-width="80px">
          <el-form-item label="标题">
            <el-input placeholder="请输入标题，30个字以内" v-model="healthNews.title"></el-input>
          </el-form-item>
          <el-form-item label="封面">
            <div>
              <img style="width: 60%;height: 170px;border-radius: 5px;" v-if="healthNews.cover"
                :src="healthNews.cover || ''" alt="">
              <el-upload class="avatar-uploader" :action="fileUploadApi" :show-file-list="false"
                :on-success="handleImageSuccess">
                <i style="font-size: 16px;" class="el-icon-picture">点击此上传/替换图片</i>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="类别">
            <el-radio-group v-model="healthNews.typeId">
              <el-radio style="margin-top: 13px;" v-for="(option, index) in healthNewsTypes" :key="index" :label="option.value">
                {{ option.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="摘要">
            <el-input placeholder="请输入摘要，200个字以内" type="textarea" :rows="4" v-model="healthNews.summary"></el-input>
          </el-form-item>
          <el-form-item label="内容">
            <Editor @on-listener="onListener" :receiveContent="healthNews.content" height="300px"
              :api="fileUploadApi" />
          </el-form-item>
          <el-form-item>
            <el-button v-if="dialogControlOperation" type="primary" @click="onSaveHealthNews">立即新增</el-button>
            <el-button v-else type="primary" @click="onUpdateHealthNews">立即修改</el-button>
            <el-button @click="handleClose">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-drawer>

    <!-- 删除确认弹窗 -->
    <el-dialog title="删除健康资讯" :show-close="false" :visible.sync="dialogDeletedVisible" width="20%">
      <span>确定删除健康资讯数据？</span>
      <span slot="footer" class="dialog-footer">
        <el-button size="mini" @click="dialogDeletedVisible = false">取消</el-button>
        <el-button size="mini" type="primary" @click="confirmDeleted">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>

import AutoInput from "@/components/AutoInput.vue";
import Editor from "@/components/Editor"

export default {
  components: { AutoInput, Editor },
  data() {
    return {
      fileUploadApi: '/api/v1.0/self-health-api/file/upload',
      drawer: false,
      direction: 'rtl',
      healthNews: {
        id: null,
        typeId: null,
        cover: '',
        title: '',
        content: '',
        summary: '',
        createTime: null
      },
      healthNewsTypes: [],
      dialogControlOperation: true,
      id: null,
      apiResult: {
        data: [],
        total: 0,
      },
      healthNewsQueryDto: {
        current: 1,
        size: 10,
        role: null,
        username: null,
      },
      dialogDeletedVisible: false,
    };
  },
  created() {
    this.fetchHealthNewsTypes();
    this.fetchFreshData();
  },
  methods: {
    async onUpdateHealthNews() {
      try {
        const { message } = await this.$axios.put('/health-news/update', this.healthNews);
        this.$message(message);
        this.handleClose();
        this.fetchFreshData();
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    async onSaveHealthNews() {
      try {
        const { message } = await this.$axios.post('/health-news/save', this.healthNews);
        this.$message(message);
        this.fetchFreshData();
        this.handleClose();
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    onListener(content) {
      this.healthNews.content = content;
    },
    saveHealthNews() {
      this.drawer = true;
    },
    handleClose() {
      this.drawer = false;
      this.dialogControlOperation = true;
      this.healthNews = {
        id: null,
        typeId: null,
        cover: '',
        title: '',
        content: '',
        summary: '',
        createTime: null
      };
    },
    async fetchHealthNewsTypes() {
      try {
        const { data } = await this.$axios.get('/health-news/fetchHealthNewsTypes');
        this.healthNewsTypes = data;
      } catch (error) {
        this.$message.error(error.message);
      }
    },
    handleImageSuccess(res) {
      this.$notify({
        title: '封面上传',
        type: res.code === 200 ? 'success' : 'error',
        message: res.code === 200 ? '上传成功' : res.data,
        position: 'buttom-right',
        duration: 1000,
      })
      if (res.code === 200) {
        this.healthNews.cover = res.data;
      }
    },
    listener(text) {
      this.healthNewsQueryDto.title = text;
      this.fetchFreshData();
    },
    async fetchFreshData() {
      try {
        const { data, total } = await this.$axios.post('/health-news/list', this.healthNewsQueryDto);
        this.apiResult.data = data;
        this.apiResult.total = total;
      } catch (error) {
        console.error('查询健康资讯信息异常:', error);
      }
    },
    handleSizeChange(size) {
      this.healthNewsQueryDto.size = size;
      this.healthNewsQueryDto.current = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(current) {
      this.healthNewsQueryDto.current = current;
      this.fetchFreshData();
    },
    async handleEdit(healthNewListVO) {
      try {
        const { data } = await this.$axios.get(`/health-news/${healthNewListVO.id}`);
        this.healthNews = { ...data };
        this.drawer = true;
        this.dialogControlOperation = false;
      } catch (error) {
        this.$message.error("查询健康资讯信息异常");
        console.error('查询健康资讯信息异常:', error);
      }
    },
    handleDelete(row) {
      this.dialogDeletedVisible = true;
      this.id = row.id;
    },
    async confirmDeleted() {
      try {
        const { code } = await this.$axios.delete(`/health-news/${this.id}`);
        if (code === 200) {
          this.$notify.success({
            title: '健康资讯删除',
            message: '删除成功',
            position: 'buttom-right',
            duration: 1000,
          });
          this.dialogDeletedVisible = false;
          this.id = null;
          this.fetchFreshData();
        }
      } catch (error) {
        console.log("删除健康资讯数据异常：", error);
      }
    }
  },
};
</script>

<style scoped lang="scss">
.pager {
  margin-block: 20px;
}

.operate-buttons {
  // opacity: 0;
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