<template>
  <div class="container">
    <div class="top-header">
      <div class="nav-left">
        <Tab :buttons="[
          { label: '全部', value: 'null' },
          { label: '私人食谱', value: '0' },
          { label: '公开食谱', value: '1' }
        ]" initialActive="null" @change="handleChange" />
      </div>
      <div class="nav-right">
        <div>
          <AutoInput placeholder="搜索食谱" @listener="listener" />
        </div>
        <div style="margin-right: 0;" class="primary-bt" @click="saveRecipe">
          <i class="el-icon-plus"></i>
          新增食谱
        </div>
      </div>
    </div>
    <!-- 表格及分页信息 -->
    <div>
      <el-table :data="apiResult.data">
        <el-table-column prop="cover" width="150" label="图标">
          <template #default="scope">
            <img style="width: 70px;height: 50px;border-radius: 5px;" :src="scope.row.cover" alt="">
          </template>
        </el-table-column>
        <el-table-column prop="name" label="食谱名"></el-table-column>
        <el-table-column prop="name" label="审核状态">
          <template #default="scope">
            <div v-if="!scope.row.isPublic">
              私人无需审核
            </div>
            <div v-else>
              <el-tag size="mini" :type="scope.row.isAudit ? 'success' : 'warning'">{{ scope.row.isAudit ? '已审核' : '未审核'
              }}</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <div class="operate-buttons">
              <el-dropdown trigger="click" placement="bottom-end">
                <span class="el-dropdown-link">
                  <i class="el-icon-more"></i>
                </span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item @click.native="selectedModel(scope.row)" icon="el-icon-finished">
                    选中食谱项
                  </el-dropdown-item>
                  <el-dropdown-item @click.native="handleEdit(scope.row.id)" icon="el-icon-edit">
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
          :current-page="recipeQueryDto.current" :page-sizes="[10, 20]" :page-size="recipeQueryDto.size"
          layout="total, sizes, prev, pager, next, jumper" :total="apiResult.total"></el-pagination>
      </div>
    </div>

    <el-drawer :title="isCreateMode ? '新增食谱' : '修改食谱'" size="40%" :visible.sync="drawer" :direction="direction"
      :before-close="handleClose">
      <div style="padding: 10px 30px  10px 0;">
        <el-form ref="recipe" :model="recipe" label-width="80px">
          <el-form-item label="*封面">
            <div>
              <img style="width: 120px;height: 80px;border-radius: 5px;" v-if="cover" :src="cover || ''" alt="">
              <el-upload class="avatar-uploader" :action="fileUploadApi" :show-file-list="false"
                :on-success="handleImageSuccess">
                <i style="font-size: 14px;" class="el-icon-picture">点击此上传/替换图片</i>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="*名称">
            <el-input placeholder="请输入食谱名，50个字以内" v-model="recipe.name"></el-input>
          </el-form-item>
          <el-form-item label="*类别">
            <el-radio-group v-model="recipe.typeId">
              <el-radio v-for="recipeType in recipeTypes" :key="recipeType.value" :label="recipeType.value">{{
                recipeType.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="*权限">
            <el-radio-group v-model="recipe.isPublic">
              {{ recipe.isPublic }}
              <el-radio v-for="isPublic in isPublicList" :key="isPublic.value" :label="isPublic.value">{{
                isPublic.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="*内容">
            <Editor @on-listener="onListener" :receiveContent="content" height="300px" :api="fileUploadApi" />
          </el-form-item>
          <el-form-item>
            <el-button v-if="isCreateMode" type="primary" @click="onSaveRecipe">立即新增</el-button>
            <el-button v-else type="primary" @click="onUpdateRecipe">立即修改</el-button>
            <el-button @click="handleClose">取消</el-button>
          </el-form-item>
        </el-form>
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
// B站【程序员辰星】原创出品
import AutoInput from "@/components/AutoInput.vue";
import Tab from "@/components/Tab" // 导入封装好的Tab组件
import Editor from "@/components/Editor"
export default {
  components: { AutoInput, Tab, Editor },
  data() {
    return {
      fileUploadApi: '/api/v1.0/self-health-api/file/upload',
      isPublicList: [
        { label: '私人食谱', value: 0 },
        { label: '公开食谱', value: 1 }
      ],
      drawer: false,
      direction: 'rtl',
      cover: '',
      content: '',
      recipe: {
        id: null,
        typeId: null,
        isPublic: 0,
      },
      isCreateMode: true, // 是否是新增模式
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
    this.fetchRecipeType();
  },
  methods: {
    onListener(content) {
      this.content = content;
    },
    selectedModel(model) {
      this.$emit('selected', model);
    },
    handleChange(obj) {
      this.recipeQueryDto.isPublic = Number(obj.value);
      this.fetchFreshData();
    },
    handleRecipePublicChange(obj) {
      this.recipeQueryDto.isPublic = Number(obj.value);
    },
    normalValueText(recipe) {
      const valueRange = recipe.normalValue.split(',');
      return `下限值：${valueRange[0]}${recipe.unit}，上限值：${valueRange[1]}${recipe.unit}`
    },
    async fetchRecipeType() {
      try {
        const { data } = await this.$axios.get('/recipe/fetchRecipeTypeList');
        this.recipeTypes = data;
        this.recipe.typeId = this.recipeTypes[0].value;
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    async onUpdateRecipe() {
      try {
        this.recipe.cover = this.cover;
        this.recipe.content = this.content;
        const { message } = await this.$axios.put('/recipe/update', this.recipe);
        this.$message.success(message);
        this.handleClose();
        this.fetchFreshData();
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    async onSaveRecipe() {
      try {
        this.recipe.cover = this.cover;
        this.recipe.content = this.content;
        const { message } = await this.$axios.post('/recipe/save', this.recipe);
        this.$message.success(message);
        this.fetchFreshData();
        this.handleClose();
      } catch (error) {
        this.$message.info(error.message);
      }
    },
    saveRecipe() {
      this.drawer = true;
    },
    handleClose() {
      this.drawer = false;
      this.isCreateMode = true;
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
        const { data, total } = await this.$axios.post('/recipe/listUser', this.recipeQueryDto);
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
        console.log(data);
        
        this.recipe = data;
        this.cover = data.cover;
        this.drawer = true;
        this.content = data.content;
        this.isCreateMode = false;
        this.recipe.isPublic = data.isPublic ? 1 : 0;
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
  display: flex;
  justify-content: flex-end;
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