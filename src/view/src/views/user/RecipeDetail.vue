<template>
    <div class="detail-container">
        <div class="content">
            <div class="title">
                <div>
                    <el-page-header @back="goBack" content="食谱详情">
                    </el-page-header>
                </div>
                <div style="margin-top: 20px;">
                    {{ recipe.name }}
                </div>
                <div class="user-info">
                    <img :src="recipe.avatar" alt="">
                    <div>{{ recipe.username }}</div>
                    <div>发布于{{ recipe.createTime }}</div>
                </div>

            </div>
            <div class="content" v-html="recipe.content"></div>
            <div>
                <FlowIndex :contentId="Number(id)" contentModule="RECIPE" />
            </div>
            <div>
                <Evaluations :userId="userId" :avatar="avatar" contentType="HEALTH-NEWS" :contentId="Number(id)" />
            </div>
        </div>
        <div class="right">
            <h3 style="margin-top: 0;margin-left: 8px;">推荐食谱</h3>
            <div @click="recipeClick(recipe)" class="item" v-for="(recipe, index) in recipeRecommendList" :key="index">
                <img class="cover" :src="recipe.cover" alt="" srcset=""></img>
                <div class="title">{{ recipe.name }}</div>
            </div>
        </div>
    </div>
</template>

<script>
import Evaluations from "@/components/Evaluations"
import FlowIndex from "@/components/FlowIndex"
export default {
    components: { Evaluations, FlowIndex },
    name: "recipeDetail",
    data() {
        return {
            id: null,
            recipe: {},
            userId: 0,
            avatar: '',
            recommendCount: 3,
            recipeRecommendList: []
        }
    },
    async created() {
        await this.fetchUserBaseInfo();
        this.id = this.$router.currentRoute.query.id;
        this.fetchRecipeDetail(this.id);
        this.fetchRecommendRecipe(this.recommendCount);
    },
    methods: {
        recipeClick(recipe){
            this.id = recipe.id;
            this.fetchRecipeDetail(recipe.id);
        },
        goBack() {
            this.$router.push('/user');
        },
        async fetchRecommendRecipe(count) {
            try {
                const { data } = await this.$axios.get(`/recipe/recommend/${count}`);
                this.recipeRecommendList = data;
            } catch (error) {
                this.$message.info(error.message);
            }
        },
        async fetchUserBaseInfo() {
            try {
                const { data } = await this.$axios.get(`/user/auth`);
                this.userId = data.id;
                this.avatar = data.avatar;
            } catch (error) {
                console.error('查询用户信息信息异常:', error);
            }
        },
        async fetchRecipeDetail(id) {
            try {
                const { data } = await this.$axios.get(`/recipe/${id}`);
                this.recipe = data;
            } catch (error) {
                console.error('查询食谱详情信息异常:', error);
            }
        },
    }
}
</script>

<style scoped lang="scss">
.right {
    width: 15%;
    box-sizing: border-box;
    // box-shadow: 0 4px 6px rgb(240, 240, 240);
    padding: 20px;

    .item {
        margin-bottom: 10px;
        cursor: pointer;
        padding: 10px;

        &:hover {
            transform: translateY(-2px);
        }

        .cover {
            width: 200px;
            height: 110px;
            border-radius: 5px;
        }
    }
}

.detail-container {
    display: flex;
    min-height: 100vh;
    padding: 20px 100px;
    box-sizing: border-box;
    gap: 40px;

    .content {
        width:85%;

        .title {
            font-size: 26px;
            font-weight: 600;
            position: sticky;
            top: 0;

            z-index: 100;
            background-color: rgb(255, 255, 255);

            .user-info {
                display: flex;
                margin-bottom: 10px;
                font-weight: 400;
                justify-content: left;
                align-items: center;
                gap: 6px;
                padding-block: 15px;

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
        }

        .time {
            font-size: 16px;
            margin-bottom: 10px;
        }

        .summary {
            background-color: rgb(246, 246, 246);
            padding: 20px;
            box-sizing: border-box;
            font-size: 14px;
            border-radius: 5px;
        }

        .content {
            width: 100%;
        }
    }

    .right {
        width: 40%;
    }
}
</style>