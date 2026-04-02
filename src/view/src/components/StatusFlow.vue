<template>
    <div class="status-flow-container">
        <div class="status-title">预约状态流转记录</div>
        <div v-if="statusRecords.length === 0">
            <el-empty description="暂无状态流转路径"></el-empty>
        </div>
        <el-steps direction="vertical" :active="activeStep" :process-status="processStatus" :space="100">
            <div v-for="(record, index) in statusRecords" :key="'record-' + index" class="status-record">
                <el-step :title="getStatusText(record.originStatus)" :description="formatTime(record.createTime)"
                    :icon="getStatusIcon(record.originStatus)" :status="getStepStatus(record.originStatus)"></el-step>
                <el-step :title="getStatusText(record.newId)" :description="formatTime(record.createTime)"
                    :icon="getStatusIcon(record.newId)" :status="getStepStatus(record.newId)"></el-step>
            </div>
        </el-steps>
    </div>
</template>

<script>
export default {
    props: {
        statusRecords: {
            type: Array,
            default: () => [],
            validator: value => {
                return value.every(record =>
                    record.hasOwnProperty('originStatus') &&
                    record.hasOwnProperty('newId') &&
                    record.hasOwnProperty('createTime')
                )
            }
        }
    },
    data() {
        return {
            statusConfig: {
                1: { text: "预约中", icon: "el-icon-time", color: "#409EFF", status: "process" },
                2: { text: "已预约", icon: "el-icon-success", color: "#67C23A", status: "success" },
                3: { text: "预约失败", icon: "el-icon-warning", color: "#E6A23C", status: "error" },
                4: { text: "已取消", icon: "el-icon-circle-close", color: "#F56C6C", status: "error" },
                5: { text: "已完成", icon: "el-icon-finished", color: "#909399", status: "success" }
            }
        }
    },
    computed: {
        activeStep() {
            return this.statusRecords.length ? this.statusRecords.length * 2 - 1 : 0
        },
        processStatus() {
            if (!this.statusRecords.length) return 'wait'
            const lastStatus = this.statusRecords[this.statusRecords.length - 1].newId
            return this.getStepStatus(lastStatus)
        }
    },
    methods: {
        formatTime(time) {
            if (!time) return '--'
            try {
                const date = new Date(time)
                if (isNaN(date.getTime())) return '无效日期'

                const pad = num => num.toString().padStart(2, '0')
                return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
            } catch (e) {
                console.error('日期格式错误:', e)
                return '无效日期'
            }
        },

        getStatusText(status) {
            return (this.statusConfig[status] && this.statusConfig[status].text) || "未知状态"
        },

        getStatusIcon(status) {
            return (this.statusConfig[status] && this.statusConfig[status].icon) || "el-icon-info"
        },

        getStatusColor(status) {
            return (this.statusConfig[status] && this.statusConfig[status].color) || "#909399"
        },

        getStepStatus(status) {
            return (this.statusConfig[status] && this.statusConfig[status].status) || "wait"
        }
    }
}
</script>

<style scoped lang="scss">
.status-flow-container {
    padding: 20px 0;
    background: #fff;
    border-radius: 8px;
    // box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;

    .status-title {
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 20px;
        color: #333;
        padding-left: 10px;
        border-left: 4px solid #409EFF;
    }

    .status-record {
        margin-bottom: 10px;

        &:last-child {
            margin-bottom: 0;
        }
    }

    ::v-deep {
        .el-step {
            &__head {
                &.is-process {
                    color: #409EFF;
                    border-color: #409EFF;

                    .el-step__icon {
                        background: rgba(64, 158, 255, 0.1);
                    }
                }

                &.is-success {
                    color: #67C23A;
                    border-color: #67C23A;

                    .el-step__icon {
                        background: rgba(103, 194, 58, 0.1);
                    }
                }

                &.is-error {
                    color: #F56C6C;
                    border-color: #F56C6C;

                    .el-step__icon {
                        background: rgba(245, 108, 108, 0.1);
                    }
                }

                &.is-wait {
                    color: #909399;
                    border-color: #909399;
                }
            }

            &__title {
                font-size: 14px;
                font-weight: bold;

                &.is-process {
                    color: #409EFF;
                }

                &.is-success {
                    color: #67C23A;
                }

                &.is-error {
                    color: #F56C6C;
                }

                &.is-wait {
                    color: #909399;
                }
            }

            &__description {
                font-size: 12px;
                color: #999;
            }

            &__line {
                left: 11px !important;
                background: linear-gradient(to bottom,
                        rgba(64, 158, 255, 0.3),
                        rgba(103, 194, 58, 0.3)) !important;
            }
        }
    }
}
</style>