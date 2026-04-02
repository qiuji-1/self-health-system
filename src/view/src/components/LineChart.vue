<template>
  <div class="line-main">
    <div class="header">
      <span class="tag">{{ tag }}</span>
      <span class="time-show">
        <span class="top-bar">时间选择</span>
        <Tab :buttons="options" :initialActive="selectedValueItem" @change="handleChange" />
      </span>
    </div>
    <div v-if="date.length === 0" class="empty-container">
      <el-empty description="暂无数据"></el-empty>
    </div>
    <div v-else class="chart-wrapper">
      <div ref="chart" class="chart" :style="{ height: height }"></div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import Tab from "@/components/Tab" // 导入封装好的Tab组件
export default {
  components: { Tab }, // 注册组件
  name: 'DialogLine',
  props: {
    tag: {
      type: String,
      default: '折线图'
    },
    values: {
      type: Array,
      required: true
    },
    date: {
      type: Array,
      required: true
    },
    height: {
      type: String,
      default: '220px'
    },
    tooltipFormatter: {
      type: Function,
      default: (params) => {
        // 默认格式：日期 -> 数值
        return `${params[0].axisValue} -> ${params[0].data}`;
      }
    }
  },
  data() {
    return {
      chart: null,
      options: [
        { value: 7, label: '一周内' },
        { value: 30, label: '一个月内' },
        { value: 90, label: '三个月内' },
        { value: 365, label: '一年内' }
      ],
      selectedValueItem: 365,
      isChartReady: false
    }
  },
  watch: {
    selectedValueItem(newVal) {
      this.$emit('on-selected', newVal);
    },
    values: {
      handler() {
        this.$nextTick(() => {
          this.initChart();
        });
      },
      deep: true
    },
    date: {
      handler() {
        this.$nextTick(() => {
          this.initChart();
        });
      },
      deep: true
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
    });
  },
  methods: {
    handleChange(obj) {
      this.selectedValueItem = obj.value;
    },
    initChart() {
      if (!this.$refs.chart) return;

      if (this.chart) {
        this.chart.dispose();
      }

      try {
        this.chart = echarts.init(this.$refs.chart);
        const option = this.getChartOption();
        this.chart.setOption(option);
        this.isChartReady = true;

        // 添加窗口大小变化监听
        window.addEventListener('resize', this.handleResize);
      } catch (error) {
        console.error('初始化图表失败:', error);
      }
    },

    getChartOption() {
      return {
        grid: {
          left: 50,
          right: 10,
          top: 50,
          borderWidth: 1,
        },
        tooltip: {
          trigger: 'axis',
          formatter: this.tooltipFormatter,
        },
        xAxis: {
          data: this.date,
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            color: 'rgb(51,51,51)',
            fontSize: '14'
          },
        },
        yAxis: {
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: {
            color: 'rgb(51,51,51)',
            fontSize: '14'
          },
        },
        series: [{
          type: 'line',
          smooth: true,
          data: this.values,
          areaStyle: {
            color: 'rgba(144, 191, 237, 0.3)'
          },
          lineStyle: {
            color: '#5B8FF9',
          },
          itemStyle: {
            color: '#5B8FF9',
            borderColor: '#5B8FF9',
            borderWidth: 2
          },
          label: {
            show: true,
            position: 'top',
            color: 'rgb(102, 102, 102)',
          },
        }]
      };
    },

    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    }
  },
  beforeDestroy() {
    // 移除窗口大小变化监听
    window.removeEventListener('resize', this.handleResize);

    if (this.chart) {
      this.chart.dispose();
      this.chart = null;
    }
  }
};
</script>

<style scoped lang="scss">
.line-main {
  width: 100%;
  display: flex;
  flex-direction: column;

  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;

    .tag {
      font-size: 16px;
      font-weight: bold;
      color: #333;
    }

    .time-show {
      display: flex;
      align-items: center;

      .top-bar {
        font-size: 12px;
        margin-right: 10px;
        color: #666;
      }

      .el-select {
        width: 100px;
      }
    }
  }

  .empty-container {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .chart-wrapper {
    width: 100%;
    flex: 1;
    position: relative;

    .chart {
      width: 100%;
    }
  }
}
</style>