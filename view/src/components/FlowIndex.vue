<template>
  <div class="operation">
    <div class="block">
      <!-- 点赞 -->
      <div @click="upvoteOperation">
        <i :style="{ color: upvoteStatusFlag ? 'rgb(230, 62, 49)' : 'rgb(51,51,51)' }"
          class="el-icon-s-opportunity"></i>
        {{ upvoteStatusFlag ? '取消点赞' : '点赞' }}
      </div>
      <!-- 收藏 -->
      <div @click="collectionOperation">
        <i :style="{ color: collectionStatusFlag ? 'rgb(230, 62, 49)' : 'rgb(51,51,51)' }" class="el-icon-star-on"></i>
        {{ collectionStatusFlag ? '取消收藏' : '收藏' }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'FlowIndex',
  props: {
    contentModule: {
      type: String,
      required: true
    },
    contentId: {
      type: Number,
      required: true,
    },
  },
  watch: {
    contentId: {
      handler(newVal) {
        if (newVal > 0) {
          this.saveView();
          this.fetchUpvoteStatus();
          this.fetchCollectionStatus();
        }
      },
      deep: true,
      immediate: true
    },
  },
  data() {
    return {
      flowIndexType: {
        TYPE_1: 2, // 浏览
        TYPE_2: 3, // 点赞
        TYPE_3: 4, // 收藏
        TYPE_4: 5, // 停留
      },
      startTime: null,
      isRecordingStay: false,
      upvoteStatusFlag: false,
      collectionStatusFlag: false,
    }
  },
  created() {
    this.initStartTime();
    window.addEventListener('beforeunload', this.recordStayTime);
  },
  beforeDestroy() {
    this.cleanup();
  },
  deactivated() {
    this.recordStayTime();
  },
  methods: {

    async collectionOperation() { // 收藏操作
      try {
        const { contentId, contentModule } = this;

        await this.$axios.post('/flow-index/save', {
          type: this.flowIndexType.TYPE_3,
          contentModule,
          contentId
        });

        this.collectionStatusFlag = !this.collectionStatusFlag;

      } catch (error) {
        console.error('收藏操作失败:', error);
      }
    },

    async upvoteOperation() { // 点赞操作
      try {
        const { contentId, contentModule } = this;

        await this.$axios.post('/flow-index/save', {
          type: this.flowIndexType.TYPE_2,
          contentModule,
          contentId
        });

        this.upvoteStatusFlag = !this.upvoteStatusFlag;

      } catch (error) {
        console.error('点赞操作失败:', error);
      }
    },

    initStartTime() {
      this.startTime = Date.now();
      this.isRecordingStay = false;
    },

    cleanup() {
      this.recordStayTime();
      window.removeEventListener('beforeunload', this.recordStayTime);
    },

    async recordStayTime() {
      if (this.isRecordingStay || !this.startTime) return;

      this.isRecordingStay = true;
      const stayDuration = Date.now() - this.startTime;

      try {
        if (stayDuration > 1000) {
          await this.$axios.post('/flow-index/save', {
            type: this.flowIndexType.TYPE_4,
            contentModule: this.contentModule,
            contentId: this.contentId,
            times: stayDuration
          });
        }
      } catch (error) {
        console.error('停留记录失败:', error);
      } finally {
        this.isRecordingStay = false;
      }
    },

    async fetchFlowIndex(contentId, contentModule, type) {
      try {
        const response = await this.$axios.post('/flow-index/listUser', {
          contentId,
          contentModule,
          type
        });
        return response.code === 200 ? response.data : [];
      } catch (error) {
        console.error('查询失败:', error);
        return [];
      }
    },

    async fetchUpvoteStatus() {
      try {
        const { contentId, contentModule } = this;
        const flowData = await this.fetchFlowIndex(
          contentId,
          contentModule,
          this.flowIndexType.TYPE_2
        );
        this.upvoteStatusFlag = flowData.length > 0;
      } catch (error) {
        console.error('查询点赞记录失败:', error);
      }
    },

    async fetchCollectionStatus() {
      try {
        const { contentId, contentModule } = this;
        const flowData = await this.fetchFlowIndex(
          contentId,
          contentModule,
          this.flowIndexType.TYPE_3
        );
        this.collectionStatusFlag = flowData.length > 0;
      } catch (error) {
        console.error('查询收藏记录失败:', error);
      }
    },

    async saveView() {
      try {
        const { contentId, contentModule } = this;
        const flowData = await this.fetchFlowIndex(
          contentId,
          contentModule,
          this.flowIndexType.TYPE_1
        );

        if (flowData.length === 0) {
          await this.$axios.post('/flow-index/save', {
            type: this.flowIndexType.TYPE_1,
            contentModule,
            contentId
          });
        }
      } catch (error) {
        console.error('浏览记录失败:', error);
      }
    }
  }
};
</script>

<style scoped lang="scss">
.operation {
  display: flex;
  justify-content: center;
  align-items: center;
  padding-block: 20px;
  box-sizing: border-box;

  .block {
    display: flex;
    justify-content: left;
    gap: 20px;

    div {
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 4px;
      font-weight: 600;
      background-color: rgb(246,246,246);

      i {
        font-size: 20px;
      }
    }

    div:hover {
      background-color: rgb(236, 236, 236);
    }

  }
}
</style>