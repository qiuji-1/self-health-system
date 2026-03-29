<template>
    <div class="detail-container">
        <div class="content">
            <div class="title">
                <div>
                    <el-page-header @back="goBack" content="详情页面">
                    </el-page-header>
                </div>
                <div style="margin-top: 20px;">
                    {{ healthNews.title }}
                </div>
            </div>
            <div class="time">
                <div>
                    发布于{{ healthNews.createTime }}
                </div>
                <div>
                    {{ healthNews.viewCount }}人阅读
                </div>
                <div>
                    {{ healthNews.upvoteCount }}人点赞
                </div>
                <div>
                    {{ healthNews.collectionCount }}人收藏
                </div>
            </div>
            <div class="summary">{{ healthNews.summary }}</div>
            <div class="content" v-html="healthNews.content"></div>
            <div>
                <FlowIndex :contentId="Number(id)" contentModule="HEALTH_NEWS" />
            </div>
            <div>
                <Evaluations :userId="userId" :avatar="avatar" contentType="HEALTH-NEWS" :contentId="Number(id)" />
            </div>
        </div>
        <div class="right">
            <h3 style="margin-top: 0;margin-left: 8px;">推荐资讯</h3>
            <div @click="healthNewsClick(healthNews)" class="item" v-for="(healthNews, index) in healthNewsList"
                :key="index">
                <img class="cover" :src="healthNews.cover" alt="" srcset=""></img>
                <div class="title">{{ healthNews.title }}</div>
            </div>
        </div>
    </div>
</template>

<script>
import Evaluations from "@/components/Evaluations"
import FlowIndex from "@/components/FlowIndex"
export default {
    components: { Evaluations, FlowIndex },
    name: "HealthNewsDetail",
    data() {
        return {
            id: null,
            healthNews: {},
            userId: 0,
            avatar: '',
            healthNewsList: [],
        }
    },
    async created() {
        await this.fetchUserBaseInfo();
        this.id = this.$router.currentRoute.query.id;
        this.fetchHealthNewsDetail(this.id);
        this.fetchRecommendHealthNews(4);
    },
    methods: {
        healthNewsClick(healthNews) {
            this.id = healthNews.id;
            this.fetchHealthNewsDetail(healthNews.id);
        },
        goBack() {
            this.$router.push('/user');
        },
        async fetchRecommendHealthNews(count) {
            try {
                const { data } = await this.$axios.get(`/health-news/recommend/${count}`);
                this.healthNewsList = data;
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
        async fetchHealthNewsDetail(id) {
            try {
                const { data } = await this.$axios.get(`/health-news/${id}`);
                this.healthNews = data;
            } catch (error) {
                console.error('查询健康资讯信息异常:', error);
            }
        },
    }
}
</script>

<style scoped lang="scss">
.detail-container {
    display: flex;
    min-height: 100vh;
    padding: 20px 100px;
    box-sizing: border-box;
    gap: 60px;

    .content {
        width: 80%;

        .title {
            font-size: 26px;
            font-weight: 600;
            position: sticky;
            top: 0;
            padding-block: 20px;
            z-index: 100;
            background-color: rgb(255, 255, 255);
        }

        .time {
            font-size: 16px;
            margin-bottom: 20px;
            display: flex;
            gap: 10px;
            color: #666;
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
        width: 20%;

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

            .title {
                font-size: 16px;
                width: 200px;
                font-weight: 500;
                white-space: nowrap;
                /* 不换行 */
                overflow: hidden;
                /* 超出隐藏 */
                text-overflow: ellipsis;
            }
        }
    }
}
</style>