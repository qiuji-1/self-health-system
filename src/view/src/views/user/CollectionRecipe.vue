<template>
    <div class="container">
        <div class="top-header">
            <div class="nav-left">
                <i class="el-icon-shopping-cart-full"></i>
                收藏食谱
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
                <el-table-column prop="name" label="标题"></el-table-column>
                <el-table-column prop="createTime" :sortable="true" width="168" label="发布时间"></el-table-column>
                <el-table-column label="" width="150" align="center">
                    <template #default="scope">
                        <div class="operate-buttons">
                            <el-dropdown trigger="click" placement="bottom-end">
                                <span class="el-dropdown-link">
                                    <i class="el-icon-more"></i>
                                </span>
                                <el-dropdown-menu slot="dropdown">
                                    <el-dropdown-item @click.native="view(scope.row)"
                                        icon="el-icon-turn-off">前去阅读</el-dropdown-item>
                                    <el-dropdown-item @click.native="handleDelete(scope.row)"
                                        icon="el-icon-star-off">取消收藏</el-dropdown-item>
                                </el-dropdown-menu>
                            </el-dropdown>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页组件区域 -->
            <div class="pager">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="recipeQueryDto.current" :page-sizes="[10, 20]"
                    :page-size="recipeQueryDto.size" layout="total, sizes, prev, pager, next, jumper"
                    :total="apiResult.total"></el-pagination>
            </div>
        </div>

        <!-- 删除确认弹窗 -->
        <el-dialog title="取消收藏" :show-close="false" :visible.sync="dialogDeletedVisible" width="20%">
            <span>确定取消收藏【{{ recipe.name }}】？</span>
            <span slot="footer" class="dialog-footer">
                <el-button size="mini" @click="dialogDeletedVisible = false">取消</el-button>
                <el-button size="mini" type="primary" @click="collectionOperation">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
// B站【程序员辰星】原创出品
import AutoInput from "@/components/AutoInput.vue";
import Editor from "@/components/Editor"

export default {
    components: { AutoInput, Editor },
    data() {
        return {
            fileUploadApi: '/api/v1.0/self-health-api/file/upload',
            direction: 'rtl',
            recipe: {},
            dialogControlOperation: true,
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
            flowIndexType: {
                TYPE_1: 2, // 浏览
                TYPE_2: 3, // 点赞
                TYPE_3: 4, // 收藏
                TYPE_4: 5, // 停留
            },
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        async collectionOperation() { // 收藏操作
            try {

                await this.$axios.post('/flow-index/operation', {
                    type: this.flowIndexType.TYPE_3,
                    contentModule: "RECIPE",
                    contentId: this.recipe.id
                });

                this.dialogDeletedVisible = false; // 关闭取消收藏弹窗
                this.fetchFreshData();
                this.$message.success('取消收藏成功');

            } catch (error) {
                console.error('取消收藏失败:', error);
            }
        },
        view(news) {
            window.open(`/recipe-detail?id=${news.id}`);
        },
        onListener(content) {
            this.recipe.content = content;
        },
        handleClose() {
            this.dialogControlOperation = true;
            this.recipe = {};
        },
        listener(text) {
            this.recipeQueryDto.title = text;
            this.fetchFreshData();
        },
        async fetchFreshData() {
            try {
                const { data, total } = await this.$axios.post('/recipe/collectionList', this.recipeQueryDto);
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
        handleDelete(row) {
            this.recipe = { ...row };
            this.dialogDeletedVisible = true;
            this.id = row.id;
        },
    },
};
</script>

<style scoped lang="scss">
.pager {
    margin-block: 20px;
}

.operate-buttons {
    opacity: 0;
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