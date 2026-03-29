<template>
  <div class="container">
    <div class="top-header">
      <div class="nav-left">
        <Tab :buttons="[
          { label: '全部', value: 'null' },
          { label: '未审核', value: '0' },
          { label: '已审核', value: '1' }
        ]" initialActive="null" @change="handleChange" />
      </div>
      <div class="nav-right">
        <div>
          <AutoInput placeholder="搜索食谱" @listener="listener" />
        </div>
      </div>
    </div>
    <!-- 表格及分页信息 -->
    <div>
      <el-table :data="apiResult.data">
        <el-table-column prop="cover" width="80" label="头像">
          <template #default="scope">
            <img style="width: 30px;height: 30px;border-radius: 4px;" :src="scope.row.avatar" alt="">
          </template>
        </el-table-column>
        <el-table-column prop="username" width="120" label="发布者"></el-table-column>
        <el-table-column prop="cover" width="80" label="食谱封面">
          <template #default="scope">
            <img style="width: 50px;height: 30px;border-radius: 5px;" :src="scope.row.cover" alt="">
          </template>
        </el-table-column>
        <el-table-column prop="name" label="食谱名"></el-table-column>
        <el-table-column prop="audit" width="150" label="审核状态">
          <template #default="scope">
            <el-tag size="mini" :type="scope.row.isAudit ? 'success' : 'warning'">{{ scope.row.isAudit ? '已审核' :
              '未审核' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="audit" width="150" label="公开状态">
          <template #default="scope">
            <div>{{ scope.row.isPublic ? '公开' : '私人' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" width="168" label="发布时间"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <div class="operate-buttons">
              <el-dropdown trigger="click" placement="bottom-end">
                <span class="el-dropdown-link">
                  <i class="el-icon-more"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item @click.native="detailRecipe(scope.row)" icon="el-icon-finished">
                    详情
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
          :current-page="recipeQueryDto.current" :page-sizes="[10, 20]" :page-size="recipeQueryDto.size"
          layout="total, sizes, prev, pager, next, jumper" :total="apiResult.total"></el-pagination>
      </div>
    </div>

    <el-drawer title="食谱详情" size="40%" :visible.sync="drawer" :direction="direction" :before-close="handleClose">
      <div style="padding: 10px 30px  10px 20px;">
        <div style="display: flex;justify-content: left;align-items: center;gap: 6px;">
          <img style="width: 30px;height: 30px;border-radius: 50%;" :src="recipe.avatar" alt="">
          <div>
            <div>{{ recipe.username }}</div>
            <div style="font-size: 12px;color: #666;">发布于{{ recipe.createTime }}</div>
          </div>
        </div>
        <div v-html="content"></div>
        <div style="margin-left: 10px;">
          <el-button v-if="recipe.isPublic && !recipe.isAudit" type="primary" icon="el-icon-success"
            @click="auditRecipe">通过审核</el-button>
          <el-button @click="handleClose">取消</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 删除确认弹窗 -->
    <el-dialog title="删除健康食谱" :show-close="false" :visible.sync="dialogDeletedVisible" width="20%">
      <span>确定删除食谱数据？</span>
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
import Editor from "@/components/Editor"
export default {
  components: { AutoInput, Tab, Editor },
  data() {
    return {
      isPublicList: [
        { label: '私人食谱', value: 0 },
        { label: '公开食谱', value: 1 }
      ],
      drawer: false,
      direction: 'rtl',
      content: '',
      recipe: {
        id: null,
        typeId: null,
        isPublic: 0,
      },
      id: null,
      apiResult: {
        data: [],
        total: 0,
      },
      recipeQueryDto: {
        current: 1,
        size: 10,
      },
      dialogDeletedVisible: false,
      recipeTypes: [],
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    onListener(content) {
      this.recipe.content = content;
    },
    async detailRecipe(recipe) {
      try {
        this.recipe = { ...recipe };
        const { data } = await this.$axios.get(`/recipe/${recipe.id}`);
        this.drawer = true;
        this.content = data.content;
      } catch (error) {
        console.log("通过ID查询食谱数据异常：", error);
      }
    },
    handleChange(obj) {
      this.recipeQueryDto.isAudit = Number(obj.value);
      this.fetchFreshData();
    },
    async auditRecipe() {
      try {
        const { message } = await this.$axios.put(`/recipe/audit/${this.recipe.id}`);
        this.$message.success(message);
        this.fetchFreshData();
        this.handleClose();
      } catch (error) {
        this.$message.error(error.message);
      }
    },
    handleClose() {
      this.drawer = false;
      this.cover = '';
      this.content = '';
      this.recipe = {
        id: null,
        typeId: null,
        isPublic: null,
      };
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
        this.cover = res.data;
      }
    },
    listener(text) {
      this.recipeQueryDto.name = text;
      this.fetchFreshData();
    },
    async fetchFreshData() {
      try {
        const { data, total } = await this.$axios.post('/recipe/list', this.recipeQueryDto);
        this.apiResult.data = data;
        this.apiResult.total = total;
      } catch (error) {
        console.error('查询食谱信息异常:', error);
      }
    },
    handleSizeChange(size) {
      this.recipeQueryDto.size = size;
      this.recipeQueryDto.current = 1;
      this.fetchFreshData();
    },
    handleCurrentChange(current) {
      this.recipeQueryDto.current = current;
      this.fetchFreshData();
    },
    async handleEdit(id) {
      try {
        const { data } = await this.$axios.get(`/recipe/${id}`);
        this.recipe = data;
        this.drawer = true;
        this.content = data.content;
      } catch (error) {
        console.log("通过ID查询食谱数据异常：", error);
      }

    },
    handleDelete(row) {
      this.dialogDeletedVisible = true;
      this.id = row.id;
    },
    async confirmDeleted() {
      try {
        const { code } = await this.$axios.delete(`/recipe/${this.id}`);
        if (code === 200) {
          this.$notify.success({
            title: '健康食谱删除',
            message: '删除成功',
            position: 'buttom-right',
            duration: 1000,
          });
          this.dialogDeletedVisible = false;
          this.id = null;
          this.fetchFreshData();
        }
      } catch (error) {
        console.log("删除健康食谱数据异常：", error);
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
  margin-block: 20px;
  // padding-inline: 10px;
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