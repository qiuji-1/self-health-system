<template>
    <div class="dashbord-container">
        <div class="left">
            <!-- 静态数据 -->
            <div class="static-count">
                <div class="item">
                    <div class="count">{{ staticCount.userCount }}人</div>
                    <div class="label">存量用户</div>
                </div>
                <div class="item">
                    <div class="count">{{ staticCount.modelCount }}套</div>
                    <div class="label">收录模型</div>
                </div>
                <div class="item">
                    <div class="count">{{ staticCount.healthNewsCount }}篇</div>
                    <div class="label">收录资讯</div>
                </div>
                <div class="item">
                    <div class="count">{{ staticCount.recipeCount }}本</div>
                    <div class="label">收录食谱</div>
                </div>
            </div>
            <!-- 模型收录情况 - 折线图 -->
            <div>
                <LineChart :tooltipFormatter="customTooltip" @on-selected="onSelected" :height="lineChartHeight"
                    :tag="lineChartTag" :values="values" :date="dateList" />
            </div>
        </div>
        <div class="right">
            <PieCharts tag="资讯内容占比" value-format="{name}" tooltip-format="【{name}】分类下有{value}篇资讯，占比{percent}%"
                :height="pieHeight" :types="newsTypes" :values="newsValues" />
            <PieCharts tag="食谱内容占比" value-format="{name}" tooltip-format="【{name}】分类下有{value}本食谱，占比{percent}%"
                :height="pieHeight" :types="recipeTypes" :values="recipeValues" />
        </div>
    </div>
</template>

<script>
import LineChart from '@/components/LineChart.vue';
import PieCharts from '@/components/PieCharts.vue';
export default {
    components: { LineChart, PieCharts },
    data() {
        return {
            values: [19, 20],
            dateList: ['8-2', '8-3'],
            lineChartHeight: '430px',
            lineChartTag: '健康模型收录情况',
            staticCount: {},
            days: 365, //默认查询一年的数据
            newsTypes: [],
            newsValues: [],
            pieHeight: '245px',
            recipeTypes: [],
            recipeValues: [],
        }
    },
    created() {
        this.fetchStaticCount();
        this.fetchModelInfo();
        this.fetchNewsContentType();
        this.fetchRecipeContentType();
    },

    methods: {
        customTooltip(params) {
            return `
                <div style="padding: 5px 10px;">
                    <div>${params[0].axisValue}</div>
                    <div>当天收录模型：${params[0].data}（套）</div>
                </div>`;
        },
        onSelected(days) {
            this.days = days;
            this.fetchModelInfo();
        },
        async fetchStaticCount() {
            try {
                const { data } = await this.$axios.get('/dashboard/staticCount');
                this.staticCount = data;
            } catch (error) {
                console.log("仪表盘 - 查询静态数据异常：", error);
            }
        },
        async fetchNewsContentType() {
            try {
                const { data } = await this.$axios.get('/dashboard/newsContentType');
                this.newsTypes = data.map(entity => entity.typeName);
                this.newsValues = data.map(entity => entity.count);
            } catch (error) {
                console.log("仪表盘 - 查询健康资讯内容类型异常：", error);
            }
        },
        async fetchRecipeContentType() {
            try {
                const { data } = await this.$axios.get('/dashboard/recipeContentType');
                this.recipeTypes = data.map(entity => entity.typeName);
                this.recipeValues = data.map(entity => entity.count);
            } catch (error) {
                console.log("仪表盘 - 查询食谱内容类型异常：", error);
            }
        },
        async fetchModelInfo() {
            try {
                const { data } = await this.$axios.get(`/dashboard/modelInfo/${this.days}`);
                this.values = data.map(entity => entity.count);
                this.dateList = data.map(entity => entity.name);
            } catch (error) {
                console.log("折线图 - 查询模型收录情况异常：", error);
            }
        },
    }
};
</script>

<style scoped lang="scss">
.dashbord-container {
    display: flex;
    padding-inline: 35px;
    box-sizing: border-box;
    gap: 30px;
    overflow-x: hidden;

    .left {
        width: 65%;

        .static-count {
            background-color: rgb(250, 250, 250);
            display: flex;
            justify-content: space-evenly;
            padding-block: 20px;
            border-radius: 5px;
            // box-shadow: 0 4px 6px rgb(240, 240, 240);
            margin-bottom: 20px;

            .count {
                font-size: 28px;
                font-weight: 600;
            }

            .label {
                color: #666;
                margin-top: 6px;
            }
        }
    }

    .right {
        width: 35%;
        box-shadow: 0 4px 6px rgb(240,240,240);
        padding-right: 20px;
        box-sizing: border-box;
    }
}
</style>