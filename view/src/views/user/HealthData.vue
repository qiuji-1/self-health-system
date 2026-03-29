<template>
    <div class="health-data-container">
        <div class="left">
            <div>
                <div class="top">
                    <div class="detail">
                        <i class="el-icon-info"></i>
                        {{ selectedHealthModel.detail }}
                    </div>
                    <el-select style="min-width: 250px;" @change="selectedModel" size="mini" v-model="healthRecordQueryDto.healthModelId"
                        placeholder="请选择模组">
                        <el-option v-for="(item, index) in modelOptions" :key="index" :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <LineChart @on-selected="onSelected" :tooltipFormatter="customTooltip" :tag="tag" :height="height"
                    :values="values" :date="dateList" />
            </div>
            <UserHealthRecord @listnerModelOptions="listnerModelOptions" />
        </div>
        <div class="right">
            <!-- 健康记录入口 -->
            <div class="nav">
                <div>
                    <div class="text">健康生活，从此刻开始</div>
                    <div class="record" @click="toHealthRecord">去记录<i class="el-icon-arrow-right"></i></div>
                </div>
            </div>
            <!-- BMI值测算 -->
            <div class="nav-block">
                <div class="title">BMI测算</div>
                <el-form ref="bmiForm" :model="bmiForm" label-width="70px">
                    <el-form-item label="身高(cm)">
                        <el-input size="mini" placeholder="输入身高" v-model="bmiForm.height"></el-input>
                    </el-form-item>
                    <el-form-item label="体重(kg)">
                        <el-input size="mini" placeholder="输入体重" v-model="bmiForm.weight"></el-input>
                    </el-form-item>
                </el-form>
                <div v-if="isComputeBMI">
                    <el-progress type="circle" :percentage="BMIResult.result" :color="getBMIColor(BMIResult.info)"
                        :format="formatBMI"></el-progress>
                </div>
                <div>
                    <el-button @click="computeBMI" size="mini" type="primary"
                        icon="el-icon-suitcase">立即测算BMI值</el-button>
                </div>
            </div>

            <!-- 模组统计 -->
            <div class="nav-block">
                <div class="title">模组统计</div>
                <div class="value">
                    <div>
                        <div class="count">{{ modelCount.globalModelCount }}</div>
                        <div class="model">全局模组</div>
                    </div>
                    <div>
                        <div class="count">{{ modelCount.privateModelCount }}</div>
                        <div class="model">我的模组</div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import UserHealthRecord from "@/views/user/UserHealthRecord"
import LineChart from "@/components/LineChart"
export default {
    components: { UserHealthRecord, LineChart },
    name: "HealthData",
    data() {
        return {
            modelCount: {},
            bmiForm: {
                height: 155.5,
                weight: 51.5,
            },
            BMIResult: {},
            isComputeBMI: false,
            values: [],
            dateList: ['2023-01', '2023-02', '2023-03', '2023-04', '2023-05', '2023-06', '2023-07'],
            tag: '折线图',
            height: '400px',
            modelOptions: [],
            selectedHealthModel: {},
            healthRecordQueryDto: {
                days: 365, // 默认查询一年的数据
                healthModelId: null, // 健康模组ID
            },
        }
    },
    created() {
        this.fetchModelCount();
    },
    methods: {
        formatBMI(percentage) {
            return (
                `${this.BMIResult.result}${this.BMIResult.info}`
            );
        },
        getBMIColor(info) {
            const colors = {
                '偏瘦': '#67C23A',
                '正常': '#409EFF',
                '偏胖': '#E6A23C',
                '肥胖': '#F56C6C'
            };
            return colors[info] || '#909399';
        },
        async fetchModelCount() {
            try {
                const { data } = await this.$axios.get(`/health-model/modelCount`);
                this.modelCount = data;
            } catch (error) {
                this.$message.error(error.message);
            }
        },
        async computeBMI() {
            try {
                const { data } = await this.$axios.post(`/health-model/computeBMI`, this.bmiForm);
                this.BMIResult = data;
                this.isComputeBMI = true;
            } catch (error) {
                this.$message.error(error.message);
            }
        },
        toHealthRecord() {
            window.open('/health-record', '_blank');
        },
        selectedModel() {
            const resultModelList = this.modelOptions.filter(model => model.value === this.healthRecordQueryDto.healthModelId);
            this.selectedHealthModel = resultModelList.length > 0 ? resultModelList[0] : {};
            this.tag = this.selectedHealthModel.label;
            this.lineChartListUser();
        },
        listnerModelOptions(data) {
            this.modelOptions = data;
            this.healthRecordQueryDto.healthModelId = data.length > 0 ? data[0].value : null;
            this.selectedModel();
        },
        async lineChartListUser() {
            try {
                const { data } = await this.$axios.post(`/health-record/listLineChart`, this.healthRecordQueryDto);
                this.values = data.map(entity => entity.value);
                this.dateList = data.map(entity => entity.createTime);
            } catch (error) {
                this.$message.info(error.message);
            }
        },
        onSelected(days) {
            this.healthRecordQueryDto.days = days;
            this.lineChartListUser();
        },
        customTooltip(params) {
            return `
                <div style="padding: 5px 10px;">
                    <div>记录于: ${params[0].axisValue}</div>
                    <div>记录值: ${params[0].data}${this.selectedHealthModel.unit}</div>
                </div>`;
        },
    }
}
</script>

<style scoped lang="scss">
.progress-content {
    text-align: center;
}

.bmi-value {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 5px;
}

.bmi-info {
    font-size: 14px;
    color: #666;
}

.health-data-container {
    display: flex;
    gap: 20px;

    .left {
        width: 70%;


        .top {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
            gap: 20px;

            .detail{
                font-size: 14px;
                color: #666;
            }
        }
    }

    .right {
        width: 30%;

        .nav-block {
            // background-color: rgb(246,246,246);
            padding: 20px 16px;
            border-radius: 5px;
            box-shadow: 0 4px 6px rgb(240, 240, 240);

            .title {
                font-size: 20px;
                margin-bottom: 20px;
                font-weight: 600;
                border-bottom: 1px solid rgb(246, 246, 246);
                padding-bottom: 10px;
                color: rgb(51, 51, 51);
            }

            .value {
                display: flex;
                justify-content: space-evenly;
                background-color: rgb(246, 246, 246);
                padding: 10px;
                border-radius: 5px;

                .count {
                    font-size: 24px;
                    font-weight: 600;
                }

                .model {
                    font-size: 14px;
                    color: #666;
                }
            }
        }

        .nav {
            border-radius: 5px;
            background-color: rgb(246, 246, 246);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px 30px;
            box-sizing: border-box;
            margin-bottom: 10px;

            .text {
                font-size: 24px;
                margin-bottom: 12px;
            }

            .record {
                padding: 4px 10px;
                display: inline-block;
                background-color: rgb(51, 51, 51);
                color: rgb(255, 255, 255);
                border-radius: 20px;
                cursor: pointer;

                &:hover {
                    background-color: rgb(31, 31, 31);
                }
            }
        }
    }
}
</style>