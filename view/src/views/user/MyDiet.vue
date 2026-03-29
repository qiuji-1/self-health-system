<template>
    <div class="detail-container">
        <div class="content">
            <div class="page-header">
                <el-page-header @back="goBack" content="记录饮食">
                </el-page-header>
            </div>
            <div style="box-shadow: 0 4px 8px rgb(240,240,240);padding: 20px 10px;">
                <UserRecipe @selected="recipeSelected" />
                <UserDietHistory :notifyFlag="notifyFlag"/>
            </div>
        </div>
        <div class="evaluations">
            <div class="nav-text">
                <div class="left">
                    记录饮食
                </div>
                <div class="right" @click="recipeList = []">
                    <el-tooltip class="item" effect="dark" content="清空选中项" placement="bottom">
                        <i class="el-icon-s-open"></i>
                    </el-tooltip>
                </div>
            </div>
            <div class="input-area">
                <div v-if="!recipeList.length">
                    <el-empty description="请选中食谱"></el-empty>
                </div>
                <div class="item-model" v-for="(recipe, index) in recipeList" :key="index">
                    <div class="name">
                        {{ recipe.name }}
                    </div>
                    <div class="input">
                        <el-input v-model="recipe.value" placeholder="输入食用量（单位g）"></el-input>
                    </div>
                </div>
                <div class="item-model" v-if="recipeList.length">
                    <div class="name">
                        饮食备注
                    </div>
                    <div class="input">
                        <el-input type="textarea" :rows="2" placeholder="饮食备注" v-model="detail">
                        </el-input>
                    </div>
                </div>

                <div v-if="recipeList.length">
                    <div style="text-align: center;margin-right: 0;margin-left: 0;padding-block: 10px;"
                        class="primary-bt" @click="saveDiet">
                        <i class="el-icon-success"></i>
                        立即新增
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import UserRecipe from '@/views/user/UserRecipe';
import UserDietHistory from '@/views/user/UserDietHistory';
export default {
    components: { UserRecipe, UserDietHistory },
    name: "HealthRecord",
    data() {
        return {
            recipeList: [], // 选中的模组项
            detail: '',
            notifyFlag: false,
        }
    },
    async created() {

    },
    methods: {
        async saveDiet() {
            try {
                const recipeList = this.recipeList.map(recipe => {
                    return {
                        recipeId: recipe.id,
                        detail: this.detail,
                        value: recipe.value
                    }
                });
                const { message } = await this.$axios.post(`/diet-history/save`, recipeList);
                this.$notify.success({
                    title: '饮食记录',
                    message: message,
                    duration: 1000,
                    position: 'bottom-right'
                });
                this.recipeList = [];
                // 通知用户饮食组件，及时更新数据
                this.notifyFlag = true;
            } catch (error) {
                this.$message.error(error.message);
                console.error('饮食记录异常:', error);
            }
        },
        recipeSelected(recipe) {
            if (!this.recipeList.includes(recipe)) {
                this.recipeList.push(recipe);
            } else {
                this.$notify.info({
                    title: '提示',
                    message: '当前食谱已存在',
                    duration: 1000,
                    position: 'bottom-right'
                })
            }
        },
        goBack() {
            this.$router.push('/user');
        },
    }
}
</script>

<style scoped lang="scss">
.detail-container {
    display: flex;
    min-height: 100vh;
    padding: 10px 20px;
    box-sizing: border-box;
    gap: 20px;

    .content {
        width: 60%;

        .page-header {
            padding: 20px 30px;
        }
    }

    .evaluations {
        width: 40%;
        padding: 20px 30px;

        .nav-text {
            display: flex;
            justify-content: space-between;
            font-size: 18px;

            .right {
                padding: 4px;
                box-sizing: border-box;
                background-color: rgb(245, 245, 245);
                cursor: pointer;
                border-radius: 2px;

                &:hover {
                    background-color: rgb(240, 240, 240);
                }
            }
        }

        .input-area {
            margin-block: 40px;

            .item-model {
                margin-bottom: 20px;

                .name {
                    font-size: 18px;
                }

                .input {
                    margin-block: 10px;
                }
            }
        }

    }

}
</style>