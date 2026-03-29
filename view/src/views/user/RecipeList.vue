<template>
    <div class="recipe-container">
        <div class="content">
            <div style="display: flex;justify-content: space-between;align-items: center;">
                <!-- 资讯类别 -->
                <div class="types">
                    <div :style="{
                        backgroundColor: selectedType.value === type.value ? 'rgb(36, 136, 17)' : '',
                        color: selectedType.value === type.value ? 'rgb(255,255,255)' : ''
                    }" @click="typeChange(type)" class="type-item" v-for="(type, index) in recipeTypesList"
                        :key="index">
                        {{ type.label }}
                    </div>
                </div>
                <div style="display: flex;justify-content: flex-end;">
                    <AutoInput style="width: 200px;" placeholder="搜索食谱" @listener="listener" />
                </div>
            </div>

            <!-- 资讯列表 -->
            <div>
                <div class="health-news">
                    <div v-if="!recipeList.length">
                        <el-empty description="暂无食谱信息"></el-empty>
                    </div>
                    <div @click="recipeChange(recipe)" class="item" v-for="(recipe, index) in recipeList" :key="index">
                        <img class="cover" :src="recipe.cover" alt="" srcset="">
                        <div class="user-info">
                            <img :src="recipe.avatar" alt="">
                            <div style="
                                width: 80%;
                                white-space: nowrap;
                                overflow: hidden;
                                text-overflow: ellipsis;
                            ">
                                {{ recipe.username }}
                            </div>
                        </div>
                        <div class="title">{{ recipe.name }}</div>
                    </div>
                </div>
                <div class="pager">
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                        :current-page="recipeQueryDto.current" :page-sizes="[10, 20]" :page-size="recipeQueryDto.size"
                        layout="total, sizes, prev, pager, next, jumper" :total="total"></el-pagination>
                </div>
            </div>
        </div>
        <div class="recommend">
            <div class="nav">
                <div class="text">健康饮食，从此开始</div>
                <div class="record" @click="toDietRecord">去记录饮食<i class="el-icon-arrow-right"></i></div>
            </div>
            <!-- 食譜 -->
            <div class="right">
                <h3 style="margin-top: 0;margin-left: 8px;">推荐食谱</h3>
                <div class="item" @click="recipeChange(recipe)" v-for="(recipe, index) in recipeRecommendList"
                    :key="index">
                    <img class="cover" :src="recipe.cover" alt="" srcset=""></img>
                    <div class="title">{{ recipe.name }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import AutoInput from "@/components/AutoInput.vue"; // 导入封装好的输入框组件
export default {
    components: { AutoInput },
    data() {
        return {
            selectedType: {},
            recipeTypesList: [],
            recipeList: [],
            total: 0,
            recipeQueryDto: {
                typeId: null,
                current: 1,
                size: 10,
            },
            recipeRecommendList: [],
        }
    },
    created() {
        this.fetchRecipeTypes();
        this.fetchRecommendRecipe(2);
    },
    methods: {
        async fetchRecommendRecipe(count) {
            try {
                const { data } = await this.$axios.get(`/recipe/recommend/${count}`);
                this.recipeRecommendList = data;
            } catch (error) {
                this.$message.info(error.message);
            }
        },
        // 输入框组件输入回传
        listener(text) {
            this.recipeQueryDto.name = text; // 赋值查询条件的内容
            this.fetchrecipe(); // 重新加载数据
        },
        toDietRecord() {
            // 跳转至我的饮食页面
            window.open('/my-diet', '_blank');
        },
        recipeChange(recipe) {
            window.open(`/recipe-detail?id=${recipe.id}`);
        },
        handleSizeChange(size) {
            this.recipeQueryDto.size = size;
            this.recipeQueryDto.current = 1;
            this.fetchrecipe();
        },
        handleCurrentChange(current) {
            this.recipeQueryDto.current = current;
            this.fetchrecipe();
        },
        typeChange(type) {
            this.selectedType = type;
            this.recipeQueryDto.typeId = type.value;
            this.fetchrecipe();
        },
        async fetchRecipeTypes() {
            try {
                const { data } = await this.$axios.get('/recipe/fetchRecipeTypeList');
                this.recipeTypesList = data;
                this.recipeTypesList.unshift({ value: null, label: "全部" });
                this.typeChange(this.recipeTypesList[0]);
            } catch (error) {
                this.$message.info(error.message);
            }
        },
        async fetchrecipe() {
            try {
                const { data, total } = await this.$axios.post('/recipe/listRecipe', this.recipeQueryDto);
                this.recipeList = data;
                this.total = total;
            } catch (error) {
                this.$message.info(error.message);
            }
        }
    }
}
</script>

<style scoped lang="scss">
.recipe-container {

    display: flex;
    gap: 20px;

    .recommend {
        width: 20%;

        .nav {
            margin-top: 30px;
            border-radius: 20px;
            background-color: rgb(36, 136, 17);
            align-items: center;
            color: rgb(255, 255, 255);
            justify-content: center;
            padding: 20px 10px;
            box-sizing: border-box;
            margin-bottom: 10px;

            .text {
                font-size: 18px;
                margin-bottom: 12px;
            }

            .record {
                padding: 4px 10px;
                display: inline-block;
                background-color: rgb(51, 51, 51);
                color: rgb(255, 255, 255);
                margin-left: 25px;
                border-radius: 20px;
                cursor: pointer;

                &:hover {
                    background-color: rgb(31, 31, 31);
                }
            }
        }

        .right {
            box-sizing: border-box;
            box-shadow: 0 4px 6px rgb(240, 240, 240);
            padding: 20px;

            .item {
                margin-bottom: 10px;
                cursor: pointer;
                padding: 10px;

                &:hover {
                    transform: translateY(-2px);
                }

                .cover {
                    width: 100%;
                    height: 110px;
                    border-radius: 5px;
                }
            }
        }
    }

    .content {
        width: 80%;
        box-sizing: border-box;

        .pager {
            display: flex;
            justify-content: flex-end;
        }

        .types {
            padding-block: 20px;
            display: flex;
            flex-wrap: wrap;
            gap: 20px;

            .type-item {
                padding: 6px 12px;
                border-radius: 20px;
                cursor: pointer;

                &:hover {
                    background-color: rgb(246, 246, 246);
                }
            }
        }

        .health-news {
            display: flex;
            flex-wrap: wrap;
            padding: 20px 0;
            gap: 15px;
            /* 添加间距 */
        }

        .health-news .item {
            flex: 0 0 calc(20% - 15px);
            /* 每行5个，减去间距 */
            max-width: calc(20% - 15px);
            /* 防止flex布局在某些情况下失效 */
            box-sizing: border-box;
            cursor: pointer;
            transition: transform 0.3s ease;
        }

        .health-news .item:hover {
            transform: translateY(-5px);
        }

        .health-news .user-info {
            display: flex;
            margin-bottom: 10px;
            justify-content: left;
            align-items: center;
            gap: 6px;

            img {
                width: 20px;
                height: 20px;
                border-radius: 50%;
            }

            div {
                font-size: 14px;
                color: #666;
            }
        }

        .health-news .cover {
            width: 100%;
            height: 120px;
            object-fit: cover;
            border-radius: 8px;
            margin-bottom: 10px;
        }

        .health-news .title {
            font-size: 16px;
            font-weight: 500;
            white-space: nowrap;
            /* 不换行 */
            overflow: hidden;
            /* 超出隐藏 */
            text-overflow: ellipsis;
            /* 显示省略号 */
            padding: 0 5px;
        }
    }
}
</style>