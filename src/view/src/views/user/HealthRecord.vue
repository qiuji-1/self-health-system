<template>
    <div class="detail-container">
        <div class="content">
            <div class="page-header">
                <el-page-header @back="goBack" content="健康记录">
                </el-page-header>
            </div>
            <div>
                <UserHealthModel @selected="modelSelected" />
            </div>
        </div>
        <div class="evaluations">
            <div class="nav-text">
                <div class="left">
                    记录健康
                </div>
                <div class="right" @click="modelList = []">
                    <el-tooltip class="item" effect="dark" content="清空选中项" placement="bottom">
                        <i class="el-icon-s-open"></i>
                    </el-tooltip>
                </div>
            </div>
            <div class="input-area">
                <div v-if="!modelList.length">
                    <el-empty description="请选中模组"></el-empty>
                </div>
                <div class="item-model" v-for="(healthModel, index) in modelList" :key="index">
                    <div class="name">
                        {{ healthModel.name }}
                        <span>
                            <el-tooltip class="item" effect="dark" :content="healthModel.detail" placement="bottom">
                                <i class="el-icon-question"></i>
                            </el-tooltip>
                        </span>
                    </div>
                    <div class="input">
                        <el-input v-model="healthModel.value" placeholder="输入值"></el-input>
                    </div>
                </div>
                <div v-if="modelList.length">
                    <div style="text-align: center;margin-right: 0;margin-left: 0;padding-block: 10px;"
                        class="primary-bt" @click="saveHealthRecord">
                        <i class="el-icon-success"></i>
                        立即新增
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import UserHealthModel from '@/views/user/UserHealthModel';
export default {
    components: { UserHealthModel },
    name: "HealthRecord",
    data() {
        return {
            modelList: [], // 选中的模组项
        }
    },
    async created() {

    },
    methods: {
        async saveHealthRecord() {
            try {
                const healthRecordList = this.modelList.map(healthModel => {
                    return {
                        healthModelId: healthModel.id,
                        value: healthModel.value
                    }
                });
                const { message } = await this.$axios.post(`/health-record/batchSave`, healthRecordList);
                this.$notify.success({
                    title: '健康记录',
                    message: message,
                    duration: 1000,
                    position: 'bottom-right'
                });
                // 回首页
                this.$router.push('/user');
            } catch (error) {
                this.$message.error(error.message);
                console.error('健康记录异常:', error);
            }
            console.log(JSON.stringify(healthRecordList));

        },
        modelSelected(model) {
            if (!this.modelList.includes(model)) {
                this.modelList.push(model);
            } else {
                this.$notify.info({
                    title: '提示',
                    message: '当前模组列表已存在',
                    duration: 1000,
                    position: 'bottom-right'
                })
            }
        },
        goBack() {
            this.$router.push('/user');
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