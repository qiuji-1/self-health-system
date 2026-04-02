<template>
  <div class="container">
    <div class="top-header">
      <div class="nav-left">
        我的饮食记录
      </div>
      <div class="nav-right">
        <div>
          <AutoInput placeholder="搜索饮食记录" @listener="listener" />
        </div>
      </div>
    </div>
    <!-- 表格及分页信息 -->
    <div>
      <el-table :data="apiResult.data">
        <el-table-column prop="detail" width="150" label="备注">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.detail }}-{{ scope.row.value }}g</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="cover" width="80" label="食谱封面">
          <template #default="scope">
            <img style="width: 50px;height: 30px;border-radius: 5px;" :src="scope.row.recipeCover" alt="">
          </template>
        </el-table-column> -->
        <el-table-column prop="recipeName" label="食谱名"></el-table-column>
        <el-table-column prop="createTime" width="168" label="记录时间"></el-table-column>
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
          :current-page="recipeQueryDto.current" :page-sizes="[10, 20]" :page-size="recipeQueryDto.size"
          layout="total, sizes, prev, pager, next, jumper" :total="apiResult.total"></el-pagination>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <el-dialog title="删除健康饮食记录" :show-close="false" :visible.sync="dialogDeletedVisible" width="20%">
      <span>确定删除饮食记录数据？</span>
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
export default {
  components: { AutoInput },
  props: {
    notifyFlag: {
      type: Boolean,
      required: true,
    },
  },
  watch: {
    notifyFlag: {
      handler(newValue) {
        if (newValue) {
          this.fetchFreshData();
        }
      },
      deep: true,
      immediate: true
    },
  },
  data() {
    return {
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
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    listener(text) {
      this.recipeQueryDto.detail = text;
      this.fetchFreshData();
    },
    async fetchFreshData() {
      try {
        const { data, total } = await this.$axios.post('/diet-history/listUser', this.recipeQueryDto);
        this.apiResult.data = data;
        this.apiResult.total = total;
      } catch (error) {
        console.error('查询饮食记录信息异常:', error);
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
    handleDelete(row) {
      this.dialogDeletedVisible = true;
      this.id = row.id;
    },
    async confirmDeleted() {
      try {
        const { code } = await this.$axios.delete(`/diet-history/${this.id}`);
        if (code === 200) {
          this.$notify.success({
            title: '健康饮食记录删除',
            message: '删除成功',
            position: 'buttom-right',
            duration: 1000,
          });
          this.dialogDeletedVisible = false;
          this.id = null;
          this.fetchFreshData();
        }
      } catch (error) {
        console.log("删除健康饮食记录数据异常：", error);
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
    font-size: 18px;
  }
}
</style>