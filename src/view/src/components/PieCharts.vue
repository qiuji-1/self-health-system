<template>
  <div class="line-main" :style="{ backgroundColor: bag }">
    <div ref="chart" :style="{ width: width, height: height }"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: 'PieChart',
  props: {
    types: {
      type: Array,
      default: () => [],
    },
    values: {
      type: Array,
      default: () => [],
    },
    width: {
      type: String,
      default: '100%',
    },
    tag: {
      type: String,
      default: '',
    },
    height: {
      type: String,
      default: '253px',
    },
    bag: {
      type: String,
      default: '#fff',
    },
    fontColor: {
      type: String,
      default: '#333',
    },
    valueFormat: {
      type: String,
      default: '{name}: {value}篇' // 默认格式，可外部传入自定义格式
    },
    tooltipFormat: {
      type: String,
      default: '{name}下共有资讯{value}篇' // 默认提示框格式
    }
  },
  data() {
    return {
      chart: null,
    };
  },
  watch: {
    types() {
      this.initChart();
    },
    values() {
      this.initChart();
    },
  },
  mounted() {
    this.initChart();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose();
    }
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    formatValue(name, value) {
      return this.valueFormat
        .replace('{name}', name)
        .replace('{value}', value);
    },
    formatTooltip(name, value, percent) {
      return this.tooltipFormat
        .replace('{name}', name)
        .replace('{value}', value)
        .replace('{percent}', percent);
    },
    initChart() {
      if (!this.types.length || !this.values.length) return;

      if (this.chart) {
        this.chart.dispose();
      }
      this.chart = echarts.init(this.$refs.chart);

      const option = {
        title: {
          text: this.tag,
          subtext: '',
          left: 'right',
          textStyle: {
            color: this.fontColor,
          },
        },
        tooltip: {
          trigger: 'item',
          formatter: params => {
            return this.formatTooltip(
              params.name,
              params.value,
              params.percent
            );
          }
        },
        legend: {
          type: 'scroll',
          orient: 'horizontal',
          bottom: 0,
          padding: [5, 20],
          itemGap: 10,
          itemWidth: 12,
          itemHeight: 12,
          textStyle: {
            color: this.fontColor,
            fontSize: 12,
          },
          formatter: (name) => {
            const index = this.types.indexOf(name);
            if (index >= 0 && index < this.values.length) {
              return this.formatValue(name, this.values[index]);
            }
            return name;
          },
        },
        series: [
          {
            name: this.tag,
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['50%', '45%'],
            avoidLabelOverlap: true,
            label: {
              show: true,
              position: 'outside',
              formatter: params => {
                return this.formatValue(params.name, params.value);
              },
              color: this.fontColor,
            },
            labelLine: {
              length: 10,
              length2: 10,
              smooth: 0.2,
            },
            emphasis: {
              scale: true,
              scaleSize: 10,
              label: {
                show: true,
                fontSize: 14,
                fontWeight: 'bold',
                formatter: params => {
                  return this.formatValue(params.name, params.value);
                },
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            data: this.types.map((type, index) => ({
              name: type,
              value: this.values[index],
            })),
            itemStyle: {
              borderRadius: 5,
              borderColor: '#fff',
              borderWidth: 2,
              color: (params) => {
                const colorList = [
                  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C',
                  '#909399', '#E4E7ED', '#F2F6FC',
                  '#A0D911', '#722ED1', '#13C2C2', '#FA8C16'
                ];
                return colorList[params.dataIndex % colorList.length];
              },
            },
          },
        ],
        grid: {
          top: '15%',
          bottom: '25%',
          left: '10%',
          right: '10%'
        }
      };

      this.chart.setOption(option);
      this.handleResize();
    },
  },
};
</script>

<style scoped lang="scss">
.line-main {
  padding: 30px 0 0 0;
  margin-bottom: 5px;
  border-radius: 3px;
  background-color: v-bind(bag);

  .tag {
    font-size: 14px;
    text-align: center;
    padding: 15px 6px;
    display: block;
    margin-bottom: 20px;
  }
}
</style>